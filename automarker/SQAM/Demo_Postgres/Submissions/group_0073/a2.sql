-- Add below your SQL statements. , r.custref AS refid, c2.cname 
-- You can create intermediate views (as needed). Remember to drop these views after you have populated the result tables.
-- You can use the "\i a2.sql" command in psql to execute the SQL commands in this file.

-- START Query 1
SELECT r.custref AS cuid, c2.cname AS cuname, r.custid AS refid, c.cname as refname
	FROM referral r
	JOIN customer c
	ON r.custid = c.cid
	JOIN customer c2
	ON r.custref = c2.cid
	ORDER BY cuname ASC
;

-- END Query 1
-- START Query 2
SELECT orders.oid, orders.pid, orders.shipwid AS wid, orders.quantity AS ordqty, stock.quantity AS stockqty
	FROM orders
	JOIN stock
	ON orders.pid = stock.pid AND orders.shipwid = stock.wid
	WHERE orders.status = 'O' AND orders.quantity > stock.quantity
;

-- END Query 2
-- START Query 3
SELECT t.cuid, customer.cname AS cuname, t.totalsales
	FROM (SELECT cid AS cuid, SUM(orders.quantity * orders.price) AS totalsales 
		FROM orders 
		WHERE status = 'S'
		GROUP BY cid) t
	JOIN customer
	ON t.cuid = customer.cid
	ORDER BY totalsales DESC 
;  

-- END Query 3
-- START Query 4
SELECT t.pid, product.pname, t.totalcost
	FROM 
		(SELECT orders.pid, SUM(orders.quantity * product.cost) AS totalcost FROM orders 
		JOIN product
		ON orders.pid = product.pid
		WHERE status = 'S'
		GROUP BY orders.pid) t
	JOIN product
	ON product.pid = t.pid  
	ORDER BY totalcost ASC  
;

-- END Query 4
-- START Query 5
SELECT product.pid AS pid, product.pname, product.introdate
	FROM product 
	LEFT JOIN orders
	ON product.pid = orders.pid
	WHERE orders.pid IS NULL
	ORDER BY pname ASC
;

-- END Query 5
-- START Query 6

-- DISTINCT?

CREATE VIEW customers AS
SELECT cid, cname, lname AS locname
FROM customer 
JOIN location
ON customer.lid = location.lid;

SELECT customers.cid, customers.cname, customers.locname
	FROM customers
	LEFT JOIN orders
	ON customers.cid = orders.cid
	WHERE orders.cid IS NULL
	ORDER BY customers.cname ASC
;

DROP VIEW customers;

-- END Query 6
-- START Query 7

-- Is status supposed to be 'S'?
SELECT CAST(to_char(odate, 'YYYYMM') AS INTEGER) AS period, 
	SUM(orders.quantity * orders.price) AS sales, 
	SUM(orders.quantity * product.cost) AS cost
	FROM orders
	JOIN product
	ON orders.pid = product.pid
	WHERE status = 'S'
	GROUP BY period
	ORDER BY period ASC
;

-- END Query 7
-- START Query 8

-- Clarify commission is it the sum of price * qunatity * comission 

--fix this

SELECT customer.cid AS cid, cname, SUM(referral.commission * price * quantity) AS comission
	FROM referral 
	JOIN orders ON orders.cid = referral.custref	
	JOIN customer ON referral.custid = customer.cid
	GROUP BY customer.cid
	ORDER BY cname ASC;


-- END Query 8
-- START Query 9

SELECT total_sales.pid, product.introdate AS date, total_sales.totalsales
	FROM (SELECT pid, SUM(quantity * price) AS totalsales FROM orders WHERE orders.status = 'S' GROUP BY pid) AS total_sales 
	JOIN product
	ON total_sales.pid = product.pid
	WHERE product.introdate <= '2015-12-31'
	ORDER BY date ASC
;

-- END Query 9
-- START Query 10

--CREATE VIEW warehouse_sales AS
--SELECT location.lid, location.lname, t.totalsales 
	--FROM (SELECT shipwid, SUM(quantity * price) AS totalsales
	--	FROM orders 
	--	WHERE status = 'S'
	--	GROUP BY shipwid) t
--	JOIN warehouse 
--	ON warehouse.wid = t.shipwid
--	JOIN location
--	ON warehouse.lid = location.lid;


CREATE VIEW warehouse_sales AS
SELECT warehouse.lid AS lid, SUM(quantity * price) AS totalsales
	FROM orders 
	JOIN warehouse ON orders.shipwid = warehouse.wid
	WHERE status = 'S' 
	GROUP BY warehouse.lid;

CREATE VIEW ordered_empty_sales AS
SELECT warehouse.lid AS lid, 0 AS totalsales
	FROM orders 
	JOIN warehouse ON orders.shipwid = warehouse.wid
	WHERE status = 'O' AND lid NOT IN (SELECT lid FROM warehouse_sales) 
	GROUP BY warehouse.lid;

CREATE VIEW empty_sales AS
SELECT DISTINCT location.lid, 0 AS totalsales 
	FROM location
	WHERE lid NOT IN 
	(SELECT lid FROM warehouse_sales 
	UNION SELECT lid FROM ordered_empty_sales);


SELECT DISTINCT t.lid, location.lname, t.totalsales
FROM (SELECT * FROM warehouse_sales
	UNION
	SELECT * FROM empty_sales
	UNION
	SELECT * FROM ordered_empty_sales
	) t
	JOIN location ON t.lid = location.lid
	ORDER BY location.lname ASC
;

DROP VIEW empty_sales;	
DROP VIEW ordered_empty_sales;
DROP VIEW warehouse_sales;




-- END Query 10
