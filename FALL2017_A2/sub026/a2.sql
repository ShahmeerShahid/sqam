-- Add below your SQL statements. 
-- You can create intermediate views (as needed). Remember to drop these views after you have populated the result tables.
-- You can use the "\i a2.sql" command in psql to execute the SQL commands in this file.

-- Query 1 statements

INSERT INTO Query1 (SELECT R.custid AS cuid, C1.cname AS cuname, R.custref AS refid, C2.cname AS refname FROM referral R, customer C1, customer C2 WHERE R.custid=C1.cid AND R.custref=C2.cid ORDER BY cuname ASC);

-- Query 2 statements TEST

INSERT INTO Query2 (SELECT oid, O.pid AS pid, shipwid AS wid, O.quantity AS ordqty, S.quantity AS stockqty FROM orders O, stock S WHERE O.pid = S.pid AND status = 'O' AND O.quantity > S.quantity);

-- Query 3 statements TEST

INSERT INTO Query3 (SELECT C.cid AS cuid, cname AS cuname, SUM(quantity*price) AS totalsales FROM customer C, orders O WHERE status='S' AND C.cid=O.cid GROUP BY C.cid ORDER BY totalsales DESC);

-- Query 4 statements TEST

INSERT INTO Query4 (SELECT O.pid AS pid, pname, quantity*cost AS totalcost FROM orders O, product P WHERE status='S' AND O.pid=P.pid ORDER BY totalcost DESC);

-- Query 5 statements TEST

INSERT INTO Query5 (SELECT O.pid AS pid, pname, introdate FROM product P, orders O WHERE P.pid=O.pid AND cid=NULL ORDER BY pname);

-- Query 6 statements Could be shortened by NOT IN?

INSERT INTO Query6 (SELECT C1.cid AS cid, cname, lname AS locname FROM customer C1, location L WHERE C1.lid=L.lid AND NOT EXISTS (SELECT distinct C2.cid FROM customer C2 RIGHT OUTER JOIN orders O ON C2.cid=O.cid) ORDER BY cname ASC);

-- Query 7 statements FIX THE DATE
-- Needs Month to be casted to something with 2 digits.
INSERT INTO Query7 (SELECT CAST((CAST(EXTRACT(YEAR FROM odate) AS VARCHAR) || CAST(EXTRACT(MONTH FROM odate) AS VARCHAR)) AS INTEGER) AS period, SUM(quantity*price) AS sales, SUM(quantity*cost) AS cost FROM orders O, product P WHERE O.pid=P.pid GROUP BY period);

-- Query 8 statements TEST Is commission a percentage or do we need to /100?

INSERT INTO Query8 (SELECT O.cid AS cid, cname, SUM(quantity*price*commission) AS commission FROM orders O, customer C, referral R WHERE O.cid=c.cid AND c.cid=R.custid AND R.custref<>NULL GROUP BY O.cid, cname ORDER BY cname ASC);

-- Query 9 statements TEST

INSERT INTO Query9 (SELECT P.pid, introdate AS date, SUM(quantity*price) AS totalsales FROM product P, orders O WHERE P.pid=O.pid AND introdate <= date '2017-12-31' AND status='S' GROUP BY P.pid ORDER BY date ASC);

-- Query 10 statements TEST Supposed to include 0 sales while others not

INSERT INTO Query10 (SELECT W.lid, lname, SUM(price*quantity) AS totalsales FROM orders O, location L, warehouse W WHERE status='S' AND shipwid=wid AND W.lid=L.lid GROUP BY W.lid, lname ORDER BY lname ASC);
