-- Add below your SQL statements. 
-- You can create intermediate views (as needed). Remember to drop these views after you have populated the result tables.
-- You can use the "\i a2.sql" command in psql to execute the SQL commands in this file.

-- START Query 1


SELECT c1.cid AS cuid, c1.cname AS cuname, c2.cid AS refid, c2.cname AS refname 
FROM referral as r JOIN customer AS c1 ON  r.custid = c1.cid JOIN customer AS c2 ON r.custref = c2.cid 
ORDER BY c1.cname;

-- END Query 1
-- START Query 2


SELECT o.oid, o.pid, o.shipwid AS wid, o.quantity AS ordqty, s.quantity AS stockqty 
FROM orders o, stock s 
WHERE o.status = 'O' AND o.pid = s.pid AND o.shipwid = s.wid AND o.quantity > s.quantity;

-- END Query 2
-- START Query 3


SELECT c.cid AS cuid, c.cname AS cuname, SUM(o.quantity * o.price) AS totalsales 
FROM orders AS o JOIN customer AS c ON o.cid = c.cid 
WHERE o.status = 'S' 
GROUP BY c.cid
ORDER BY totalsales DESC;

-- END Query 3
-- START Query 4


SELECT p.pid as pid, p.pname as pname, SUM(o.quantity * p.cost) AS totalcost 
FROM orders AS o JOIN product as p ON o.pid = p.pid 
WHERE o.status = 'S'
GROUP BY p.pid
ORDER BY totalcost ASC;

-- END Query 4
-- START Query 5


SELECT p.pid AS pid, p.pname as pame, p.introdate AS introdate 
FROM product AS p 
WHERE p.pid NOT IN (SELECT pid FROM orders) 
ORDER By pname;

-- END Query 5
-- START Query 6


SELECT c.cid, cname, lname AS locname 
FROM customer c, location l 
WHERE c.cid NOT IN (SELECT cid FROM orders) AND c.lid = l.lid 
ORDER BY cname ASC;

-- END Query 6
-- START Query 7


SELECT CAST(to_char(odate,'yyyymm') AS INTEGER) as period, SUM(o.quantity * o.price) AS sales, SUM(o.quantity * p.cost) AS cost 
FROM orders o, product p
WHERE o.status = 'S' AND o.pid = p.pid
GROUP BY period 
ORDER BY period ASC;

-- END Query 7
-- START Query 8


SELECT c.cid AS cid, c.cname AS cname, SUM(r.commission * o.quantity * o.price) AS commission 
FROM referral as r, customer AS c, orders AS o 
WHERE r.custref = o.cid AND r.custid = c.cid 
GROUP BY c.cid 
ORDER BY c.cname ASC;

-- END Query 8
-- START Query 9


SELECT p.pid AS pid, p.introdate AS date, SUM(o.quantity * o.price) AS totalsales 
FROM orders o, product p 
WHERE o.status = 'S' AND p.introdate <= '2015-12-31' AND o.pid = p.pid 
GROUP by p.pid 
ORDER BY date ASC;

-- END Query 9
-- START Query 10

INSERT INTO Query10 (

CREATE VIEW warehouse_orders 
AS SELECT l.lid, o.quantity AS quantity, o.price AS price 
FROM orders o, warehouse w, location l 
WHERE o.status = 'S' AND o.shipwid = w.wid AND w.lid = l.lid;

SELECT l.lid as lid, l.lname as lname, 
CASE 
WHEN SUM(wo.quantity * wo.price) IS NULL THEN 0
ELSE SUM(wo.quantity * wo.price) 
END AS totalsales
FROM location l LEFT JOIN warehouse_orders wo ON l.lid = wo.lid 
GROUP BY l.lid 
ORDER BY l.lname ASC;
DROP VIEW warehouse_orders);
-- END Query 10
