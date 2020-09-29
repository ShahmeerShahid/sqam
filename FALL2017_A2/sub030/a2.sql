-- Add below your SQL statements. 
-- You can create intermediate views (as needed). Remember to drop these views after you have populated the result tables.
-- You can use the "\i a2.sql" command in psql to execute the SQL commands in this file.

-- Query 1 statements
INSERT INTO Query1
(
	SELECT cust2.cid AS cuid, cust2.cname AS cuname, cust1.cid AS refid, cust1.cname AS refname
	FROM customer cust1, customer cust2, referral ref
	WHERE cust1.cid = ref.custid AND cust2.cid = ref.custref
	ORDER BY cuname ASC
); 

-- Query 2 statements
INSERT INTO Query2
( 
	SELECT o.oid as oid, o.pid as pid, s.wid as wid, o.quantity as ordqty, s.quantity as stockqty
	FROM orders o JOIN stock s 
	ON o.pid = s.pid AND s.wid = o.shipwid  --add this 
	WHERE status = 'O' AND o.quantity > s.quantity -- quantity part
);


-- Query 3 statements
INSERT INTO Query3
(
	SELECT c.cid as cuid, c.cname as cuname, SUM(price * quantity) AS totalsales
	FROM customer c, orders o
	WHERE c.cid = o.cid AND status = 'S'
	GROUP BY c.cid
	ORDER BY totalsales DESC
);


-- Query 4 statements
INSERT INTO Query4
(
	SELECT pid, p.pname as pname, SUM(p.cost * o.quantity) as totalcost
	FROM product p NATURAL JOIN orders o
	WHERE status = 'S'
	GROUP BY p.pid
	ORDER BY totalcost ASC
);


-- Query 5 statements
INSERT INTO Query5
(
	SELECT p.pid as pid, p.pname as pname, introdate
	FROM product p LEFT JOIN orders o
	ON p.pid = o.pid
	WHERE oid IS NULL
	ORDER BY pname ASC
);


-- Query 6 statements
--don’t know yet about duplicates -- add subquery --didn't work
--SELECT c.cid as cid, c.cname as cname, l.lname as locname
--FROM customer c JOIN location l ON c.lid = l.lid
--LEFT JOIN orders o ON c.cid = o.cid
--WHERE o.oid IS NULL
--GROUP BY c.cid
--ORDER BY c.cname ASC
--Another one:
INSERT INTO Query6
(
	SELECT cid, cname, lname as locname
	FROM customer c JOIN location l ON c.lid = l.lid
	WHERE cid NOT IN (SELECT cid FROM orders)
	ORDER BY cname ASC
);


-- Query 7 statements
INSERT INTO Query7
(
	SELECT  CAST(to_char(odate, 'yyyymm') AS INTEGER) AS period, SUM(o.quantity * o.price) AS sales, SUM(o.quantity * p.cost) AS cost
	FROM orders o, product p
	WHERE o.status = 'S' AND o.pid = p.pid
	GROUP BY period
	ORDER BY period ASC

);


-- Query 8 statements
INSERT INTO Query8
(
	SELECT  c.cid as cid, c.cname as cname, SUM(o.quantity * o.price * r.commission) as comission  
	FROM customer c JOIN referral r ON c.cid = r.custid 
	JOIN orders o ON r.custref = o.cid  
	GROUP BY c.cid  
	ORDER BY c.cname ASC
);


-- Query 9 statements 
INSERT INTO Query9
(
	--date is a key word --- datee
	SELECT p.pid as pid, p.introdate as date, SUM(o.price * o.quantity) as totalsales
	FROM product p JOIN orders o ON p.pid = o.pid
	WHERE introdate <= '20151231' AND status = 'S'
	GROUP BY p.pid
	ORDER BY date ASC
);



-- Query 10 statements. 
INSERT INTO Query10
(
	SELECT l.lid as lid, l.lname as lname, COALESCE(totalsales, 0) as totalsales
    FROM location l FULL OUTER JOIN (
    	SELECT l.lid as lid, l.lname as lname, SUM(o.price * o.quantity) as totalsales
		FROM location l JOIN warehouse w ON l.lid = w.lid
	JOIN orders o ON w.wid = o.shipwid
	WHERE status = 'S' 
    GROUP BY l.lid) as l2 ON l.lid = l2.lid
	ORDER BY lname ASC
);
	--not right ????? plz check this    should report 4 rows 
	--On query 10, please do report the locations with zero sales as well.
	--SELECT l.lid as lid, l.lname as lname, SUM(o.price * o.quantity) as totalsales
	--FROM location l JOIN warehouse w ON l.lid = w.lid
	--JOIN orders o ON w.wid = o.shipwid
	--WHERE status = 'S'
	--GROUP BY l.lid
	--ORDER BY lname ASC  

