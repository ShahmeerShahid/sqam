-- Add below your SQL statements.
-- You can create intermediate views (AS needed). Remember to drop these views after you have populated the result tables.
-- You can use the "\i a2.sql" command in psql to execute the SQL commands in this file.

-- START Query 1
SELECT C1.cid AS cuid, C1.cname AS cname, C2.cid AS refid, C2.cname AS refname
                     FROM customer C1, customer C2, referral R
                     WHERE r.custref = C1.cid AND r.custid = C2.cid
                     ORDER BY C1.cname;



-- END Query 1
-- START Query 2
SELECT O.oid AS oid, O.pid AS pid , S.wid AS wid, O.quantity AS ordqty, S.quantity AS stockqty
                    FROM orders O, stock S
                    WHERE O.status = 'O' AND (O.quantity > S.quantity) AND O.shipwid = S.wid AND S.pid = O.pid
                   ;



-- END Query 2
-- START Query 3
SELECT C.cid AS cuid, C.cname AS cuname,  SUM(O.quantity * O.price) AS totalsales
                    FROM customer AS C, orders AS O
                    WHERE C.cid = O.cid AND O.status = 'S'
                    GROUP BY C.cid
                    ORDER by totalsales DESC
                  ;



-- END Query 3
-- START Query 4
SELECT P.pid AS pid, P.pname AS pname, SUM(O.quantity * P.cost) AS totalcost
                    FROM product P, orders O
                    WHERE P.pid = O.pid AND O.status = 'S'
                    GROUP BY P.pid
                    ORDER by totalcost ASC;



-- END Query 4
-- START Query 5
SELECT P.pid as pid, P.pname as pname, P.introdate as introdate
                    FROM product P
                    WHERE P.pid not in
                      (SELECT distinct pid
                       FROM orders O)
	            ORDER BY P.pname ASC;



-- END Query 5
-- START Query 6
SELECT C.cid as cid, C.cname as cname, L.lname as locname
                    FROM customer C, location L
                    WHERE C.lid = l.lid AND C.cid NOT in
                      (SELECT cid
                       FROM orders O
                       )
		    ORDER BY C.cname ASC;




-- END Query 6
-- START Query 7
SELECT CAST(to_char(O.odate, 'YYYYMM')AS Integer) AS period, SUM(O.quantity * O.price) AS sales, SUM(O.quantity * P.cost) AS cost
                    FROM product P, orders O
                    WHERE O.pid = P.pid AND O.status = 'S'
                    GROUP BY period
                    ORDER BY period ASC;


-- END Query 7
-- START Query 8
SELECT C.cid as cid, C.cname as cname, SUM(O.quantity * O.price * R.commission) as commisssion
                    FROM customer C, orders O, referral R
                    WHERE C.cid = R.custid AND O.cid = R.custref
                    GROUP BY C.cid
                    ORDER BY C.cname ASC;




-- END Query 8
-- START Query 9
SELECT P.pid AS pid, P.introdate AS date, SUM(O.price * O.quantity) as totalsales
                    FROM product P, orders O
                    WHERE O.pid = P.pid AND P.introdate <= '31 Dec 2015' AND O.status = 'S'
                    GROUP by P.pid
                    ORDER BY P.introdate ASC;


-- END Query 9
-- START Query 10
CREATE VIEW OrderNotShipped AS
(SELECT L.lid AS lid, L.lname as lname, 0 AS totalsales
 FROM location L, orders O, warehouse W
 WHERE W.wid = O.shipwid AND W.lid = L.lid AND O.status = 'O');

CREATE VIEW NoShippedWarehouse AS
(SELECT L.lid AS lid, L.lname AS lname, 0 AS totalsales
                     FROM location L
                      WHERE L.lid NOT IN
                        (SELECT shipwid
                          FROM orders));


CREATE VIEW WAREHOUSESHIPPED AS
  (SELECT L.lid AS lid, L.lname as lname, SUM(O.price * O.quantity) AS totalsales
                       FROM location L, orders O, warehouse W
                       WHERE W.wid = O.shipwid AND W.lid = L.lid AND O.status = 'S'
		       GROUP BY L.lid
		       );

CREATE VIEW UNIONSHIPPED AS
(SELECT *
FROM OrderNotShipped UNION (SELECT * FROM NoShippedWarehouse) UNION (SELECT * FROM WAREHOUSESHIPPED));

SELECT unionshipped.lid as lid, unionshipped.lname as lname, SUM(unionshipped.totalsales) as totalsales
		     FROM UNIONSHIPPED
		     GROUP BY unionshipped.lid, unionshipped.lname
		     ORDER by lname ASC;
DROP VIEW UNIONSHIPPED;
DROP VIEW OrderNotShipped;
DROP VIEW NoShippedWarehouse;
DROP VIEW WAREHOUSESHIPPED;
-- END Query 10
