-- Add below your SQL statements. 
-- You can create intermediate views (as needed). Remember to drop these views after you have populated the result tables.
-- You can use the "\i a2.sql" command in psql to execute the SQL commands in this file.

-- Query 1 statements
INSERT INTO Query1 (SELECT customer1.cid AS cuid, customer1.cname AS cuname, customer2.cid AS refid, customer2.cname AS refname
FROM customer customer1, customer customer2, referral
WHERE customer1.cid = referral.custid AND customer2.cid = referral.custref
ORDER BY cuname ASC);


-- Query 2 statements
INSERT INTO Query2 (
SELECT orders.oid AS oid, orders.pid AS pid, orders.shipwid AS wid, 
orders.quantity AS ordqty, stock.quantity AS stockqty
FROM orders, stock
WHERE orders.pid = stock.pid AND orders.shipwid = stock.wid 
AND orders.quantity > stock.quantity AND orders.status = 'O');


-- Query 3 statements
INSERT INTO Query3(
SELECT customer.cid AS cuid, customer.cname AS cuname, sum(orders.price * orders.quantity) AS totalsales
FROM customer, orders
WHERE customer.cid = orders.cid AND orders.status = 'S'
GROUP BY cuid
ORDER BY totalsales DESC);


-- Query 4 statements
INSERT INTO Query4(SELECT product.pid AS pid, product.pname AS pname, sum(product.cost*orders.quantity) AS totalcost
FROM product, orders
WHERE product.pid = orders.pid AND orders.status = 'S'
GROUP BY product.pid
ORDER BY totalcost ASC);


-- Query 5 statements
INSERT INTO Query5 (
SELECT pid, pname, introdate 
FROM product
WHERE pid NOT IN (SELECT pid FROM orders)
ORDER BY pname ASC);


-- Query 6 statements
INSERT INTO Query6 (
SELECT cid, cname, lid AS locname
FROM customer
WHERE cid NOT IN (SELECT cid FROM orders)
ORDER BY cname ASC);


-- Query 7 statements
INSERT INTO Query7(
SELECT CAST(to_char(orders.odate, 'YYYYMM') AS INTEGER) AS period, sum (orders.price * orders.quantity) AS sales, sum(product.cost * orders.quantity) AS cost
FROM orders, product
WHERE orders.pid = product.pid AND orders.status = 'S'
GROUP BY period
ORDER BY period ASC);


-- Query 8 statements
INSERT INTO Query8 (
SELECT customer.cid AS cid, customer.cname AS cname, 
sum(referral.commission * orders.price * orders.quantity) AS commission
FROM customer, referral, orders
WHERE customer.cid = referral.custid AND orders.cid = referral.custref
GROUP BY customer.cid
ORDER BY cname ASC);


-- Query 9 statements
INSERT INTO Query9 (
SELECT product.pid AS pid, product.introdate AS date, 
sum(orders.price * orders.quantity) AS totalsales
FROM product, orders
WHERE product.pid = orders.pid AND introdate <= '20151231' AND status = 'S'
GROUP BY product.pid
ORDER BY date ASC);


-- Query 10 statements

