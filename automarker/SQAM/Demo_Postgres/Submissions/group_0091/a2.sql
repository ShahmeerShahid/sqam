-- Add below your SQL statements. 
-- You can create intermediate views (as needed). Remember to drop these views after you have populated the result tables.
-- You can use the "\i a2.sql" command in psql to execute the SQL commands in this file.

-- START Query 1

SELECT cu.cuid AS cuid, cu.cuname AS cuname, cid AS refid, cname AS refname
FROM customer, (SELECT cid AS cuid, cname AS cuname, custid
		FROM customer , referral 
		WHERE cid = custref) as cu
WHERE cid = custid
ORDER BY cuname ASC;

-- END Query 1
-- START Query 2

SELECT o.oid AS oid, s.pid AS pid, s.wid AS wid,
	CAST(o.quantity as NUMERIC(10,2)) AS ordqty,
	CAST(s.quantity as NUMERIC(10,2)) AS stockqty
FROM orders o, stock s
WHERE o.shipwid = s.wid AND o.quantity > s.quantity AND o.status = 'O';

-- END Query 2
-- START Query 3

SELECT c.cid AS cuid, c.cname AS cuname, 
	CAST(SUM(o.quantity * o.price) as NUMERIC(12,2)) AS totalsales 
FROM customer c, orders o
WHERE c.cid = o.cid AND o.status = 'S'
GROUP BY c.cid, c.cname
ORDER BY totalsales DESC;

-- END Query 3
-- START Query 4

SELECT p.pid AS pid, p.pname AS pname,
	CAST(SUM(p.cost * o.quantity) as NUMERIC(12,2)) AS totalcost
FROM product p, orders o
WHERE o.status = 'S' AND p.pid = o.pid
GROUP by p.pid
ORDER BY totalcost ASC;

-- END Query 4
-- START Query 5

SELECT pid, pname, introdate
FROM product 
WHERE pid <> ANY (SELECT DISTINCT pid FROM orders)
ORDER BY pname ASC;

-- END Query 5
-- START Query 6

SELECT c.cid as cid, c.cname cname, l.lname AS locname
FROM customer c, location l
WHERE cname <> ANY (SELECT DISTINCT cname FROM customer NATURAL JOIN orders)
	AND c.lid = l.lid
ORDER BY c.cname ASC;

-- END Query 6
-- START Query 7

SELECT CAST(to_char(odate,'YYYYMM') as INTEGER) AS period,
	CAST(SUM(quantity * price) as NUMERIC(10,2)) AS sales,
	CAST(SUM(quantity * cost) as NUMERIC(10,2)) AS cost
FROM orders NATURAL JOIN product
GROUP BY CAST(to_char(odate,'YYYYMM') as INTEGER)
ORDER BY period ASC;

-- END Query 7
-- START Query 8

SELECT c.cid AS cid, c.cname AS cname,
	CAST(SUM(o.quantity * o.price * commission) as NUMERIC(10,2)) AS commission
FROM customer c, orders o, referral
WHERE c.cid = custid AND custref = o.cid
GROUP BY c.cid, c.cname
ORDER BY cname ASC;

-- END Query 8
-- START Query 9

SELECT pid, introdate AS date,
	CAST(SUM(quantity * price) as NUMERIC(12,2)) AS totalsales
FROM product NATURAL JOIN orders
WHERE introdate > '2015-12-31' AND status = 'S'
GROUP BY pid, introdate
ORDER BY date ASC;

-- END Query 9
-- START Query 10

SELECT lid, lname,
	CAST(SUM(quantity * price) as NUMERIC(12,2)) AS totalsales
FROM orders NATURAL JOIN customer NATURAL JOIN location
WHERE status = 'S'
GROUP BY lid, lname
ORDER BY lname ASC;

-- END Query 10
