-- Add below your SQL statements. 
-- You can create intermediate views (as needed). Remember to drop these views after you have populated the result tables.
-- You can use the "\i a2.sql" command in psql to execute the SQL commands in this file.

-- Query 1 statements
INSERT INTO Query1 (SELECT custref AS refid, ref.cname AS refname, custid AS cuid, customer.cname AS cuname, FROM customer,referral,customer AS ref WHERE custid = customer.cid AND custref = ref.cid ORDER BY ref.cname ASC;)


-- Query 2 statements
INSERT INTO Query2( SELECT oid, orders.pid, wid, orders.quantity AS ordqty, stock.quantity AS stockqty FROM stock INNER JOIN orders ON stock.pid = orders.pid WHERE orders.quantity < stock.quantity AND orders.status = 'O';)

-- Query 3 statements
INSERT INTO Query3(SELECT customer.cid AS cuid, customer.cname AS cuname, SUM(quantity*price) AS totalsales FROM orders NATURAL JOIN customer WHERE status = 'S' GROUP BY customer.cid ORDER BY totalsales DESC;)


-- Query 4 statements
INSERT INTO Query4(SELECT pid, pname, SUM(cost*quantity) AS totalcost FROM product NATURAL JOIN orders WHERE status = 'S' GROUP BY product.pid ORDER BY totalcost ASC;)


-- Query 5 statements
INSERT INTO Query5(SELECT pid, pname AS pame, introdate FROM product WHERE product.pid NOT IN (SELECT pid FROM orders) ORDER BY pame ASC;)


-- Query 6 statements

INSERT INTO Query6(SELECT cid, cname, lid AS locname FROM customer WHERE customer.cid NOT IN (SELECT cid FROM orders) ORDER BY cname;)

-- Query 7 statements

INSERT INTO Query7(SELECT CAST(to_char(orders.odate,'YYYYmm') AS INTEGER) AS period, SUM(orders.quantity*price) AS sales, SUM(orders.quantity * cost) AS cost FROM orders, product WHERE orders.pid = product.pid AND orders.status = 'S' GROUP BY period ORDER BY period ASC;)

-- Query 8 statements

INSERT INTO Query8(SELECT cid, cname, commission FROM customer, referral WHERE custref = cid ORDER BY cname ASC;)

-- Query 9 statements
INSERT INTO Query9(SELECT pid, introdate AS date, SUM(quantity * price) AS totalsales FROM orders NATURAL JOIN product WHERE introdate < '2015-12-31' AND status = 'S' GROUP BY orders.pid ,date ORDER BY date ASC;)

-- Query 10 statements

INSERT INTO Query10(SELECT lid,wname AS lname ,SUM(quantity*price) AS totalsales FROM orders, warehouse WHERE shipwid = wid AND status = 'S' GROUP BY lid,wname ORDER BY lname;)
