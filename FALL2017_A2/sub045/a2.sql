-- Add below your SQL statements. 
-- You can create intermediate views (as needed). Remember to drop these views after you have populated the result tables.
-- You can use the "\i a2.sql" command in psql to execute the SQL commands in this file.

-- Query 1 statements WORKS!!!
INSERT INTO Query1 (
SELECT 
	custref as cuid, 
	c1.cname as cuname, 
	custid as refid, 
	c2.cname as refname
FROM customer c1, customer c2, referral
WHERE c1.cid = custref AND c2.cid = custid
ORDER BY
	cuname ASC
);


-- Query 2 statements WORKS
INSERT INTO Query2 (
SELECT 
	o.oid as oid,
	o.pid as pid,
	w.wid as wid,
	o.quantity as ordqty,
	s.quantity as stockqty
FROM warehouse w, stock s, orders o
WHERE
	w.wid = s.wid AND
	s.pid = o.pid AND
	o.status = 'O' AND 
	o.quantity >  s.quantity
);


-- Query 3 statements WORKS!!
INSERT INTO Query3 (
SELECT 
	c.cid as cuid,
	c.cname as cuname,
	SUM(o.price * o.quantity) as totalsales
FROM customer c JOIN orders o ON c.cid = o.cid
WHERE o.status = 'S'
GROUP BY cuname, cuid
ORDER BY totalsales DESC
);


-- Query 4 statements WORKS!!
INSERT INTO Query4 (
SELECT
	p.pid as pid,
	p.pname as pname,
	SUM(p.cost * o.quantity) as totalcost
FROM product p 
	JOIN orders o ON p.pid = o.pid
WHERE
	o.status = 'S'
GROUP BY p.pid, p.pname
ORDER BY totalcost ASC
);


-- Query 5 statements WORKS!
INSERT INTO Query5 (
SELECT
	p.pid as pid,
	p.pname as pname,
	p.introdate as introdate
FROM product p 
WHERE p.pid NOT IN (SELECT p.pid FROM orders o WHERE o.pid = p.pid)
ORDER BY pname ASC
);


-- Query 6 statements WORKS
INSERT INTO Query6 (
SELECT
	c.cid as cid,
	c.cname as cname,
	l.lname as locname
FROM customer c 
	JOIN location l ON c.lid = l.lid
WHERE 
	c.cid NOT IN (SELECT c.cid FROM orders o WHERE o.cid = c.cid)
ORDER BY cname ASC
);


-- Query 7 statements  WORKS!
INSERT INTO Query7 (
SELECT
	cast(to_char(o.odate,'YYYYMM') as int) AS period,
	SUM(o.price * o.quantity) as sales,
	SUM(p.cost * o.quantity) as cost
FROM orders o
	JOIN product p ON o.pid = p.pid
GROUP BY period
ORDER BY period ASC
);


-- Query 8 statements WORKS - double check commision calculate
INSERT INTO Query8 (
SELECT
	c.cid as cid,
	c.cname as cname,
	SUM(o.quantity * o.price * r.commission) as commission
FROM customer c
	JOIN referral r ON c.cid = r.custid
	JOIN orders o ON o.cid = r.custref
GROUP BY cname, c.cid
ORDER BY cname ASC
);


-- Query 9 statements WORKS
INSERT INTO Query9 (
SELECT
	o.pid as pid,
	o.odate as date,
	(o.quantity * o.price) as totalsales
FROM orders o
WHERE o.odate <= '2015-12-31' 
	AND o.status = 'S'
ORDER BY date ASC
);

-- Query 10 statements  WORKS
INSERT INTO Query10 (
(SELECT
            l.lid as lid, l.lname as lname, SUM(o.quantity * o.price) as totalsales
        FROM location l 
            JOIN warehouse w ON w.lid = l.lid
            JOIN orders o ON w.wid = o.shipwid
        WHERE o.status = 'S'
        GROUP BY l.lname, l.lid
        ) 
UNION
(SELECT lid, lname, 0 as totalsales
FROM location 
WHERE lid NOT IN 
        (SELECT
            l.lid as lid
        FROM location l 
            JOIN warehouse w ON w.lid = l.lid
            JOIN orders o ON w.wid = o.shipwid
        WHERE o.status = 'S'
        ) 

) 
ORDER BY lname ASC
);
