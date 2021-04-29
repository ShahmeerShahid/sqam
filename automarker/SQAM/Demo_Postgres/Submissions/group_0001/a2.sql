-- Add below your SQL statements. 
-- You can create intermediate views (as needed). Remember to drop these views after you have populated the result tables.
-- You can use the "\i a2.sql" command in psql to execute the SQL commands in this file.

-- START Query 1

SELECT c.cid AS cuid, c.cname AS cuname, r.cid AS refid, r.cname AS refname
	FROM referral re
	JOIN customer c ON c.cid = re.custid
	JOIN customer r ON r.cid = re.custref
	ORDER BY c.cname ASC;

-- END Query 1
-- START Query 2

SELECT o.oid AS oid, o.pid AS pid, o.shipwid AS wid, o.quantity AS ordqty, s.quantity as stockqty
	FROM orders o
	JOIN stock s ON o.pid = s.pid AND o.shipwid = s.wid
	WHERE o.status = 'o' AND o.quantity > s.quantity;


-- END Query 2
-- START Query 3

SELECT c.cid AS cuid, c.cname AS cuname, sum(o.quantity * o.price) AS totalsales
	FROM customer c
	JOIN orders o ON c.cid = o.cid
	WHERE status = 'S'
	GROUP BY c.cid
	ORDER BY sum(o.quantity * o.price) DESC;

-- END Query 3
-- START Query 4

SELECT p.pid AS pid, p.pname AS pname, sum(o.quantity * p.cost) AS totalcost
	FROM product p
	JOIN orders o ON p.oid = o.oid
        WHERE status = 'S'
        GROUP BY p.pid
        ORDER BY sum(o.quantity * p.cost) ASC;
-- END Query 4
-- START Query 5

SELECT p.pid AS pid, p.pname AS pname, p.introdate AS introdate
        FROM product p
        LEFT OUTER JOIN orders o ON o.pid = p.pid
        WHERE o.oid IS NULL
        ORDER BY p.pname ASC;

-- END Query 5
-- START Query 6

SELECT c.cid AS cid, c.cname AS cname, l.lname AS locname
        FROM customer c
	JOIN location l ON c.lid = l.lid
        LEFT OUTER JOIN orders o ON o.cid = c.cid
        WHERE o.oid IS NULL
        ORDER BY c.cname ASC;

-- END Query 6
-- START Query 7



-- END Query 7
-- START Query 8

SELECT r.cid AS cid, r.cname AS cname, sum(0.005 * o.price * O.quantity) AS commission
        FROM referral re
        JOIN customer c ON c.cid = re.custid
        JOIN customer r ON r.cid = re.custref
	JOIN orders o ON c.cid = o.cid
	GROUP BY r.cid
        ORDER BY r.cname ASC;

-- END Query 8
-- START Query 9

SELECT p.pid AS pid, p.introdate AS date, sum(o.price * o.quantity) AS totalsales
	FROM orders o
	JOIN product p ON o.pid = p.pid
	WHERE p.introdate <= '2015-12-31'
	GROUP BY p.pid
	ORDER BY p.introdate ASC;

-- END Query 9
-- START Query 10

-- END Query 10
-- END Query 10
