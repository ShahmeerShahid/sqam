-- Add below your SQL statements. 
-- You can create intermediate views (as needed). Remember to drop these views after you have populated the result tables.
-- You can use the "\i a2.sql" command in psql to execute the SQL commands in this file.

-- Query 1 statements
CREATE VIEW refs AS
	SELECT custid AS refid, custref AS cid
	FROM referral;

INSERT INTO Query1(
	SELECT c1.cid AS cuid, c1.cname AS cuname, c2.cid AS refid, c2.cname AS refname
	FROM refs, customer c1, customer c2
	WHERE c1.cid = refs.cid AND c2.cid = refs.refid
	ORDER BY c1.cname ASC
	);

DROP VIEW refs;


-- Query 2 statements

INSERT INTO Query2(
	SELECT oid, O.pid, wid, O.quantity AS ordqty, S.quantity AS stockqty
	FROM orders O, stock S
	WHERE O.pid = S.pid AND O.shipwid = S.wid AND O.quantity > S.quantity AND O.status = 'O'
	);


-- Query 3 statements

CREATE VIEW ordtotalsales AS
        SELECT cid AS cuid, CAST((price * quantity) as NUMERIC(12,2)) AS totalsales, status
        FROM orders
        GROUP BY cuid, totalsales, status;

INSERT INTO Query3(
	SELECT cuid, C.cname AS cuname, SUM(totalsales) AS totalsales
	FROM ordtotalsales, customer C
	WHERE cuid = C.cid AND status = 'S'
	GROUP BY cuid, cuname
	ORDER BY totalsales DESC
	);

DROP VIEW ordtotalsales;


-- Query 4 statements

INSERT INTO Query4(
	SELECT P.pid, P.pname, CAST(SUM(P.cost * O.quantity) as NUMERIC(12,2)) AS totalcost
	FROM orders O, product P
	WHERE O.status = 'S'
	GROUP BY P.pid
	ORDER BY totalcost ASC
	);


-- Query 5 statements

INSERT INTO Query5(
	SELECT P.pid, P.pname, P.introdate
	FROM product P
	WHERE P.pid NOT IN (SELECT pid FROM orders)
	GROUP BY P.pid
	ORDER BY P.pname ASC
	);


-- Query 6 statements

INSERT INTO Query6(
	SELECT C.cid, C.cname, L.lname
	FROM customer C NATURAL JOIN location L
	WHERE C.cid NOT IN (SELECT cid from orders) AND C.lid = L.lid
	GROUP BY C.cid, L.lname
	ORDER BY C.cname ASC
	);

-- Query 7 statements

CREATE VIEW periods AS
	SELECT *, (EXTRACT (year FROM odate) * 100)+(EXTRACT (month FROM odate)) AS period
	FROM orders
	GROUP BY oid,period;

INSERT INTO Query7(
	SELECT period,CAST(SUM(PR.price * PR.quantity) as NUMERIC(12,2)) AS sales, CAST(SUM(P.cost * PR.quantity) as NUMERIC(12,2)) AS cost
	FROM periods PR, product P
	WHERE PR.status = 'S'
	GROUP BY period
	ORDER BY period ASC
	);

DROP VIEW periods;

-- Query 8 statements



-- Query 9 statements

CREATE VIEW oldproduct AS
	SELECT *
	FROM product P
	WHERE P.introdate <= CAST('2015-12-31' as DATE);

INSERT INTO Query9(
	SELECT P.pid, P.introdate, CAST(SUM(O.price * O.quantity) as NUMERIC(12,2)) AS totalsales
	FROM oldproduct P, orders O
	WHERE P.pid = O.pid AND O.status = 'S'
	GROUP BY P.pid, P.introdate
	ORDER BY P.introdate ASC
	);

DROP VIEW oldproduct;

-- Query 10 statements


