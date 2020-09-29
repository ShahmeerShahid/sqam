-- Add below your SQL statements. 
-- You can create intermediate views (as needed). Remember to drop these views after you have populated the result tables.
-- You can use the "\i a2.sql" command in psql to execute the SQL commands in this file.

-- Query 1 statements
INSERT INTO query1(
SELECT R.custref AS cuid, C2.cname AS cuname, R.custid AS refid, C1.cname AS refname
FROM referral R JOIN customer C1 ON R.custid=C1.cid JOIN customer C2 ON R.custref=C2.cid
ORDER BY cuname ASC);


-- Query 2 statements
INSERT INTO query2(
SELECT o.oid, o.pid, o.shipwid AS wid, o.quantity AS ordqty, s.quantity AS stockqty
FROM orders AS o JOIN stock AS s ON s.pid = o.pid AND o.shipwid = s.wid 
WHERE o.status = 'O' AND s.quantity < o.quantity);


-- Query 3 statements
INSERT INTO query3(
SELECT c.cid AS cuid, c.cname AS cuname, SUM(o.quantity * o.price) AS totalsales
FROM customer c NATURAL JOIN orders o
WHERE o.status = 'S'
GROUP BY cuid
ORDER BY totalsales DESC);


-- Query 4 statements
INSERT INTO query4(
SELECT p.pid, p.pname, SUM(p.cost * o.quantity) AS totalcost
FROM product p NATURAL JOIN orders o
WHERE o.status = 'S'
GROUP BY p.pid
ORDER BY totalcost ASC);


-- Query 5 statements
INSERT INTO query5(
SELECT pid, pname, introdate 
FROM product 
WHERE pid NOT IN (SELECT pid FROM orders)
ORDER BY pname ASC);


-- Query 6 statements
INSERT INTO query6(
SELECT c.cid, c.cname, l.lname AS locname 
FROM customer c NATURAL JOIN location l 
WHERE c.cid NOT IN (SELECT cid FROM orders) 
ORDER BY c.cname ASC);


INSERT INTO query7(SELECT DISTINCT CAST(CAST(EXTRACT(year FROM o.odate) AS text) || CAST(EXTRACT(month FROM o.odate) AS text) AS integer) AS period, SUM(o.quantity*o.price) AS sales, SUM(p.cost*o.quantity) AS cost
FROM customer AS c NATURAL JOIN orders AS o NATURAL JOIN product As p
WHERE o.status = 'S'
GROUP By period
ORDER BY period ASC);


-- Query 8 statements
INSERT INTO query8(
SELECT c1.cid, c1.cname, SUM(o.price * o.quantity * r.commission) AS commission 
FROM referral R JOIN customer c1 ON R.custid=c1.cid JOIN customer c2 ON R.custref=c2.cid JOIN orders o ON c2.cid=o.cid 
GROUP BY c1.cid 
ORDER BY c1.cname ASC);


-- Query 9 statements
INSERT INTO query9(
SELECT p.pid, p.introdate AS "date", SUM(o.price * o.quantity) AS totalsales 
FROM product p NATURAL JOIN orders o 
WHERE o.status='S' AND p.introdate < '2015-12-31' 
GROUP BY p.pid 
ORDER BY p.introdate ASC);


-- Query 10 statements
CREATE VIEW shipped AS
SELECT * 
FROM warehouse w JOIN orders o ON o.shipwid=w.wid
WHERE o.status = 'S';

INSERT INTO query10(
SELECT l.lid, l.lname AS locname, SUM(shipped.price * shipped.quantity) AS totalsales 
FROM location l LEFT OUTER JOIN shipped ON l.lid=shipped.lid 
GROUP BY l.lid
ORDER BY l.lname ASC);

DROP VIEW IF EXISTS shipped CASCADE;



