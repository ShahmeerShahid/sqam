-- Add below your SQL statements. 
-- You can create intermediate views (as needed). Remember to drop these views after you have populated the result tables.
-- You can use the "\i a2.sql" command in psql to execute the SQL commands in this file.

-- Query 1 statements






SELECT RferCust.cid AS cuid, RferCust.cname AS cuname, RferdCust.cid AS refid, RferdCust.cname AS refname
FROM Customer RferCust,Customer RferdCust,Referral Rfl
WHERE RferCust.cid = Rfl.custid AND RferdCust.cid = Rfl.custref 
ORDER BY refName ASC;




-- Query 2 statements

CREATE TEMP VIEW Table2 AS

	SELECT ord.oid AS oid, ord.pid AS  pid, ord.shipwid AS wid, ord.quantity AS ordqty, stock.quantity AS stockqty

	FROM orders ord JOIN stock ON ord.shipwid = stock.wid AND ord.pid = stock.pid

	WHERE status = 'O' AND  ord.quantity > stock.quantity;

CREATE TEMP VIEW NotInStock AS

	SELECT oid AS oid, ord.pid AS pid, shipwid AS wid, ord.quantity AS ordqty, 0 AS stockqty

	FROM orders ord

	WHERE ord.quantity > 0 AND status = 'O' AND 
			NOT EXISTS(SELECT * FROM stock 
			WHERE ord.shipwid = stock.wid AND
			ord.pid = stock.pid);

SELECT * FROM Table2

	UNION ALL

	SELECT * FROM NotInStock;

DROP VIEW NotInStock; 
DROP VIEW Table2;

-- Query 3 statements

SELECT cust.cid AS cuid, cust.cname AS cuname, SUM(ord.price * ord.quantity) AS totalsales

	FROM orders ord,customer cust

	WHERE ord.cid = cust.cid AND ord.status = 'S'

	GROUP BY cuid

	ORDER BY totalsales DESC;


-- Query 4 statements

SELECT prod.pid AS pid, prod.pname AS pname, SUM(prod.cost * ord.quantity) AS totalcost

	FROM product prod,orders ord

	WHERE prod.pid = ord.pid AND ord.status = 'S'

	GROUP BY prod.pid

	ORDER BY totalcost ASC;

-- Query 5 statements

SELECT pid, pname, introdate

	FROM product prod

	WHERE pid NOT IN (SELECT pid from orders)

	ORDER BY pname ASC;

-- Query 6 statements

SELECT cid, cname, lid AS locname

	FROM customer cust

	WHERE cid NOT IN (SELECT cid FROM orders)

	ORDER BY cname ASC;

-- Query 7 statements


-- INSERT INTO Query7


CREATE TEMP VIEW test7 AS

	SELECT CAST(CONCAT(substring(CAST(ord.odate AS TEXT), 1, 4),substring(CAST(ord.odate AS TEXT), 6, 2)) AS INT) AS period, SUM(ord.quantity * ord.price) AS sales, SUM(ord.quantity * prod.cost) AS cost

	FROM orders ord, product prod

	WHERE ord.pid = prod.pid 

	GROUP BY period 

	ORDER BY period ASC;

SELECT * FROM test7;

DROP VIEW test7;



-- Query 8 statements

SELECT cust.cid AS cid, cust.cname AS cname, SUM(ord.quantity * ord.price * ref.commission) AS commission

	FROM referral ref, customer cust, orders ord

	WHERE ref.custid = cust.cid AND ref.custref = ord.cid 

	GROUP BY cust.cid

	ORDER BY cname ASC;

-- Query 9 statements

CREATE TEMP VIEW test9 AS

	SELECT prod.pid AS pid, prod.introdate AS introdate, SUM(ord.price * ord.quantity) AS totalsales

	FROM product prod, orders ord

	WHERE prod.pid = ord.pid AND introdate <= '2015-12-31' AND ord.status = 'S'

	GROUP BY prod.pid

	ORDER BY introdate ASC;


SELECT * FROM test9;

DROP VIEW test9;

-- Query 10 statements

CREATE TEMP VIEW Atleast1 AS

	SELECT loc.lid AS lid, loc.lname AS lname, SUM(ord.quantity * ord.price) AS totalsales

	FROM location loc, warehouse ware, orders ord

	WHERE loc.lid = ware.lid AND ware.wid = ord.shipwid AND ord.status = 'S'

	GROUP BY loc.lid;


CREATE TEMP VIEW NoSales AS

	SELECT loc.lid AS lid, loc.lname AS lname

	FROM location loc

	WHERE loc.lid NOT IN(SELECT lid FROM Atleast1);



CREATE TEMP VIEW NoSalesNoMoney AS

	SELECT lid, lname, 0 AS totalsales

	FROM NoSales;



CREATE TEMP VIEW Final1 AS

	SELECT * FROM NoSalesNoMoney 

	UNION ALL 

	SELECT * FROM Atleast1;

CREATE TEMP VIEW Final2 AS

	SELECT lid, lname, totalsales

	FROM Final1

	ORDER BY lname ASC;


SELECT *
	FROM Final2 
	ORDER BY lname ASC;

DROP VIEW Final2;
DROP VIEW Final1;
DROP VIEW NoSalesNoMoney;
DROP VIEW NoSales;
DROP VIEW Atleast1;



