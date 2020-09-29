-- Add below your SQL statements. 
-- You can create intermediate views (as needed). Remember to drop these views after you have populated the result tables.
-- You can use the "\i a2.sql" command in psql to execute the SQL commands in this file.

-- Query 1 statements
INSERT INTO Query1(
	SELECT c.cid AS cuid, c.cname AS cuname, r.custref AS refid, co.cid AS refname
	FROM customers AS c JOIN referral AS r ON c.cid = r.custid JOIN customer AS co ON r.custref = co.cid
)

-- Query 2 statements
INSERT INTO Query2(
	SELECT oid, pid, wid, ordqty, stockqty
	FROM orders, stock
	WHERE stock.status = 'O', stock.wid = orders.shipwid , stock.pid = orders.pid , stock.quantity<orders.quantity
	)

-- Query 3 statements
INSERT INTO Query3(
	SELECT c.cid AS cuid, c.name AS cuname, SUM(o.price*o.quantity) AS totalsales
	FROM customer AS c JOIN orders AS o ON c.cid = o.cid
	WHERE o.status = 'S'
	GROUP BY o.cid
	order by totalsales DESC
)

-- Query 4 statements
INSERT INTO Query4(
	SELECT pid, pname, ADD(order.price) AS totalcost
	FROM order , product
	WHERE order.pid = product.pid, status = 'S'
	GROUP BY order.pid
	ORDER BY totalcost ASC
	)
-- Query 5 statements
INSERT INTO Query5(
	SELECT p.pid, p.name, p.introdate
	FROM product AS p
	WHERE NOT EXISTS(
		SELECT p1.pid, p1.name, p1.introdate
		FROM product AS p1 JOIN orders AS o ON p1.pid = o.pid)
)

-- Query 6 statements
INSERT INTO Query6(
	SELECT cid, cname, lname
	FROM customer, location 
	WHERE customer.lid = location.lid, cid NOT IN (SELECT cid FROM order)
	ORDER BY cname ASC
	)

-- Query 7 statements
INSERT INTO Query7(
	SELECT CONCAT(YEAR(odate), MONTH(odate)) AS period, c.quantity AS sales, c.price AS cost
	FROM orders AS c
	ORDER BY period ASC
)

-- Query 8 statements 
INSERT INTO Query8(
	SELECT cid, cname, SUM(commission)
	FROM customer AS c JOIN referral as r ON c.cid = r.custid
	GROUP BY r.custid  
	ORDER BY cname ASC
	)
-- Query 9 statements
INSERT INTO Query9(
	SELECT p.pid, p.introdate AS date, COUNT(o.oid) AS totalsales
	FROM product AS p JOIN orders AS o ON p.pid = o.pid
	WHERE introdate <= '2015-12-31' AND o.status = 'S'
)


-- Query 10 statements
INSERT INTO Query10(
	SELECT l.lid, l.lname, COUNT(o.oid) AS totalsales
	FROM orders AS o JOIN warehouse AS w ON o.shipwid = w.wid JOIN location AS l ON l.lid = w.lid
	GROUP BY l.lid
	ORDER BY l.lname ASC
)
