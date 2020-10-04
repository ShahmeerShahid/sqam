-- Add below your SQL statements. 
-- You can create intermediate views (as needed). Remember to drop these views after you have populated the result tables.
-- You can use the "\i a2.sql" command in psql to execute the SQL commands in this file.

-- Query 1 statements

-- Query 2 statements
CREATE VIEW cannotShip AS SELECT o.oid, s.pid, s.wid, o.quantity AS ordqty, s.quantity as stockqty
	FROM orders as o, stock as s
	WHERE o.quantity > s.quantity AND o.status = 'O';
insert into query2 select * FROM cannotShip;
DROP VIEW IF EXISTS cannotShip CASCADE;

-- Query 3 statements
CREATE VIEW totalSales as SELECT c.cid AS cuid, c.cname as cuname, (o.price * o.quantity) as totalsales
	FROM customer as c
	INNER JOIN orders as o
	ON c.cid = o.cid
	WHERE o.status = 'S' AND (o.price * o.quantity) > 0 
	ORDER BY totalsales DESC;
insert into query3 select * FROM totalSales;
DROP VIEW IF EXISTS totalSales CASCADE;
	
-- Query 4 statements
CREATE VIEW costSales as SELECT p.pid, p.pname, (o.quantity * p.cost) as totalcost
	FROM product as p,  orders as o
	WHERE p.pid = o.oid
	AND o.status = 'S'
	ORDER BY totalcost ASC;
insert into query4 select * FROM costSales;
DROP VIEW IF EXISTS costSales CASCADE;

-- Query 5 statements
CREATE VIEW productIntro as SELECT p.pid, p.pname, p.introdate
	FROM product as p
	LEFT JOIN orders o ON p.pid = o.pid
	WHERE o.pid IS NULL
	ORDER BY pname ASC;
insert into query5 select * FROM productIntro;
DROP VIEW IF EXISTS productIntro CASCADE;

-- Query 6 statements
CREATE VIEW noOrder as SELECT c.cid, c.cname, l.lname as locname
	FROM customer as c
	LEFT JOIN orders o 
	ON c.cid = o.cid
	INNER JOIN location as l 
	ON c.lid = l.lid
	WHERE o.cid IS NULL
	ORDER BY cname ASC;
INSERT into query6 select * FROM noOrder;
DROP VIEW IF EXISTS noOrder CASCADE;

-- Query 7 statements
CREATE VIEW monthGroup as SELECT CAST(TO_CHAR(o.odate, 'YYYYMM') AS INT) AS period, SUM((o.quantity * o.price)) as sales, SUM((o.quantity * p.cost)) as cost
	FROM orders as o
	INNER JOIN product as p
	ON p.pid = o.pid
	where (o.quantity * o.price) > 0
	GROUP BY period
	ORDER BY period ASC;
INSERT into query7 select * FROM monthGroup;
DROP VIEW IF EXISTS monthGroup CASCADE;

-- Query 8 statements
CREATE VIEW customersWithReferrals as SELECT c.cid, c.cname, r.custref, SUM(r.commission) as commission 
	FROM referral as r
	INNER JOIN customer as c
	ON r.custid = c.cid
	GROUP BY c.cid, r.custref;
CREATE VIEW custRefBuy as SELECT DISTINCT c.cid, c.cname, c.commission
	FROM customersWithReferrals as c
	INNER JOIN orders as o
	ON o.cid = c.custref
	ORDER BY cname ASC;
INSERT into query8 select * FROM custRefBuy;
DROP VIEW IF EXISTS customersWithReferrals CASCADE;
DROP VIEW IF EXISTS custRefBuy CASCADE;

-- Query 9 statements
CREATE VIEW beforeDec as SELECT p.pid, p.introdate as date, (o.price * o.quantity) as totalsales
	FROM product as p
	INNER JOIN orders as o
	ON p.pid = o.pid
	AND p.introdate < '2015-12-31'
	AND (o.price * o.quantity) > 0
	AND o.status = 'S'
	ORDER BY date ASC;
INSERT into query9 select * FROM beforeDec;
DROP VIEW IF EXISTS beforeDec CASCADE;

-- Query 10 statements
CREATE VIEW customerLocations as SELECT c.lid, (o.price * o.quantity) as totalsales
	FROM customer as c
	INNER JOIN orders as o
	ON c.cid = o.cid AND o.status = 'S';
CREATE VIEW groupLocation as SELECT l.lid, l.lname, SUM(c.totalsales)
	FROM customerLocations as c
	LEFT JOIN location as l
	ON c.lid = l.lid 
	GROUP BY l.lid
	ORDER BY l.lname ASC;
INSERT into query10 select * from groupLocation;
DROP VIEW IF EXISTS customerLocations CASCADE;
DROP VIEW IF EXISTS groupLocation CASCADE;

