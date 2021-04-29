-- Add below your SQL statements. 
-- You can create intermediate views (as needed). Remember to drop these views after you have populated the result tables.
-- You can use the "\i a2.sql" command in psql to execute the SQL commands in this file.

-- START Query 1
SELECT custref AS refid, ref.cname AS refname, custid AS cuid, customer.cname AS cuname, FROM customer,referral,customer AS ref WHERE custid = customer.cid AND custref = ref.cid ORDER BY ref.cname ASC;


-- END Query 1
-- START Query 2
 SELECT oid, orders.pid, wid, orders.quantity AS ordqty, stock.quantity AS stockqty FROM stock INNER JOIN orders ON stock.pid = orders.pid WHERE orders.quantity < stock.quantity AND orders.status = 'O';

-- END Query 2
-- START Query 3
SELECT customer.cid AS cuid, customer.cname AS cuname, SUM(quantity*price) AS totalsales FROM orders NATURAL JOIN customer WHERE status = 'S' GROUP BY customer.cid ORDER BY totalsales DESC;


-- END Query 3
-- START Query 4
SELECT pid, pname, SUM(cost*quantity) AS totalcost FROM product NATURAL JOIN orders WHERE status = 'S' GROUP BY product.pid ORDER BY totalcost ASC;


-- END Query 4
-- START Query 5
SELECT pid, pname AS pame, introdate FROM product WHERE product.pid NOT IN (SELECT pid FROM orders) ORDER BY pame ASC;


-- END Query 5
-- START Query 6

SELECT cid, cname, lid AS locname FROM customer WHERE customer.cid NOT IN (SELECT cid FROM orders) ORDER BY cname;

-- END Query 6
-- START Query 7

SELECT CAST(to_char(orders.odate,'YYYYmm') AS INTEGER) AS period, SUM(orders.quantity*price) AS sales, SUM(orders.quantity * cost) AS cost FROM orders, product WHERE orders.pid = product.pid AND orders.status = 'S' GROUP BY period ORDER BY period ASC;

-- END Query 7
-- START Query 8

SELECT cid, cname, commission FROM customer, referral WHERE custref = cid ORDER BY cname ASC;

-- END Query 8
-- START Query 9
SELECT pid, introdate AS date, SUM(quantity * price) AS totalsales FROM orders NATURAL JOIN product WHERE introdate < '2015-12-31' AND status = 'S' GROUP BY orders.pid ,date ORDER BY date ASC;

-- END Query 9
-- START Query 10

SELECT lid,wname AS lname ,SUM(quantity*price) AS totalsales FROM orders, warehouse WHERE shipwid = wid AND status = 'S' GROUP BY lid,wname ORDER BY lname;
-- END Query 10
