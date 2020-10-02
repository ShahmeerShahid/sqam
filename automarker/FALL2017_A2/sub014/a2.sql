-- Add below your SQL statements. 
-- You can create intermediate views (as needed). Remember to drop these views after you have populated the result tables.
-- You can use the "\i a2.sql" command in psql to execute the SQL commands in this file.

-- Query 1 statements
INSERT INTO Query1
(SELECT C.cid as "cuid", C.cname as "cuname", C2.cid as "refid", C2.cname as "refname"
FROM Customer AS C, Customer AS C2, referral AS R
WHERE C.cid = R.custid AND C2.cid = R.custref
ORDER BY cuname ASC);

-- Query 2 statements
INSERT INTO Query2
(SELECT oid, stock.pid AS "pid", wid, orders.quantity AS "ordqty", stock.quantity AS "stockqty"
FROM stock,orders
WHERE stock.pid = orders.pid AND stock.quantity<orders.quantity);

-- Query 3 statements
INSERT INTO Query3
(SELECT orders.cid as cuid, cname, sum(quantity*price) as totalsales
FROM orders, customer 
WHERE status = 'S' AND orders.cid = customer.cid
GROUP BY  orders.cid, cname
ORDER BY totalsales DESC
);

-- Query 4 statements
INSERT INTO Query4
(SELECT p.pid , pname , SUM(o.quantity * cost) AS totalcost
FROM product AS p, orders as o
WHERE p.pid = o.pid AND status = 'S'
GROUP BY p.pid
ORDER BY totalcost);

-- Query 5 statements
INSERT INTO Query5
(SELECT pid, pname, introdate
FROM product 
WHERE pid NOT IN (select distinct pid from orders)
ORDER BY pname);

-- Query 6 statements
INSERT INTO Query6
(SELECT cid, cname, lname
FROM customer, location
WHERE customer.lid = location.lid AND
	cid NOT IN (
        	SELECT DISTINCT cid
        	FROM orders)
ORDER BY cname);
-- Query 7 statements
INSERT INTO Query7(
SELECT to_char(odate, 'YYYYMM')::INTEGER as period, count(status) AS sales,
 SUM(quantity * cost) AS cost
FROM product AS p, orders AS o
WHERE p.pid = o.pid AND status = 'S'
GROUP BY period
ORDER BY period
);

-- Query 8 statements
INSERT INTO Query8
(SELECT r.custid AS cid, c.cname AS cname, sum(r.commission * price * o.quantity) AS commission
FROM  referral AS r, customer AS c, orders AS o
WHERE r.custid = c.cid AND r.custref = o.cid
GROUP BY r.custid, c.cname
ORDER BY c.cname);




-- Query 9 statements
CREATE VIEW  oldproducts as 
	(SELECT pid, introdate                                                                            
		FROM product                           
		WHERE to_char(introdate, 'YYYYMMDD')::INTEGER < 20160101);

INSERT INTO Query9 (
SELECT p.pid as pid, p.introdate as date, SUM(o.quantity * price) AS totalsales
FROM oldproducts AS p, orders as o
WHERE p.pid = o.pid AND status = 'S'
GROUP BY p.pid, p.introdate
ORDER BY p.introdate ASC);

DROP VIEW IF EXISTS oldproducts;
-- Query 10 statements
Create View fts AS(
SELECT shipwid, sum(quantity * price) AS totalsales 
FROM orders WHERE status = 'S' GROUP BY shipwid );


Create View salesNotZero AS (
SELECT l.lid, l.lname, sum(fts.totalsales)
FROM fts, location AS l, warehouse AS w
WHERE w.wid = fts.shipwid AND w.lid = l.lid
GROUP BY l.lid);

Create View ZeroSales AS(
SELECT lid, lname, 0 AS totalsales
FROM location
WHERE lid NOT IN (SELECT lid from salesNotZero));

Create View sales AS (
SELECT l.lid, l.lname, sum(fts.totalsales)
FROM fts, location AS l, warehouse AS w
WHERE w.wid = fts.shipwid AND w.lid = l.lid
GROUP BY l.lid
union all
SELECT lid, lname, 0 AS totalsales
FROM location
WHERE lid NOT IN (SELECT lid FROM salesNotZero));

INSERT INTO Query10 (SELECT DISTINCT * FROM sales ORDER BY lname);


DROP VIEW IF EXISTS sales; 
DROP VIEW IF EXISTS ZeroSales; 
DROP VIEW IF EXISTS salesNotZero;
DROP VIEW IF EXISTS fts;
