-- Add below your SQL statements. 
-- You can create intermediate views (as needed). Remember to drop these views after you have populated the result tables.
-- You can use the "\i a2.sql" command in psql to execute the SQL commands in this file.

-- Query 1 statements

SELECT c2.cid as cuid, c2.cname as cuname, r.custid as refid, c1.cname as refname
FROM customer c1, customer c2, referral r
WHERE c1.cid = r.custid AND r.custref = c2.cid
ORDER BY c2.cname ASC;

-- Query 2 statements

SELECT o.oid, o.pid, o.shipwid AS wid, CAST(o.quantity AS NUMERIC(10,2)) AS ordqty, CAST(s.quantity AS NUMERIC(10,2)) AS stockqty
FROM stock s, orders o
WHERE o.shipwid = s.wid AND o.pid = s.pid AND o.quantity > s.quantity AND o.status = 'O';

-- Query 3 statements

SELECT c.cid AS cuid, c.cname AS cuname, CAST(sum(o.quantity * o.price) AS NUMERIC(12,2)) AS totalsales
FROM orders o, customer c
WHERE o.status = 'S' AND o.cid = c.cid  
GROUP BY c.cid, cuname
ORDER BY sum(o.quantity * o.price) DESC;


-- Query 4 statements

SELECT p.pid , p.pname, CAST(sum(o.quantity * p.cost) AS NUMERIC(12,2)) AS totalcost
FROM product p, customer c, orders o
WHERE o.status = 'S' AND c.cid = o.cid AND p.pid = o.pid 
GROUP BY p.pid, p.pname
ORDER BY sum(o.quantity * p.cost) ASC;


-- Query 5 statements

SELECT p.pid, p.pname, p.introdate
FROM product p
WHERE p.pid <> ALL (SELECT pid FROM orders)
ORDER BY p.pname;

-- Query 6 statements

SELECT c.cid,c.cname,l.lname AS locname
FROM customer c, location l
WHERE l.lid = c.lid  AND c.cid <> ALL(SELECT cid FROM orders)
ORDER BY cname;


-- Query 7 statements

SELECT CAST(TO_CHAR(o.odate,'YYYYMM') as integer) AS period, CAST(sum(o.quantity * o.price) AS NUMERIC(10,2))AS sales,  CAST(sum(o.quantity * p.cost) AS NUMERIC(10,2))AS cost 
FROM product p, orders o
WHERE o.status = 'S'  AND o.pid = p.pid
GROUP BY period 
ORDER BY period;

-- Query 8 statements

SELECT c2.cid, c2.cname, CAST(sum(o.quantity * price * (r.commission * 0.01)) AS NUMERIC(10,2)) AS comission
FROM customer c1,  customer c2, referral r, orders o
WHERE c1.cid = r.custref AND c1.cid = o.cid AND c2.cid = r.custid 
GROUP BY c2.cid ,c2.cname
ORDER BY c2.cname ASC;


-- Query 9 statements

SELECT p.pid, p.introdate AS date, CAST(sum(o.quantity * o.price) AS NUMERIC(12,2)) AS totalsales
FROM orders o, product p
WHERE o.status = 'S' AND p.pid = o.pid AND p.introdate <= '2015-12-31'
GROUP BY p.pid, date
ORDER BY date ASC;


-- Query 10 statements

SELECT l.lid, l.lname , CAST(sum(o.quantity * o.price) AS NUMERIC(12,2)) AS totalsales
FROM orders o, location l, warehouse w
WHERE o.status = 'S' AND w.lid = l.lid AND w.wid = o.shipwid 
GROUP BY l.lid, l.lname
ORDER BY l.lname ASC ;

