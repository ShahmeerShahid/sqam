-- Add below your SQL statements. 
-- You can create intermediate views (as needed). Remember to drop these views after you have populated the result tables.
-- You can use the "\i a2.sql" command in psql to execute the SQL commands in this file.

-- Query 1 statements
INSERT INTO Query1 (refid, refname, cuid, cuname)
SELECT referral.custref, cust2.cname, referral.custid, cust1.cname
FROM referral, customer AS "cust1", customer AS "cust2"
WHERE referral.custid = cust1.cid AND referral.custref = cus2.cid
ORDER BY cust1.cname;  


-- Query 2 statements
INSERT INTO Query2 (oid, pid, wid, ordqty, stockqty)
SELECT orders.oid, orders.pid, orders.shipwid, orders.quantity, stock.quantity
FROM orders, stock
WHERE orders.status = "O" AND orders.pid = stock.pid AND orders.shipwid = stock.wid AND stock.quantity < orders.quantity;


-- Query 3 statements
INSERT INTO Query3 (cuid, cuname, totalsales)
SELECT cid, customer.name, SUM(orders.price * orders.quantity) AS totalsales
FROM orders INNER JOIN customer ON orders.cid = customer.cid
WHERE orders.status = "S"
GROUP BY cid, customer.name
ORDER BY totalsales ASC;


-- Query 4 statements
INSERT INTO Query4 (pid, pname, totalcost)
SELECT pid, product.pname, SUM(orders.quantity * product.cost) AS totalcost
FROM orders INNER JOIN product ON orders.pid = product.pid
WHERE orders.status = "S"
GROUP BY pid, pname
ORDER BY totalcost ASC;


-- Query 5 statements
INSERT INTO Query5 (pid, pname, introdate)
SELECT pid, pname, introdate
FROM product
WHERE pid NOT IN (SELECT pid from orders)
ORDER BY pname ASC;  



-- Query 6 statements
CREATE VIEW [cust_no_order] AS
SELECT cid, cname, lid
FROM customer
WHERE cid NOT IN (SELECT cid FROM orders)
ORDER BY cname ASC;
  
INSERT INTO Query6 (cid, cname, locname)
SELECT cust_no_order.cid, cust_no_order.cname, location.lname
FROM cust_no_order INNER JOIN location ON cust_no_order.lid = location.lid
ORDER BY location.lname;

DROP VIEW cust_no_order; 



-- Query 7 statements
INSERT INTO Query7 (period, sales, cost)
SELECT SUM(DATE_PART("year", orders.odate)*100, DATE_PART("month", orders.odate)) AS period, SUM(orders.quantity * orders.price) AS sales, SUM(orders.quantity * product.cost) AS cost
FROM orders INNER JOIN product ON orders.pid = product.pid
GROUP BY period
ORDER BY period ASC;


-- Query 8 statements
CREATE VIEW [total_com] AS
SELECT referral.custid AS comid, SUM(orders.quantity * orders.price * referral.commission / 100) AS commission
FROM referral INNER JOIN orders ON referral.custref = orders.cid
GROUP BY comid;

INSERT INTO Query8 (cid, cname, commission)
SELECT customer.cid, customer.cname, total_com.commission
FROM total_com INNER JOIN customer ON total_com.comid = customer.cid
ORDER BY customer.cname ASC; 

DROP VIEW total_com;

-- Query 9 statements
INSERT INTO Query4 (pid, date, totalsales)
SELECT pid, product.introdate, SUM(orders.quantity * orders.price) AS totalsales
FROM orders INNER JOIN product ON orders.pid = product.pid
WHERE orders.status = "S" AND product.introdate <= DATE("2015-12-31")
GROUP BY pid
ORDER BY product.introdate ASC;


-- Query 10 statements
CREATE VIEW [order_loc] AS
SELECT customer.lid AS locid, SUM(order.quantity * order.price) AS totalsales
FROM orders INNER JOIN customer ON orders.cid = customer.cid
WHERE orders.status = "S"
GROUP BY locid;

INSERT INTO Query10 (lid, lname, totalsales)
SELECT location.lid, location.lname, order_loc.totalsales
FROM order_loc INNER JOIN location ON order_loc.locid = location.lid
ORDER BY location.lname;

DROP VIEW order_loc;
 

