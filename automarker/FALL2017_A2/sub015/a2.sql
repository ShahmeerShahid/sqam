-- Add below your SQL statements. 
-- You can create intermediate views (as needed). Remember to drop these views after you have populated the result tables.
-- You can use the "\i a2.sql" command in psql to execute the SQL commands in this file.

-- REMEMBER: "Sale" means status = SHIPPED.

-- Query 1 statements
INSERT INTO QUERY1 (
	SELECT r.custref AS cuid, c1.cname AS cuname, r.custid AS refid, c2.cname AS refname
	FROM referral AS r, customer AS c1, customer AS c2
	WHERE r.custref = c1.cid AND r.custid = c2.cid
	ORDER BY cuname ASC
	);


-- Query 2 statements
INSERT INTO QUERY2 (
	SELECT o.oid AS oid, o.pid AS pid, o.shipwid AS wid, o.quantity AS ordqty, s.quantity AS stockqty
	FROM orders AS o, stock AS s
	WHERE o.status = 'O' AND o.pid = s.pid AND o.quantity > s.quantity
	);


-- Query 3 statements
INSERT INTO QUERY3 (
	SELECT t.cuid AS cuid, c.cname AS cuname, t.totalsales AS totalsales
	FROM (
		SELECT o.cid AS cuid, SUM(o.price*o.quantity) AS totalsales
		FROM orders AS o
		WHERE o.status = 'S'
		GROUP BY cuid
		) t JOIN customer AS c ON t.cuid = c.cid
	ORDER BY totalsales DESC
	);


-- Query 4 statements
INSERT INTO QUERY4 (
	SELECT t.pid AS pid, p.pname AS pname, t.totalcost AS totalcost
	FROM (
		SELECT o.pid AS pid, SUM(o.quantity*p.cost) AS totalcost
		FROM orders AS o, product AS p
		WHERE o.status = 'S'
		GROUP BY o.pid 
		) t JOIN product AS p ON t.pid = p.pid
	ORDER BY totalcost ASC
	);


-- Query 5 statements
INSERT INTO QUERY5 (
	SELECT p.pid AS pid, p.pname AS pname, p.introdate AS introdate
	FROM product AS p, orders as o
	WHERE p.pid NOT IN (SELECT pid FROM orders)
	ORDER BY pname ASC
	);


-- Query 6 statements
INSERT INTO QUERY6 (
	SELECT DISTINCT c.cid AS cid, c.cname AS cname, l.lname AS locname
	FROM customer AS c, location AS l
	WHERE c.cid NOT IN (SELECT orders.cid FROM orders)
	ORDER BY cname ASC
	);


-- Query 7 statements
INSERT INTO QUERY7 (
	SELECT TO_CHAR(o.odate, 'YYYYMM')::text::int AS period, SUM(o.quantity*o.price) AS sales, SUM(p.cost*o.quantity) AS cost
	FROM orders AS o, product AS p
	WHERE o.status = 'S' AND p.pid = o.pid
	GROUP BY period
	ORDER BY period ASC
	);


-- Query 8 statements
INSERT INTO QUERY8 (
	SELECT q1.refid AS cid, q1.refname AS cname, r.commission/100*(o.quantity*o.price)
	FROM Query1 AS q1, referral AS r, orders as o
	WHERE q1.refid = o.cid
	ORDER BY cname ASC
	);


-- Query 9 statements
INSERT INTO QUERY9 (
	SELECT p.pid AS pid, p.introdate AS date, o.quantity*o.price AS totalsales
	FROM product AS p, orders AS o
	WHERE p.introdate >= to_date(cast('2015-12-31' AS text), 'YYYY-MM-DD') AND o.status='S'
	--GROUP BY pid
	ORDER BY date ASC
	);


-- Query 10 statements
INSERT INTO QUERY10 (
	SELECT t.lid AS lid, l.lname AS lname, o.quantity*o.price AS totalsales
	FROM (
		SELECT l.lid AS lid
		FROM location AS l
		GROUP BY l.lid
		) t LEFT OUTER JOIN warehouse AS w ON w.wid = t.lid JOIN orders AS o ON w.wid = o.shipwid JOIN location AS l ON t.lid = l.lid
	WHERE o.status='S'
	ORDER BY lname ASC
	);

