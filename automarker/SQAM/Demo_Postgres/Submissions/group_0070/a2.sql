-- Add below your SQL statements. 
-- You can create intermediate views (as needed). Remember to drop these views after you have populated the result tables.
-- You can use the "\i a2.sql" command in psql to execute the SQL commands in this file.

-- Query 1 statements
SELECT r.cid AS cuid, r.cname  AS cuname, c.cid  AS refid, c.cname AS refname 
	FROM customer c JOIN referral ON c.cid = custid JOIN customer r ON r.cid = custref ORDER BY c.cname;

-- Query 2 statements
SELECT o.oid AS oid, o.pid AS pid, o.shipwid AS wid, o.quantity AS ordqty, s.quantity 
	AS stockqty FROM orders AS o, stock AS s WHERE o.pid = s.pid AND o.status = 'O' AND o.shipwid = s.wid 
	AND o.quantity > s.quantity;


-- Query 3 statements
SELECT o.cid AS cuid, c.cname AS cuname, (SUM(o.quantity*o.price)) as totalsales 
	FROM orders o JOIN customer c ON o.cid = c.cid WHERE o.status = 'S' GROUP BY o.cid, c.cname
	ORDER BY totalsales DESC;


-- Query 4 statements
SELECT o.pid AS pid, p.pname AS pname, (SUM(o.quantity*p.cost)) AS totalcost 
	FROM orders o JOIN product p ON o.pid = p.pid WHERE o.status = 'S' GROUP BY o.pid, p.pname
	ORDER BY totalcost;

-- Query 5 statements
SELECT DISTINCT p.pid AS pid, p.pname AS pname, p.introdate as introdate FROM orders o, product p 
	WHERE p.pid NOT IN(SELECT pid FROM orders) ORDER BY p.pname;


-- Query 6 statements
SELECT DISTINCT c.cid AS cid, c.cname  AS cname, l.lname as locname FROM customer c JOIN location l 
	ON c.lid = l.lid, orders o WHERE c.cid NOT IN(SELECT cid FROM orders) ORDER BY c.cname;


-- Query 7 statements
SELECT cast(to_char(o.odate,'YYYYMM') as int) AS period, SUM(o.quantity * o.price) 
	AS sales, SUM(o.quantity * p.cost) AS cost FROM orders o JOIN product p ON o.pid = p.pid WHERE o.status = 'S'
	GROUP BY period ORDER BY period;


-- Query 8 statements
SELECT custid AS cid, c.cname AS cname, ROUND(SUM((o.quantity * o.price * commission)/100),2) 
	AS commission FROM customer c JOIN referral ON c.cid = custid JOIN customer r ON r.cid = custref JOIN orders o ON o.cid = r.cid 
	GROUP BY custid, c.cname ORDER BY c.cname;


-- Query 9 statements
SELECT p.pid AS pid, p.introdate AS date, SUM(o.price * o.quantity) AS totalsales
	FROM product p JOIN orders o ON p.pid = o.pid WHERE p.introdate <= '2015-12-31' AND o.status = 'S' 
	GROUP BY p.pid
	ORDER BY p.introdate;


-- Query 10 statements
SELECT l.lid AS lid, l.lname AS lname, COALESCE(totalsales, 0) AS totalsales FROM(SELECT o.shipwid, sum(o.quantity*o.price) AS totalsales FROM orders o WHERE status = 'S' GROUP BY o.shipwid)a 
JOIN warehouse w ON a.shipwid = w.wid RIGHT OUTER JOIN location l on l.lid = w.lid ORDER BY l.lname;

