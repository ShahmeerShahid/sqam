-- Add below your SQL statements. 
-- You can create intermediate views (as needed). Remember to drop these views after you have populated the result tables.
-- You can use the "\i a2.sql" command in psql to execute the SQL commands in this file.


-- location(*lid, lname, laddress)
-- warehouse(*wid, wname, lid)
-- customer(*cid, cname, lid)
-- referral(*custid, *custref, commission)
-- product(*pid, pname, introdate, um, cost)
-- stock(*pid, *wid, quantity)
-- orders(*oid, cid, pid, odate, shipwid, quantity, price, status)


-- Query 1 statements (DONE)
SELECT r.custref as cuid, c2.cname as cuname, r.custid as refid, c1.cname as refname
	FROM  referral r, customer c1, customer c2
	WHERE r.custid = c1.cid AND r.custref = c2.cid
	ORDER BY cuname ASC;

-- Query 2 statements
SELECT o.oid as oid,  o.pid as pid, o.shipwid as wid, o.quantity as ordqty, s.quantity as stockqty
	FROM orders o,stock s
	WHERE o.shipwid = s.wid AND o.quantity > s.quantity AND o.status = 'O' and o.pid = s.pid;

-- Query 3 statements
SELECT c.cid as cuid, c.cname as cuname, sum(o.quantity*o.price) as totalsales
	FROM  orders o, customer c 
	WHERE o.cid = c.cid AND o.status = 'S'
	GROUP BY c.cid, c.cname
	ORDER BY totalsales DESC;

-- Query 4 statements
SELECT p.pid as pid, p.pname as pname, sum(o.quantity * p.cost) as totalcost
	FROM orders o, product p
	WHERE o.pid = p.pid AND status = 'S'
	GROUP BY p.pid
	ORDER BY totalcost ASC;

-- Query 5 statements
SELECT p.pid as pid, p.pname as pname, p.introdate as introdate
	FROM orders o RIGHT JOIN product p ON o.pid = p.pid
	WHERE o.oid IS NULL
	ORDER BY pname ASC;

-- Query 6 statements
SELECT c.cid as cid, c.cname as cname, l.lname as locname
	FROM orders o RIGHT JOIN customer c ON o.cid = c.cid, location l
	WHERE l.lid = c.lid AND o.oid IS NULL
	ORDER BY cname ASC;

-- Query 7 statements
SELECT (extract(year from o.odate)*100 + extract(month from o.odate) :: int) as period, sum(o.quantity * o.price) as sales, sum(o.quantity * p.cost) as cost
	FROM orders o, product p 
	WHERE o.pid = p.pid AND o.status = 'S'
	GROUP BY period
	ORDER BY period ASC;

-- Query 8 statements
SELECT r.custid as cid, c.cname as cname, round(sum(r.commission * o.quantity * o.price *1/100), 2) as commission
	FROM referral r, orders o, customer c
	WHERE r.custref = o.cid AND r.custid = c.cid
	GROUP BY r.custid, c.cname
	ORDER BY cname ASC;

-- Query 9 statements	
SELECT p.pid as pid, p.introdate as date, sum(o.quantity*o.price) as totalsales
	FROM orders o, product p 
	WHERE o.pid = p.pid AND p.introdate >= '31 Dec 2015' AND o.status = 'S'
	GROUP BY p.pid, p.introdate
	ORDER BY date ASC;

-- Query 10 statements
SELECT l.lid as lid, l.lname as lname, sum(o.quantity*o.price) as totalsales
	FROM orders o RIGHT JOIN location l ON o.shipwid = l.lid
	WHERE o.status = 'S' OR o.status IS NULL
	GROUP BY l.lid, l.lname
	ORDER BY lname ASC; 
