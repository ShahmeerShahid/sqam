-- Add below your SQL statements. 
-- You can create intermediate views (as needed). Remember to drop these views after you have populated the result tables.
-- You can use the "\i a2.sql" command in psql to execute the SQL commands in this file.

-- Query 1 statements
INSERT INTO Query1(cuid, cuname, refid, refname)
SELECT r.custref AS cuid, c1.cname AS cuname, r.custid AS refid, c2.cname AS refname
FROM referral AS r, customer AS c1, customer AS c2
WHERE r.custid = c2.cid AND r.custref = c1.cid
ORDER BY cuname ASC;


-- Query 2 statements
INSERT INTO Query2(oid, pid, wid, ordqty, stockqty)
SELECT o.oid AS oid, o.pid AS pid, o.shipwid AS wid, CAST(o.quantity as NUMERIC(10,2)) AS "ordqty", CAST(s.quantity as NUMERIC(10,2)) AS stockqty
FROM orders AS o, stock AS s
WHERE o.pid = s.pid AND o.status = 'O' AND  o.shipwid = s.wid AND s.quantity < o.quantity;

-- Query 3 statements
INSERT INTO Query3(cuid, cuname, totalsales)
SELECT c.cid AS cuid, c.cname AS cuname, CAST(SUM(price*quantity) AS NUMERIC(12, 2)) AS "totalsales"
FROM customer AS c, orders AS o
WHERE c.cid = o.cid AND o.status = 'S'
GROUP BY c.cid
ORDER BY "totalsales" DESC;



-- Query 4 statements
INSERT INTO Query4(pid, pname, totalcost)
SELECT p.pid AS pid, p.pname AS pname, CAST(SUM(o.quantity*p.cost) as NUMERIC(12,2)) AS totalcost
FROM orders AS o, product as p
WHERE o.pid = p.pid AND o.status = 'S'
GROUP BY p.pid
ORDER BY totalcost ASC;

-- Query 5 statements
INSERT INTO Query5(pid, pname, introdate)
SELECT pid, pname, introdate
FROM product as p
WHERE pid NOT IN  (SELECT pid FROM orders)
ORDER BY pname ASC;


-- Query 6 statements
INSERT INTO Query6(cid, cname, locname)
SELECT c.cid AS cid, c.cname AS cname, l.lname AS locname
FROM customer AS c NATURAL JOIN location as l
WHERE c.cid NOT IN (SELECT cid FROM orders)
ORDER BY c.cname ASC;


-- Query 7 statements
INSERT INTO Query7(period, sales, cost)
SELECT CAST(o.period as INTEGER) AS period, CAST(SUM(o.quantity * o.price) as NUMERIC(10,2)) AS sales,  CAST(SUM(o.quantity*p.cost) as NUMERIC(10,2)) AS cost
FROM  (SELECT *, TO_CHAR(odate, 'YYYYMM') as period 
FROM orders) AS o,  product AS p
WHERE o.pid = p.pid AND o.status = 'S'
GROUP BY o.period
ORDER BY  o.period ASC;


-- Query 8 statements
INSERT INTO Query8(cid, cname, comission)
SELECT custid AS cid, c.cname AS cname, CAST(SUM(commission*quantity*price/100) AS NUMERIC(10,2)) AS comission 
FROM (SELECT *
FROM orders AS o, referral AS r
WHERE o.cid = r.custref) AS o, customer AS c
WHERE c.cid = custid
GROUP BY o.custid, c.cname
ORDER BY cname ASC; 


-- Query 9 statements
INSERT INTO Query9(pid, date, totalsales)
SELECT p.pid AS pid, p.introdate AS date, CAST(SUM(o.price*o.quantity) AS NUMERIC(10, 2)) AS "totalsales"
FROM product AS p, orders AS o
WHERE o.status = 'S' AND o.pid = p.pid AND p.introdate <= '2015-12-31'
GROUP BY p.pid
ORDER BY date ASC;


-- Query 10 statements
INSERT INTO Query10(lid, lname, totalsales)
SELECT l.lid, l.lname, CAST(SUM(o.price*o.quantity) AS NUMERIC(12, 2)) AS "totalsales"
FROM location AS l, orders AS o, warehouse AS w
WHERE o.status = 'S' AND o.shipwid = w.wid AND l.lid = w.lid
GROUP BY l.lid
ORDER BY l.lname ASC;
