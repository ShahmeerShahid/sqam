-- Add below your SQL statements. 
-- You can create intermediate views (as needed). Remember to drop these views after you have populated the result tables.
-- You can use the "\i a2.sql" command in psql to execute the SQL commands in this file.

-- Query 1 statements
CREATE VIEW pairs AS
SELECT c1.cid as c1cid, c1.cname c1cname, c2.cid as c2cid, c2.cname c2cname
FROM customer c1, customer c2
WHERE c1.cid != c2.cid;

INSERT INTO Query1 (
SELECT custref as cuid, c1cname as cuname, custid as refid, c2cname as refname
FROM referral, pairs
WHERE custref = c1cid AND custid = c2cid
ORDER BY cuname ASC);

drop view pairs;


-- Query 2 statements
CREATE VIEW unshipped AS
SELECT oid, pid, shipwid, quantity
FROM orders
WHERE status = 'O';

INSERT INTO Query2(
SELECT oid as oid, u.pid as pid, u.shipwid as wid, u.quantity as ordqty, s.quantity as stockqty
FROM unshipped u , stock s
WHERE u.pid = s.pid AND u.shipwid = s.wid AND s.quantity < u.quantity);

drop view unshipped;


-- Query 3 statements
CREATE VIEW shipped AS
SELECT oid, cid, pid, quantity, price
FROM orders
WHERE status = 'S';

CREATE VIEW totals AS
SELECT oid, cid, price*quantity as total
FROM shipped;

INSERT INTO Query3(
SELECT t.cid as cud, c.cname as cuname, sum(total) as totalsales
FROM totals t, customer c
WHERE t.cid = c.cid
GROUP BY t.cid, c.cname
ORDER BY totalsales desc);

drop view shipped, totals;


-- Query 4 statements
CREATE VIEW shipped as
SELECT pid, quantity
FROM orders
WHERE status = 'S';

INSERT INTO query4(
SELECT s.pid as pid, p.pname as pname, p.cost*s.quantity as totalcost
FROM shipped s, product p
WHERE s.pid = p.pid
GROUP BY s.pid, p.pname, p.cost, s.quantity
ORDER BY totalcost ASC);

drop view shipped;


-- Query 5 statements
CREATE VIEW never AS
SELECT pid
FROM product
EXCEPT
SELECT pid
FROM orders;

INSERT INTO query5(
SELECT p.pid as pid, p.pname as pname, p.introdate as introdate
FROM never n, product p
WHERE p.pid = n.pid
ORDER BY p.pname ASC);

drop view never;



-- Query 6 statements
CREATE VIEW never AS
SELECT cid
FROM customer
EXCEPT
SELECT cid
FROM orders;

CREATE VIEW nevercustomer AS
SELECT c.cid as cid, c.cname as cname, c.lid as lid
FROM never n, customer c
WHERE c.cid = n.cid;

INSERT INTO query6(
SELECT n.cid as cid, n.cname as cname, l.lname as locname
FROM nevercustomer n, location l
WHERE n.lid = l.lid
ORDER BY n.cname);

drop view never, nevercustomer;


-- Query 7 statements

CREATE VIEW dateconvert as
SELECT oid, cid, pid, to_char(odate, 'YYYYMM') as odate, shipwid, quantity, price, status
FROM orders
WHERE status = 'S';

CREATE VIEW sales as
SELECT odate, sum(d.price*d.quantity) as sale
FROM dateconvert d
GROUP BY odate;

CREATE VIEW costs as
SELECT odate, sum(p.cost*d.quantity) as cost
FROM dateconvert d, product p
WHERE d.pid = p.pid
GROUP BY d.odate;

INSERT INTO query7(
SELECT cast (s.odate as integer) as period, s.sale as sales, c.cost as costs
FROM sales s, costs c
WHERE s.odate = c.odate
GROUP BY s.odate, s.sale, c.cost
ORDER BY s.odate ASC);


drop view dateconvert, sales, costs;

-- Query 8 statements
CREATE VIEW madepurchases as
SELECT r.custid as custid, sum(r.commission*(o.price*o.quantity)) as commission 
FROM referral r, orders o
WHERE r.custref = o.cid
GROUP BY r.custid, o.quantity, o.price, r.commission;

INSERT INTO query8(
SELECT m.custid as cid, c.cname as cname, sum(m.commission) as commission
FROM madepurchases m, customer c
WHERE m.custid = c.cid 
GROUP BY m.custid, c.cname
ORDER BY c.cname ASC);

drop view madepurchases;


-- Query 9 statements
CREATE VIEW intro as
SELECT pid, introdate
FROM product 
WHERE introdate <= '31 Dec 2015';

CREATE VIEW shipped as
SELECT oid, pid, quantity, price
FROM orders
WHERE status = 'S';

INSERT INTO query9(
SELECT i.pid as pid, i.introdate as date, (s.quantity*s.price) as totalsales
FROM intro i, shipped s
WHERE i.pid = s.pid
GROUP BY i.pid, i.introdate, s.quantity, s.price
ORDER BY i.introdate ASC);

drop view intro, shipped;


-- Query 10 statements
CREATE VIEW shipped as
SELECT oid, shipwid, quantity, price
FROM orders
WHERE status = 'S';

CREATE VIEW allwarehouse as
SELECT w.lid as lid, l.lname as lname, w.wid as wid
FROM warehouse w, location l
WHERE w.lid = l.lid;

CREATE VIEW nosales as
SELECT wid
FROM allwarehouse
EXCEPT
SELECT shipwid as wid
FROM shipped;

CREATE VIEW nosalesloc as
SELECT w.lid as lid
FROM nosales n, warehouse w
WHERE n.wid = w.wid;

CREATE VIEW nosaleslocfull as
SELECT n.lid as lid, l.lname as lname, 0 as totalsales
FROM nosalesloc n, location l
WHERE n.lid = l.lid;

CREATE VIEW shippedloc as 
SELECT a.lid as lid, a.lname as lname, s.quantity*s.price as totalsales 
FROM shipped s, allwarehouse a
WHERE s.shipwid = a.wid
GROUP BY a.lid, a.lname, s.quantity, s.price;

CREATE VIEW together as
SELECT * FROM nosaleslocfull
UNION
SELECT * FROM shippedloc;

insert into query10(
SELECT *
FROM together
ORDER BY lname ASC); 


drop view shipped, allwarehouse, nosales, nosalesloc, nosaleslocfull, shippedloc CASCADE;

