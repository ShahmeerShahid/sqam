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
	--[5 Marks] For each customer, find (if it exists) the customer which referred it. Report the id and the name of customer and the id and the
	--name of the customer which referred it. For example, if the table referral contains the tuple (100, 200, 0.5), assuming the name of the
	--customer with id equal to 100 is customer100 and the name of customer with id equal to 200 is customer200 you need to report the following:
	--200 customer200 100 customer100
	INSERT INTO query1
	SELECT r.custref as cuid, c2.cname as cuname, r.custid as refid, c1.cname as refname
	FROM  referral r, customer c1, customer c2
	WHERE r.custid = c1.cid AND r.custref = c2.cid
	ORDER BY cuname ASC;

-- Query 2 statements
	--[5 Marks] Find unshipped orders which cannot be shipped from their designated warehouse, that is, order with a quantity strictly more than
	--the quantity found in the stock for the product in this order. Report the order ID, product id, the warehouse ID, order quantity, and available
	--stock quantity.
	INSERT INTO query2
	SELECT o.oid as oid,  o.pid as pid, o.shipwid as wid, o.quantity as ordqty, s.quantity as stockqty
	FROM orders o,stock s
	WHERE o.shipwid = s.wid AND o.quantity > s.quantity AND o.status = 'O' and o.pid = s.pid;

-- Query 3 statements
	--[5 Marks] Find total sales by customer and sort them in decreasing order of the sales amount. The sales amount for each order is the product
	--of quantity and price, on condition that the order status is ‘S’ (that is an order is considered a sale if an only if the order is shipped).
	INSERT INTO query3
	SELECT c.cid as cuid, c.cname as cuname, sum(o.quantity*o.price) as totalsales
	FROM  orders o, customer c 
	WHERE o.cid = c.cid AND o.status = 'S'
	GROUP BY c.cid, c.cname
	ORDER BY totalsales DESC;

-- Query 4 statements
	--[5 Marks] Find the cost of sales by product and sort them in increasing order of the cost. A sale is represented by a row of order table with
	--status ‘S’
	INSERT INTO query4
	SELECT p.pid as pid, p.pname as pname, sum(o.quantity * p.cost) as totalcost
	FROM orders o, product p
	WHERE o.pid = p.pid AND status = 'S'
	GROUP BY p.pid
	ORDER BY totalcost ASC;

-- Query 5 statements
	--. [5 Marks] Report product id, name, and introduction date for all products which have never been ordered by any customer sorted alphabetically
	--by product name.
	INSERT INTO query5
	SELECT p.pid as pid, p.pname as pname, p.introdate as introdate
	FROM orders o RIGHT JOIN product p ON o.pid = p.pid
	WHERE o.oid IS NULL
	ORDER BY pname ASC;

-- Query 6 statements

	--[5 Marks] Report customer id, name, and location name for all customers which have never placed any order sorted alphabetically by name
	INSERT INTO query6
	SELECT c.cid as cid, c.cname as cname, l.lname as locname
	FROM orders o RIGHT JOIN customer c ON o.cid = c.cid, location l
	WHERE l.lid = c.lid AND o.oid IS NULL
	ORDER BY cname ASC;

-- Query 7 statements
	--[5 Marks] Report the sales and the cost of sales grouped by accounting period, where an accounting period is a month. The accounting period
	--of a row in the order file is determined by the month and the year of the order date. For example, if a sales if performed on Sept15, 2017, then
	--the accounting period is 201709 (simply concatenate year written using 4 digits with month written using two digits).
	INSERT INTO query7
	SELECT (extract(year from o.odate)*100 + extract(month from o.odate) :: int) as period, sum(o.quantity * o.price) as sales, sum(o.quantity * p.cost) as cost
	FROM orders o, product p 
	WHERE o.pid = p.pid AND o.status = 'S'
	GROUP BY period
	ORDER BY period ASC;

-- Query 8 statements
	--[5 Marks] Report the commission earned by customers who have referred other customers. Please note the commission is earned on all orders,
	--shipped or not.
	INSERT INTO query8
	SELECT r.custid as cid, c.cname as cname, round(sum(r.commission * o.quantity * o.price *1/100), 2) as commission
	FROM referral r, orders o, customer c
	WHERE r.custref = o.cid AND r.custid = c.cid
	GROUP BY r.custid, c.cname
	ORDER BY cname ASC;

-- Query 9 statements	
	--[5 Marks] Report of sales by products introduced on or before 31 Dec 2015 and sort them in increasing order of the introduction date. A sale
	--is represented by a row of order table with status ‘S’.
	INSERT INTO query9
	SELECT p.pid as pid, p.introdate as date, sum(o.quantity*o.price) as totalsales
	FROM orders o, product p 
	WHERE o.pid = p.pid AND p.introdate >= '31 Dec 2015' AND o.status = 'S'
	GROUP BY p.pid, p.introdate
	ORDER BY date ASC;

-- Query 10 statements
	--[5 Marks] Report of sales by products grouped by warehouse location (determined using wid from the order). 
	--A sale is represented by a row of order table with status ‘S’.
	--report the locations with zero sales as well.
	INSERT INTO query10
	SELECT l.lid as lid, l.lname as lname, sum(o.quantity*o.price) as totalsales
	FROM orders o RIGHT JOIN location l ON o.shipwid = l.lid
	WHERE o.status = 'S' OR o.status IS NULL
	GROUP BY l.lid, l.lname
	ORDER BY lname ASC; 
