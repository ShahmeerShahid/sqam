-- Add below your SQL statements. 
-- You can create intermediate views (as needed). Remember to drop these views after you have populated the result tables.
-- You can use the "\i a2.sql" command in psql to execute the SQL commands in this file.

-- START Query 1
SELECT r.custref AS cuid, c1.cname AS cuname, r.custid AS refid, c2.cname AS refname
FROM referral AS r, customer AS c1, customer AS c2
WHERE r.custid = c2.cid AND r.custref = c1.cid
ORDER BY cuname ASC;


-- END Query 1
-- START Query 2
SELECT o.oid AS oid, o.pid AS pid, o.shipwid AS wid, CAST(o.quantity as NUMERIC(10,2)) AS "ordqty", CAST(s.quantity as NUMERIC(10,2)) AS stockqty
FROM orders AS o, stock AS s
WHERE o.pid = s.pid AND o.status = 'O' AND  o.shipwid = s.wid AND s.quantity < o.quantity;

-- END Query 2
-- START Query 3
SELECT c.cid AS cuid, c.cname AS cuname, CAST(SUM(price*quantity) AS NUMERIC(12, 2)) AS "totalsales"
FROM customer AS c, orders AS o
WHERE c.cid = o.cid AND o.status = 'S'
GROUP BY c.cid
ORDER BY "totalsales" DESC;



-- END Query 3
-- START Query 4
SELECT p.pid AS pid, p.pname AS pname, CAST(SUM(o.quantity*p.cost) as NUMERIC(12,2)) AS totalcost
FROM orders AS o, product as p
WHERE o.pid = p.pid AND o.status = 'S'
GROUP BY p.pid
ORDER BY totalcost ASC;

-- END Query 4
-- START Query 5
SELECT pid, pname, introdate
FROM product as p
WHERE pid NOT IN  (SELECT pid FROM orders)
ORDER BY pname ASC;


-- END Query 5
-- START Query 6
SELECT c.cid AS cid, c.cname AS cname, l.lname AS locname
FROM customer AS c NATURAL JOIN location as l
WHERE c.cid NOT IN (SELECT cid FROM orders)
ORDER BY c.cname ASC;


-- END Query 6
-- START Query 7
SELECT CAST(o.period as INTEGER) AS period, CAST(SUM(o.quantity * o.price) as NUMERIC(10,2)) AS sales,  CAST(SUM(o.quantity*p.cost) as NUMERIC(10,2)) AS cost
FROM  (SELECT *, TO_CHAR(odate, 'YYYYMM') as period 
FROM orders) AS o,  product AS p
WHERE o.pid = p.pid AND o.status = 'S'
GROUP BY o.period
ORDER BY  o.period ASC;


-- END Query 7
-- START Query 8
SELECT custid AS cid, c.cname AS cname, CAST(SUM(commission*quantity*price/100) AS NUMERIC(10,2)) AS comission
FROM (SELECT *
FROM orders AS o, referral AS r
WHERE o.cid = r.custref) AS o, customer AS c
WHERE c.cid = custid
GROUP BY o.custid, c.cname
ORDER BY cname ASC; 


-- END Query 8
-- START Query 9
SELECT p.pid AS pid, p.introdate AS date, CAST(SUM(o.price*o.quantity) AS NUMERIC(10, 2)) AS "totalsales"
FROM product AS p, orders AS o
WHERE o.status = 'S' AND o.pid = p.pid AND p.introdate <= '2015-12-31'
GROUP BY p.pid
ORDER BY date ASC;


-- END Query 9
-- START Query 10
SELECT l.lid, l.lname, CAST(SUM(o.price*o.quantity) AS NUMERIC(12, 2)) AS "totalsales"
FROM location AS l, orders AS o, warehouse AS w
WHERE o.status = 'S' AND o.shipwid = w.wid AND l.lid = w.lid
GROUP BY l.lid
ORDER BY l.lname ASC;
-- END Query 10
