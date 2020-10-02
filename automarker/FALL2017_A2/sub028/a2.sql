-- Add below your SQL statements. 
-- You can create intermediate views (as needed). Remember to drop these views after you have populated the result tables.
-- You can use the "\i a2.sql" command in psql to execute the SQL commands in this file.

-- Query 1 statements

INSERT INTO Query1 SELECT r.custref AS cuid, c2.cname AS cuname, r.custid AS refid, c1.cname AS refname FROM customer AS c1, customer AS c2, referral AS r WHERE c1.cid = r.custid AND c2.cid = r.custref ORDER BY c2.cname;

-- Query 2 statements

INSERT INTO Query2 SELECT oid, pid, wid, orders.quantity AS ordqty, stock.quantity AS stockqty FROM orders JOIN stock USING (pid) WHERE shipwid = wid AND orders.quantity > stock.quantity AND status = 'O';

-- Query 3 statements

INSERT INTO Query3 SELECT cid as cuid, cname as cuname, SUM(quantity * price) AS totalsales FROM customer JOIN orders USING (cid) WHERE status = 'S' GROUP BY cid ORDER BY totalsales DESC;

-- Query 4 statements

INSERT INTO Query4 SELECT pid, pname, SUM (cost*quantity) AS totalcost FROM orders JOIN product USING (pid) WHERE status = 'S' GROUP BY pid, pname ORDER BY totalcost;

-- Query 5 statements

INSERT INTO Query5 SELECT pid, pname, introdate FROM product WHERE pid NOT IN (SELECT pid FROM orders) ORDER BY pname;

-- Query 6 statements

INSERT INTO Query6 SELECT cid, cname, lname as locname FROM customer JOIN location USING (lid) GROUP BY cid, cname, locname HAVING cid NOT IN (SELECT cid FROM orders) ORDER BY cname;

-- Query 7 statements

INSERT INTO query7 SELECT cast(TO_CHAR (odate, 'YYYYMM') as int) AS period, SUM(quantity * price) AS sales, SUM(cost*quantity) AS cost FROM orders JOIN product USING(pid) GROUP BY period ORDER BY period;

-- Query 8 statements

CREATE VIEW step1 AS SELECT cid, SUM(quantity*price) AS commision FROM orders WHERE status = 'S' GROUP BY cid;

INSERT INTO Query8 SELECT c.cid, c.cname, SUM(referral.commission / 100 * step1.commision) AS commision FROM referral, step1, customer AS c WHERE referral.custref = step1.cid AND c.cid = referral.custid GROUP BY c.cid ORDER BY c.cname;

DROP VIEW step1;

-- Query 9 statements

INSERT INTO Query9 SELECT pid, introdate AS date, SUM(quantity * price) FROM orders JOIN product USING (pid) WHERE introdate <= '2015-12-31' AND status = 'S' GROUP BY pid, introdate ORDER BY introdate;

-- Query 10 statements

INSERT INTO Query10 SELECT lid, lname, totalsales FROM (SELECT SUM(quantity *price) AS totalsales, lid FROM orders JOIN customer USING (cid) WHERE status = 'S' GROUP BY lid ORDER BY totalsales) AS step1 RIGHT JOIN location USING (lid) ORDER BY lname;


