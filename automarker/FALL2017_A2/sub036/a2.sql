-- Add below your SQL statements. 
-- You can create intermediate views (as needed). Remember to drop these views after you have populated the result tables.
-- You can use the "\i a2.sql" command in psql to execute the SQL commands in this file.

-- Query 1 statements
INSERT INTO Query1 (SELECT ref.custid AS cuid, cust.cname AS cuname, ref.custref AS refid,cust2.name AS refname
FROM referral ref JOIN customer cust ON ref.custid = cust.cid JOIN customer cust2 ON ref.custred = cust2.cid
ORDER BY cust.cname ASC)

-- Query 2 statements
INSERT INTO Query2 (SELECT od.oid, od.pid, st.wid, od.quantity AS ordqty, st.quantity AS stockqty
FROM orders od JOIN stock st ON od.pid = st.pid AND od.shipwid = st.wid
WHERE od.quantity > st.quantity)


-- Query 3 statements
INSERT INTO Query3 (SELECT od.cid AS cuid, cust.cname AS cuname, SUM(od.quantity * od.price) AS totalsales
FROM orders od JOIN customer cust ON od.cid = cust.cid GROUP BY od.cid HAVING od.status = 'S'
ORDER BY SUM(od.quantity * od.price) DESC)


-- Query 4 statements
INSERT INTO Query4 (SELECT od.pid, pr.pname, SUM(pr.cost*od.quantity) AS totalcost
FROM orders od JOIN product pr ON od.pid = pr.pid GROUP BY od.pid HAVING od.status = 'S'
ORDER BY SUM(pr.cost*od.quantity) ASC)


-- Query 5 statements
INSERT INTO Query5 ((SELECT pid,pname,introdate FROM product ORDER BY pname ASC)
EXCEPT
(SELECT DISTINCT pid FROM orders))


-- Query 6 statements
INSERT INTO Query6 ((SELECT cust.cid, cust.cname, lo.lname AS locname
 FROM customer cust JOIN location lo ON cust.lid = lo.lid ORDER BY cname ASC)
EXCEPT
(SELECT DISTINCT cid FROM orders))

-- Query 7 statements


-- Query 8 statements
INSERT INTO Query8 (SELECT ref.custid AS cid, cust.cname, tot.totalprice*ref.commission AS commission
FROM referral ref JOIN customer cust ON ref.custcid = cust.cid 
     JOIN totalsale tot ON ref.custid = tot.cid
	(SELECT cid, SUM(price,quantity) AS totalprice
	FROM orders GROUP BY cid
	£©totalsale
ORDER BY cust.cname ASC)


-- Query 9 statements
INSERT INTO Query9 (SELECT od.pid, pro.introdate AS date, SUM(quantity*price) AS totalsales
FROM orders od JOIN product pro ON od.pid = pro.pid GROUP BY od.pid HAVING od.status = 'S'
WHERE pro.introdate <= 31/12/2015
ORDER BY pro.introdate ASC)


-- Query 10 statements
INSERT INTO Query10 (SELECT od.shipwid AS lid, lo.lname, quantity*price AS totalsales
FROM orders od JOIN warehouse ware ON ware.wid = od.shipwid JOIN location lo ON ware.lid = lo.lid GROUP BY lo.lname
WHERE status = 'S' ORDER BY lo.lname ASC

