-- Add below your SQL statements. 
-- You can create intermediate views (as needed). Remember to drop these views after you have populated the result tables.
-- You can use the "\i a2.sql" command in psql to execute the SQL commands in this file.

-- Query 1 statements
INSERT INTO QUERY1 (SELECT custid AS cuid, a.cname AS cuname, custref AS refid, b.cname AS refname 
					FROM referral LEFT JOIN customer AS a ON custid = cid, customer AS b 
					WHERE b.cid = custref ORDER BY cuname ASC);


-- Query 2 statements
INSERT INTO QUERY2 (SELECT oid, orders.pid, shipwid, orders.quantity AS ordqty, stock.quantity AS stockqty
					FROM orders LEFT JOIN stock ON orders.pid = stock.pid 
					WHERE orders.status = 'O' AND shipwid <> wid AND orders.quantity < stock.quantity);


-- Query 3 statements
INSERT INTO QUERY3 (SELECT orders.cid AS cuid, cname AS cuname, orders.quantity * orders.price AS totalsales 
					FROM orders LEFT JOIN customer ON orders.cid = customer.cid 
					WHERE status = 'S' ORDER BY totalsales DESC);


-- Query 4 statements
INSERT INTO QUERY4 (SELECT DISTINCT product.pid, pname as pame, orders.quantity * product.cost AS totalcost 
					FROM product LEFT JOIN orders ON orders.pid = product.pid 
					WHERE orders.status = 'S' ORDER BY totalcost ASC);


-- Query 5 statements
INSERT INTO QUERY5 (SELECT pid, pname AS pame, introdate 
					FROM product EXCEPT
					(SELECT orders.pid, pname, introdate 
					 FROM orders LEFT JOIN product ON orders.pid = product.pid) 
					ORDER BY pame ASC);


-- Query 6 statements
INSERT INTO QUERY6 (SELECT customer.cid, cname, location.lname AS locname 
					FROM customer LEFT JOIN location ON location.lid = customer.lid 
					WHERE customer.cid NOT IN (SELECT cid FROM orders) ORDER BY cname ASC);


-- Query 7 statements
INSERT INTO QUERY7 (SELECT CAST(CONCAT(EXTRACT(YEAR FROM odate), EXTRACT(MONTH FROM odate)) AS INT) AS period, 
					quantity * price AS sales, quantity * cost AS cost
					FROM orders LEFT JOIN product ON orders.pid = product.pid ORDER BY period ASC);


-- Query 8 statements
INSERT INTO QUERY8 (SELECT referral.custid AS cid, cname, commission * price AS commission 
					FROM referral LEFT JOIN orders ON referral.custref = orders.cid 
								  LEFT JOIN customer ON referral.custid = customer.cid ORDER BY cname ASC);


-- Query 9 statements
INSERT INTO QUERY9 (SELECT product.pid, introdate, quantity * price AS totalsales 
					FROM orders LEFT JOIN product ON orders.pid = product.pid 
					WHERE introdate < '20151231' AND status = 'S' ORDER BY introdate ASC);


-- Query 10 statements
INSERT INTO QUERY10 (SELECT location.lid, lname, price * quantity AS totalsales 
					 FROM (SELECT * FROM orders LEFT JOIN warehouse ON shipwid = wid WHERE status = 'S') AS b
					 LEFT JOIN location ON location.lid = b.lid ORDER BY lname ASC);

