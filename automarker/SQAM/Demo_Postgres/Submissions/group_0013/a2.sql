-- Add below your SQL statements. 
-- You can create intermediate views (as needed). Remember to drop these views after you have populated the result tables.
-- You can use the "\i a2.sql" command in psql to execute the SQL commands in this file.

-- START Query 1
SELECT custid AS cuid, a.cname AS cuname, custref AS refid, b.cname AS refname 
					FROM referral LEFT JOIN customer AS a ON custid = cid, customer AS b 
					WHERE b.cid = custref ORDER BY cuname ASC;


-- END Query 1
-- START Query 2
SELECT oid, orders.pid, shipwid, orders.quantity AS ordqty, stock.quantity AS stockqty
					FROM orders LEFT JOIN stock ON orders.pid = stock.pid 
					WHERE orders.status = 'O' AND shipwid <> wid AND orders.quantity < stock.quantity;


-- END Query 2
-- START Query 3
SELECT orders.cid AS cuid, cname AS cuname, orders.quantity * orders.price AS totalsales 
					FROM orders LEFT JOIN customer ON orders.cid = customer.cid 
					WHERE status = 'S' ORDER BY totalsales DESC;


-- END Query 3
-- START Query 4
SELECT DISTINCT product.pid, pname as pame, orders.quantity * product.cost AS totalcost 
					FROM product LEFT JOIN orders ON orders.pid = product.pid 
					WHERE orders.status = 'S' ORDER BY totalcost ASC;


-- END Query 4
-- START Query 5
SELECT pid, pname AS pame, introdate 
					FROM product EXCEPT
					(SELECT orders.pid, pname, introdate 
					 FROM orders LEFT JOIN product ON orders.pid = product.pid) 
					ORDER BY pame ASC;


-- END Query 5
-- START Query 6
SELECT customer.cid, cname, location.lname AS locname 
					FROM customer LEFT JOIN location ON location.lid = customer.lid 
					WHERE customer.cid NOT IN (SELECT cid FROM orders) ORDER BY cname ASC;


-- END Query 6
-- START Query 7
SELECT CAST(CONCAT(EXTRACT(YEAR FROM odate), EXTRACT(MONTH FROM odate)) AS INT) AS period, 
					quantity * price AS sales, quantity * cost AS cost
					FROM orders LEFT JOIN product ON orders.pid = product.pid ORDER BY period ASC;


-- END Query 7
-- START Query 8
SELECT referral.custid AS cid, cname, commission * price AS commission 
					FROM referral LEFT JOIN orders ON referral.custref = orders.cid 
								  LEFT JOIN customer ON referral.custid = customer.cid ORDER BY cname ASC;


-- END Query 8
-- START Query 9
SELECT product.pid, introdate, quantity * price AS totalsales 
					FROM orders LEFT JOIN product ON orders.pid = product.pid 
					WHERE introdate < '20151231' AND status = 'S' ORDER BY introdate ASC;


-- END Query 9
-- START Query 10
SELECT location.lid, lname, price * quantity AS totalsales 
					 FROM (SELECT * FROM orders LEFT JOIN warehouse ON shipwid = wid WHERE status = 'S') AS b
					 LEFT JOIN location ON location.lid = b.lid ORDER BY lname ASC;

-- END Query 10
