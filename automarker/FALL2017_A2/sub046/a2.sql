-- Add below your SQL statements. 
-- You can create intermediate views (as needed). Remember to drop these views after you have populated the result tables.
-- You can use the "\i a2.sql" command in psql to execute the SQL commands in this file.



-- Query 1 statements
INSERT INTO Query1 (SELECT DISTINCT R1.custref AS cuid, C2.cname AS cuname, R1.custid AS refid, C1.cname AS refname
		   FROM referral R1, customer C1, customer C2
		   WHERE R1.custid = C1.cid AND R1.custref = C2.cid
                   ORDER BY C2.cname ASC);


-- Query 2 statements
INSERT INTO Query2 (SELECT O.oid AS oid, O.pid AS pid, O.shipwid AS wid, O.quantity AS ordqty, S.quantity AS stockqty
		    FROM orders O, stock S
		    WHERE S.quantity < O.quantity AND O.status = 'O' AND O.pid = S.pid AND O.shipwid = S.wid);


-- Query 3 statements
INSERT INTO Query3 (SELECT C.cid AS cuid, C.cname AS cuname, SUM(O.quantity * O.price) AS totalsales 
		    FROM customer C, orders O
		    WHERE O.status = 'S' AND C.cid = O.cid
		    GROUP BY cuid
	            ORDER BY totalsales DESC);


-- Query 4 statements
INSERT INTO Query4 (SELECT P.pid AS pid, P.pname AS pname, SUM(P.cost * O.quantity) AS totalcost
		    FROM product P, orders O
		    WHERE O.status = 'S' AND P.pid = O.pid
		    GROUP BY P.pid
                    ORDER BY totalcost ASC);


-- Query 5 statements
INSERT INTO Query5 (SELECT P.pid, P.pname, P.introdate 
		    FROM product P 
		    WHERE P.pid NOT IN 
			(SELECT O.pid FROM orders O)
		    ORDER BY P.pname);


-- Query 6 statements
INSERT INTO Query6 (SELECT C.cid AS cid, C.cname AS cname, L.lname AS locname
 		    FROM customer C, location L 
		    WHERE L.lid = C.lid AND C.cid NOT IN 
					(SELECT O.cid FROM orders O)
		    ORDER BY cname ASC);


-- Query 7 statements
INSERT INTO Query7 (SELECT CAST (to_char(O.odate, 'YYYYMM') AS Integer) AS period, SUM(O.price * O.quantity) AS totalsales, SUM(P.cost * O.quantity) AS totalcost
		   FROM orders O, product P
		   WHERE P.pid = O.pid AND O.status = 'S'
                   GROUP BY period
                   ORDER BY period ASC);
      

-- Query 8 statements
CREATE VIEW purchases as
SELECT r.custid, SUM(r.commission * o.quantity * o.price) AS commission
FROM referral r, orders o
WHERE r.custref = o.cid
GROUP BY r.custid, o.quantity, o.price, r.commission;

INSERT INTO Query8 (SELECT p.custid, c.cname, SUM(p.commission)
		    FROM purchases p, customer c
		    WHERE p.custid = c.cid
                    GROUP BY p.custid, c.cname
	            ORDER BY c.cname ASC);

DROP VIEW purchases;

-- Query 9 statements
INSERT INTO Query9 (SELECT P.pid AS pid, P.introDate as date, SUM(O.quantity * O.price) AS totalsales
		    FROM product P, orders O
                    WHERE O.status = 'S' AND O.pid = P.pid AND P.introdate <= '31 Dec 2015'
		    GROUP BY P.pid
		    ORDER BY P.introdate ASC);


-- Query 10 statements
CREATE VIEW ShippedOrder as
	SELECT L.lid AS lid, L.lname AS lname, SUM(O.quantity * O.price) AS totalsales
	FROM orders O, warehouse W, location L
	WHERE O.status = 'S' AND O.shipwid = W.wid AND W.lid = L.lid
	GROUP BY L.lid, L.lname;


CREATE VIEW NotInOrder as
	SELECT L.lid AS lid, L.lname AS lname, 0 AS totalsales
	FROM warehouse W, location L, orders O
	WHERE O.shipwid = W.wid AND L.lid NOT IN 
				(SELECT lid FROM ShippedOrder)
	GROUP BY L.lid, L.lname;

INSERT INTO Query10 ((SELECT *
		     FROM ShippedOrder) UNION 
		     (SELECT * FROM NotInOrder)
		      ORDER BY lname
		     );

DROP VIEW NotInOrder;
DROP VIEW ShippedOrder;









