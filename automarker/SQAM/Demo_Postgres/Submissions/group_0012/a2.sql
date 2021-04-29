-- Add below your SQL statements. 
-- You can create intermediate views (as needed). Remember to drop these views after you have populated the result tables.
-- You can use the "\i a2.sql" command in psql to execute the SQL commands in this file.

-- START Query 1
SELECT R.custid AS cuid, C1.cname AS cuname, R.custref AS refid, C2.cname AS refname
			FROM customer C1, referral R, customer C2
			WHERE C1.cid = R.custid AND C2.cid = R.custref
			ORDER BY cuname;


-- END Query 1
-- START Query 2
SELECT oid, orders.pid, shipwid AS wid, orders.quantity AS ordqty, stock.quantity AS stockqty
			FROM orders JOIN stock ON orders.pid = stock.pid AND orders.shipwid = stock.wid
			WHERE status = 'O' AND orders.quantity > stock.quantity;


-- END Query 2
-- START Query 3
SELECT customer.cid AS cuid, customer.cname AS cuname, SUM(quantity*price) AS totalsales
			FROM orders, customer
			WHERE orders.cid = customer.cid AND status = 'S'
			GROUP BY cuid
			ORDER BY totalsales DESC;


-- END Query 3
-- START Query 4
SELECT product.pid AS pid, pname, SUM(quantity*cost) AS totalcost
			FROM product, orders
			WHERE product.pid = orders.pid AND status = 'S'
			GROUP BY product.pid
			ORDER BY totalcost;


-- END Query 4
-- START Query 5
SELECT pid, pname, introdate
			FROM product
			WHERE pid NOT IN
				(SELECT pid FROM orders)
			ORDER BY pname;


-- END Query 5
-- START Query 6
SELECT cid, cname, lname AS locname
			FROM customer, location
			WHERE customer.lid = location.lid AND cid NOT IN
				(SELECT cid FROM orders)
			ORDER BY cname;


-- END Query 6
-- START Query 7
SELECT CAST(TO_CHAR(odate, 'YYYYMM') AS INTEGER) AS period, SUM(quantity*price) AS sales, SUM(quantity*cost) AS cost
			FROM orders NATURAL RIGHT JOIN product
			WHERE orders.pid = product.pid
			GROUP BY period
			ORDER BY period;


-- END Query 7
-- START Query 8
SELECT referral.custid AS cid, cname, SUM(quantity*price*commission) AS commission
			FROM customer, referral, orders
			WHERE customer.cid = custid AND referral.custref = orders.cid
			GROUP BY customer.cname, referral.custid
			ORDER BY cname;


-- END Query 8
-- START Query 9
SELECT orders.pid AS pid, introdate AS date, SUM(quantity*price) AS totalsales
			FROM orders NATURAL LEFT JOIN product
			WHERE orders.pid = product.pid AND introdate <= '2015-12-31' AND status = 'S'
			GROUP BY orders.pid, product.introdate
			ORDER BY date;


-- END Query 9
-- START Query 10
CREATE VIEW withSales AS
SELECT location.lid AS lid, location.lname AS lname, SUM(quantity*price) AS totalsales
FROM location, warehouse, orders 
WHERE warehouse.wid = orders.shipwid AND location.lid = warehouse.lid AND status = 'S'
GROUP BY location.lid;

CREATE VIEW noSales AS
SELECT location.lid AS lid, location.lname AS lname, 0 AS totalsales
FROM location
WHERE lid NOT IN (SELECT lid 
	FROM warehouse w, orders o
	WHERE w.wid = o.shipwid AND status = 'S');

SELECT *
	FROM withSales NATURAL FULL OUTER JOIN noSales
	ORDER BY lname;

DROP VIEW withSales;
DROP VIEW noSales;
-- END Query 10
