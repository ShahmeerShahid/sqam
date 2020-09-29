-- Add below your SQL statements. 
-- You can create intermediate views (as needed). Remember to drop these views after you have populated the result tables.
-- You can use the "\i a2.sql" command in psql to execute the SQL commands in this file.

-- Query 1 statements

CREATE VIEW join1 AS (SELECT rf.custref AS cuid, rf.custid AS refid, cu.cname AS refname FROM referral rf JOIN customer cu ON cu.cid = custid);

INSERT INTO QUERY1 (SELECT j1.cuid, cu.cname AS cuname, j1.refid, j1.refname FROM join1 j1 JOIN customer cu ON j1.cuid = cu.cid ORDER BY cu.cname ASC); 

DROP VIEW join1;
-- Query 2 statements

INSERT INTO QUERY2 (
SELECT ord.oid, ord.pid, st.wid, ord.quantity AS ordqty, st.quantity AS stockqty 
FROM orders ord JOIN stock st ON ord.pid = st.pid and ord.shipwid = st.wid 
WHERE ord.quantity > st.quantity AND ord.status = 'O');

-- Query 3 statements

INSERT INTO QUERY3 (
SELECT cu.cid AS cuid, cu.cname AS cuname, SUM(quantity*price) AS totalsales 
FROM customer cu JOIN orders ord ON cu.cid = ord.cid 
WHERE ord.status = 'S' 
GROUP BY cu.cid HAVING SUM(quantity*price) > 0 
ORDER BY totalsales DESC);

-- Query 4 statements

INSERT INTO QUERY4 (
SELECT pr.pid, pr.pname, SUM(ord.quantity*pr.cost) AS totalcost 
FROM product pr JOIN orders ord ON pr.pid = ord.pid 
WHERE ord.status = 'S' 
GROUP BY pr.pid HAVING SUM(ord.quantity*pr.cost) > 0 
ORDER BY totalcost ASC);

-- Query 5 statements

CREATE VIEW ordered AS (SELECT DISTINCT orders.pid FROM orders);
CREATE VIEW difference AS ((SELECT product.pid FROM product) EXCEPT (SELECT * FROM ordered));

INSERT INTO QUERY5 (SELECT product.pid, product.pname, product.introdate FROM difference JOIN product ON difference.pid = product.pid ORDER BY pname ASC);

DROP VIEW difference;
DROP VIEW ordered;
-- Query 6 statements

CREATE VIEW cusordered AS (SELECT DISTINCT orders.cid FROM orders);
CREATE VIEW difference AS ((SELECT customer.cid FROM customer) EXCEPT (SELECT * FROM cusordered));
CREATE VIEW loctransfer AS (SELECT customer.cid, customer.cname, location.lname FROM customer join location ON customer.lid = location.lid);

INSERT INTO QUERY6 (SELECT loctransfer.cid, loctransfer.cname, loctransfer.lname AS locname FROM difference JOIN loctransfer ON difference.cid = loctransfer.cid ORDER BY cname ASC);

DROP VIEW difference;
DROP VIEW cusordered;
DROP VIEW loctransfer;

-- Query 7 statements

CREATE VIEW costs AS (
SELECT ord.quantity, ord.price, pr.cost, ord.odate 
FROM orders ord JOIN product pr ON ord.pid = pr.pid 
WHERE ord.status = 'S');

CREATE VIEW periods AS (
SELECT c.quantity, c.price, c.cost, to_char(c.odate, 'yyyymm') AS period 
FROM costs c);

INSERT INTO QUERY7 (
SELECT CAST(period AS INTEGER), SUM(p.quantity * p.price) AS sales, SUM(p.quantity * p.cost) 
FROM periods p 
GROUP BY period HAVING SUM(p.quantity * p.price) > 0 
ORDER BY period ASC);

DROP VIEW periods;
DROP VIEW costs;

-- Query 8 statements

CREATE VIEW temp AS (
SELECT ref.custid AS cid, SUM(ord.price * ord.quantity * ref.commission) AS commission 
FROM referral ref JOIN ORDERS ord ON ref.custref = ord.cid 
GROUP BY ref.custid HAVING SUM(ord.price * ord.quantity * ref.commission) > 0); 

INSERT INTO QUERY8 (SELECT temp.cid, c.cname, temp.commission FROM customer c JOIN temp ON c.cid = temp.cid ORDER BY c.cname ASC);

DROP VIEW temp; 

-- Query 9 statements


CREATE VIEW intro AS (SELECT pro.pid, pro.introdate FROM product pro WHERE pro.introdate <= '2015-12-31');

INSERT INTO QUERY9 (
SELECT intro.pid, intro.introdate, SUM(ord.quantity*ord.price) AS totalcost 
FROM orders ord JOIN intro on intro.pid = ord.pid 
WHERE status = 'S' 
GROUP BY intro.pid, intro.introdate HAVING SUM(ord.quantity*ord.price) > 0 
ORDER BY intro.introdate ASC);

DROP VIEW intro;

-- Query 10 statements

CREATE VIEW v AS (
SELECT w.lid, o.shipwid, COALESCE(SUM(quantity * price), 0) AS totalsales
FROM warehouse w FULL OUTER JOIN orders o ON w.wid = o.shipwid
GROUP BY o.shipwid, w.lid);

INSERT INTO QUERY10 (
SELECT l.lid, l.lname, SUM(v.totalsales)
FROM location l join v ON l.lid = v.lid
GROUP BY l.lid, lname
ORDER BY l.lname ASC);

DROP VIEW v;





