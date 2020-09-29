-- Add below your SQL statements. 
-- You can create intermediate views (as needed). Remember to drop these views after you have populated the result tables.
-- You can use the "\i a2.sql" command in psql to execute the SQL commands in this file.

-- Query 1 statements

CREATE VIEW r1 AS
SELECT custid AS cuid, cname AS cuname, custref AS refid
FROM referral INNER JOIN customer ON custid = cid;

CREATE VIEW r2 AS
SELECT cuid, cuname, refid, cname AS refname
FROM r1 INNER JOIN customer ON refid = cid
ORDER BY cuname ASC;

INSERT INTO query1(SELECT * FROM r2);

DROP VIEW IF EXISTS r2;
DROP VIEW IF EXISTS r1;

-- Query 2 statements

CREATE VIEW cannotShip AS
	SELECT orders.oid AS oid, orders.pid AS pid, stock.wid AS wid, orders.quantity AS ordqty, stock.quantity AS stockqty 
	FROM stock JOIN orders 
		ON stock.pid = orders.pid 
		AND stock.wid = orders.shipwid
	WHERE orders.quantity > stock.quantity
		AND orders.status = 'O';

INSERT INTO query2 (SELECT oid, pid, wid, ordqty, stockqty FROM cannotShip);

DROP VIEW cannotShip;

-- Query 3 statements

CREATE VIEW r1 AS
SELECT cid, quantity*price AS sales
FROM orders WHERE status = 'S';

CREATE VIEW r2 AS
SELECT cid AS cuid, cname AS cuname, SUM(sales) AS totalsales
FROM r1 NATURAL JOIN customer
GROUP BY cid, cname ORDER BY totalsales DESC;

INSERT INTO query3(SELECT * FROM r2);

DROP VIEW IF EXISTS r2;
DROP VIEW IF EXISTS r1;

-- Query 4 statements

CREATE VIEW productSales AS
	SELECT orders.pid AS pid, product.pname AS pname, SUM(orders.quantity*product.cost) AS totalcost
	FROM orders, product
	WHERE orders.pid = product.pid 
		AND orders.status = 'S'
	GROUP BY orders.pid, product.pname;

INSERT INTO query4 (SELECT pid, pname, totalcost FROM productSales ORDER BY totalcost);

DROP VIEW productSales;

-- Query 5 statements

CREATE VIEW r1 AS
(SELECT pid FROM product) EXCEPT (SELECT DISTINCT pid FROM orders);

CREATE VIEW r2 AS
SELECT pid, pname AS pame, introdate 
FROM r1 NATURAL JOIN product
ORDER BY pname ASC;

INSERT INTO query5(SELECT * FROM r2);

DROP VIEW IF EXISTS r2;
DROP VIEW IF EXISTS r1;

-- Query 6 statements

CREATE VIEW noOrders AS
	SELECT c.cid AS cid, c.cname AS cname, l.lname AS locname
	FROM customer c NATURAL JOIN location l
	WHERE NOT c.cid IN (
		SELECT customer.cid FROM customer JOIN orders USING (cid));

INSERT INTO query6 (SELECT * FROM noOrders ORDER BY cname);

DROP VIEW noOrders;

-- Query 7 statements

CREATE VIEW r1 AS
SELECT pid, CAST(TO_CHAR(odate, 'YYYYMM') AS INTEGER) AS period,
quantity, quantity*price AS sales FROM orders;

CREATE VIEW r2 AS
SELECT period, SUM(sales) AS sales, SUM(quantity*cost) AS cost
FROM R1 NATURAL JOIN product
GROUP BY period ORDER BY period ASC;

INSERT INTO query7(SELECT * FROM r2);

DROP VIEW IF EXISTS r2;
DROP VIEW IF EXISTS r1;

-- Query 8 statements

-- money purchased from each referral
CREATE VIEW refOrders AS
	SELECT o.cid AS custref, SUM(o.quantity*o.price) AS refPurch
	FROM orders o, referral r
	WHERE o.cid = r.custref
	GROUP BY o.cid;

CREATE VIEW earnings AS
	SELECT c.cid AS cid, c.cname AS cname, SUM((r.commission/100)*ro.refPurch) AS commission
	FROM  customer c, referral r, refOrders ro
	WHERE c.cid = r.custid
		AND ro.custref = r.custref
	GROUP BY c.cid;

INSERT INTO query8 (SELECT * FROM earnings ORDER BY cname);

DROP VIEW earnings;
DROP VIEW refOrders;

-- Query 9 statements

CREATE VIEW r1 AS
SELECT pid, introdate AS date, SUM(quantity * price) AS totalsales
FROM orders NATURAL JOIN product 
WHERE introdate <= '2015-12-31' AND status = 'S'
GROUP BY pid, date
ORDER BY introdate ASC;

INSERT INTO query9(SELECT * FROM r1);

DROP VIEW IF EXISTS r1;

-- Query 10 statements

-- orders augmented with the warehouse locations and only shipped ones
-- also contains the sales money earned per location
CREATE VIEW orderLocSale AS
	SELECT l.lid AS lid, l.lname AS lname, SUM(o.quantity*o.price) AS totalsales
	FROM orders o, warehouse w, location l
	WHERE o.shipwid = w.wid
		AND w.lid = l.lid
		AND o.status = 'S'
	GROUP BY l.lid, l.lname;

CREATE VIEW locSales AS
	SELECT l.lid AS lid, l.lname AS lname, ol.totalsales AS totalsales
	FROM location l LEFT OUTER JOIN orderLocSale ol
		ON l.lid = ol.lid;

INSERT INTO query10 (SELECT * FROM locSales ORDER BY totalsales);

DROP VIEW locSales;
DROP VIEW orderLocSale;
