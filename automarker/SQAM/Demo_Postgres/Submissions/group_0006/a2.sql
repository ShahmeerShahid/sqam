-- Add below your SQL statements. 
-- You can create intermediate views (as needed). Remember to drop these views after you have populated the result tables.
-- You can use the "\i a2.sql" command in psql to execute the SQL commands in this file.

-- START Query 1
SELECT customer1.cid AS cuid, customer1.cname AS cuname, customer2.cid AS refid, customer2.cname AS refname
FROM customer customer1, customer customer2, referral
WHERE customer1.cid = referral.custid AND customer2.cid = referral.custref
ORDER BY cuname ASC;


-- END Query 1
-- START Query 2

SELECT orders.oid AS oid, orders.pid AS pid, orders.shipwid AS wid, 
orders.quantity AS ordqty, stock.quantity AS stockqty
FROM orders, stock
WHERE orders.pid = stock.pid AND orders.shipwid = stock.wid 
AND orders.quantity > stock.quantity AND orders.status = 'O';


-- END Query 2
-- START Query 3

SELECT customer.cid AS cuid, customer.cname AS cuname, sum(orders.price * orders.quantity) AS totalsales
FROM customer, orders
WHERE customer.cid = orders.cid AND orders.status = 'S'
GROUP BY cuid
ORDER BY totalsales DESC;


-- END Query 3
-- START Query 4
SELECT product.pid AS pid, product.pname AS pname, sum(product.cost*orders.quantity) AS totalcost
FROM product, orders
WHERE product.pid = orders.pid AND orders.status = 'S'
GROUP BY product.pid
ORDER BY totalcost ASC;


-- END Query 4
-- START Query 5

SELECT pid, pname, introdate 
FROM product
WHERE pid NOT IN (SELECT pid FROM orders)
ORDER BY pname ASC;


-- END Query 5
-- START Query 6

SELECT cid, cname, lid AS locname
FROM customer
WHERE cid NOT IN (SELECT cid FROM orders)
ORDER BY cname ASC;


-- END Query 6
-- START Query 7

SELECT CAST(to_char(orders.odate, 'YYYYMM') AS INTEGER) AS period, sum (orders.price * orders.quantity) AS sales, sum(product.cost * orders.quantity) AS cost
FROM orders, product
WHERE orders.pid = product.pid AND orders.status = 'S'
GROUP BY period
ORDER BY period ASC;


-- END Query 7
-- START Query 8

SELECT customer.cid AS cid, customer.cname AS cname, 
sum(referral.commission * orders.price * orders.quantity) AS commission
FROM customer, referral, orders
WHERE customer.cid = referral.custid AND orders.cid = referral.custref
GROUP BY customer.cid
ORDER BY cname ASC;


-- END Query 8
-- START Query 9

SELECT product.pid AS pid, product.introdate AS date, 
sum(orders.price * orders.quantity) AS totalsales
FROM product, orders
WHERE product.pid = orders.pid AND introdate <= '20151231' AND status = 'S'
GROUP BY product.pid
ORDER BY date ASC;


-- END Query 9
-- START Query 10

-- END Query 10
