-- Add below your SQL statements. 
-- You can create intermediate views (as needed). Remember to drop these views after you have populated the result tables.
-- You can use the "\i a2.sql" command in psql to execute the SQL commands in this file.

-- START Query 1

SELECT t1.cuid, t1.cuname, t1.refid, c.cname AS refname FROM (SELECT c.cid AS cuid, c.cname AS cuname, r.custref AS refid FROM customer AS c, referral AS r WHERE c.cid = r.custid) AS t1, customer AS c WHERE t1.refid = c.cid ORDER BY cuname ASC;

-- END Query 1
-- START Query 2

SELECT o.oid, o.pid, o.shipwid AS wid, o.quantity AS ordqty, s.quantity AS stockqty FROM Orders AS o, Stock AS s WHERE o.pid = s.pid and o.shipwid = s.wid and status = 'O' and o.quantity > s.quantity;

-- END Query 2
-- START Query 3

SELECT t1.cid AS cuid, c.cname AS cuname, t1.amount AS totalsales FROM (SELECT cid, SUM(quantity * price) AS amount FROM orders WHERE status = 'S' GROUP BY cid) AS t1, customer AS c WHERE t1.cid = c.cid ORDER BY totalsales DESC;

-- END Query 3
-- START Query 4

SELECT t1.pid, p.pname, (t1.totalsold * p.cost) AS totalcost FROM (SELECT pid, SUM(quantity) AS totalsold FROM orders WHERE status = 'S' GROUP BY pid) AS t1, product AS p WHERE t1.pid = p.pid ORDER BY totalcost ASC;

-- END Query 4
-- START Query 5

SELECT t1.pid, p.pname, p.introdate FROM ((SELECT pid FROM product) EXCEPT (SELECT pid FROM orders)) AS t1, product AS p WHERE p.pid = t1.pid ORDER BY pname ASC;

-- END Query 5
-- START Query 6

SELECT t2.cid, t2.cname, l.lname AS locname FROM (SELECT t1.cid, c.cname, c.lid FROM ((SELECT cid FROM customer) EXCEPT (SELECT cid FROM orders)) AS t1, customer AS c WHERE c.cid = t1.cid) AS t2, location AS l WHERE l.lid = t2.lid ORDER BY cname ASC;

-- END Query 6
-- START Query 7

SELECT TO_CHAR(o.odate, 'YYYYMM')::INT AS period, SUM(o.quantity * o.price) AS sales, SUM(o.quantity * p.cost) AS cost FROM orders AS o, product AS p WHERE o.pid = p.pid and o.status = 'S' GROUP BY period ORDER BY period ASC;

-- END Query 7
-- START Query 8

SELECT t2.cid, c.cname, SUM(t2.comission) FROM (SELECT r.custid AS cid, t1.sales * r.commission * 0.01 AS comission FROM (SELECT cid, SUM(quantity * price) AS sales FROM orders GROUP BY cid) AS t1, referral AS r WHERE t1.cid = r.custref) AS t2, customer AS c WHERE t2.cid = c.cid GROUP BY t2.cid, c.cname ORDER BY cname ASC;

-- END Query 8
-- START Query 9

SELECT o.pid, p.introdate AS date, SUM(o.quantity * o.price) AS totalsales FROM orders AS o, product AS p WHERE status = 'S' and p.pid = o.pid and introdate <= '2015-12-31'GROUP BY o.pid, p.introdate ORDER BY date ASC;

-- END Query 9
-- START Query 10

SELECT t2.lid, t2.lname, coalesce(t2.totalsales, 0) as totalsales FROM (SELECT * FROM (SELECT w.lid, SUM(o.quantity * o.price) AS totalsales FROM orders AS o, warehouse AS w WHERE status = 'S' and w.wid = o.shipwid GROUP BY w.lid) AS t1 NATURAL FULL JOIN location AS l) AS t2 ORDER BY lname ASC;

-- END Query 10
