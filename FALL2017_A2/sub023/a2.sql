-- Add below your SQL statements. 
-- You can create intermediate views (as needed). Remember to drop these views after you have populated the result tables.
-- You can use the "\i a2.sql" command in psql to execute the SQL commands in this file.

-- Query 1 statements

INSERT INTO Query1 (SELECT r.custref cuid, c1.cname cuname, r.custid refid, c2.cname refname
FROM referral r, customer c1, customer c2
WHERE r.custref = c1.cid AND r.custid = c2.cid
ORDER BY cuname
);

-- Query 2 statements

INSERT INTO Query2 (SELECT oid, o.pid, o.shipwid AS wid, CAST(o.quantity as NUMERIC(10,2)) AS ordqty, CAST(s.quantity as NUMERIC(10,2))  AS stockqty
FROM stock s JOIN orders o ON s.wid=o.shipwid AND s.pid=o.pid
WHERE o.quantity>s.quantity AND status='O' AND o.quantity>0
);

-- Query 3 statements

INSERT INTO Query3 (SELECT c.cid AS cuid, c.cname AS cuname, CAST(SUM(o.quantity*o.price)as NUMERIC(12,2)) AS totalsales
FROM orders o NATURAL JOIN customer c
WHERE o.status = 'S' AND o.quantity>0
GROUP BY c.cid
ORDER BY totalsales DESC
);

-- Query 4 statements

INSERT INTO Query4 (SELECT p.pid, p.pname, CAST(SUM(o.quantity*p.cost)as NUMERIC(12,2)) AS totalcost
FROM orders o NATURAL JOIN product p
WHERE status = 'S' AND o.quantity>0
GROUP BY p.pid
ORDER BY totalcost
);

-- Query 5 statements

INSERT INTO Query5 (SELECT pid, pname, introdate FROM product 
WHERE pid NOT IN (SELECT pid FROM orders)
ORDER BY pname
);
-- Query 6 statements

INSERT INTO Query6 (SELECT cid, cname, lname AS locname
FROM customer NATURAL JOIN location
WHERE cid NOT IN (SELECT cid FROM orders)
ORDER BY cname
);

-- Query 7 statements

INSERT INTO Query7 (SELECT CAST(TO_CHAR(odate,'YYYYMM') as integer) AS period, 
CAST(SUM(o.quantity*o.price)as NUMERIC(10,2)) AS sales, CAST(SUM(o.quantity*p.cost) as NUMERIC(10,2)) AS cost
FROM product p NATURAL JOIN orders o
WHERE o.quantity>0 AND o.status='S'
GROUP BY period
ORDER BY period    
);

-- Query 8 statements

INSERT INTO Query8 (SELECT r.custid cid, c.cname, CAST(SUM(r.commission*o.price*o.quantity*0.01) as NUMERIC(10,2))  commission
FROM referral r, customer c, orders o
WHERE r.custid = c.cid AND r.custref = o.cid AND o.quantity>0
GROUP BY r.custid, c.cname
ORDER BY c.cname
);

-- Query 9 statements

INSERT INTO Query9 (SELECT p.pid, introdate AS date, CAST(sum(o.price*o.quantity)as NUMERIC(12,2)) AS totalsales
FROM orders o NATURAL JOIN product p
WHERE status='S' AND o.quantity>0
GROUP BY p.pid
HAVING introdate<='2015-12-31' 
ORDER BY introdate
);

-- Query 10 statements

INSERT INTO Query10 (SELECT l.lid, l.lname, CAST(SUM(o.price*o.quantity) as NUMERIC(12,2)) AS totalsales
FROM orders o, warehouse w, location l
WHERE o.quantity>=0 AND o.status='S' AND o.shipwid=w.wid AND w.lid=l.lid
GROUP BY l.lid
ORDER BY l.lname 
);
