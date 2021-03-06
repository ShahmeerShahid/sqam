-- Add below your SQL statements. 
-- You can create intermediate views (as needed). Remember to drop these views after you have populated the result tables.
-- You can use the "\i a2.sql" command in psql to execute the SQL commands in this file.

-- START Query 1

SELECT c1.cid AS cuid, c1.cname AS cuname, c2.cid AS refid, c2.cname AS refname
FROM customer c1, customer c2, referral r
WHERE c1.cid = r.custid AND c2.cid = r.custref
ORDER BY c1.cname ASC
;



-- END Query 1
-- START Query 2

SELECT o.oid, o.pid, o.shipwid as wid, o.quantity as ordqty, s.quantity as stockqty 
FROM Orders o NATURAL JOIN stock s
WHERE o.quantity > s.quantity AND status = 'O'
;


-- END Query 2
-- START Query 3

SELECT cu.cid as cuid, cu.cname as cuname, SUM(price*quantity) as totalsales
FROM Customer cu NATURAL JOIN Orders o
WHERE cu.cid = o.cid AND status = 'S'
GROUP BY cu.cid
ORDER BY totalsales desc
;




-- END Query 3
-- START Query 4

SELECT p.pid as pid, p.pname as pame, SUM(p.cost*o.quantity) as totalcost
FROM product p JOIN orders o
ON p.pid = o.pid
WHERE status = 'S'
GROUP BY p.pid
ORDER BY totalcost ASC
;



-- END Query 4
-- START Query 5

SELECT p.pid as pid, p.pname as pame , introdate
FROM product p LEFT JOIN orders o
ON p.pid = o.pid --maybe dont check pid equals
WHERE o.oid IS NULL 
ORDER BY p.pname ASC
;


-- END Query 5
-- START Query 6

SELECT distinct c.cid as cid, c.cname as cname, l.lname as locname --might need distinct
FROM customer c 
JOIN location l
ON c.lid = l.lid
LEFT JOIN orders o
ON c.cid = o.cid --is needed?

WHERE o.oid IS NULL
GROUP BY c.cid --is needed? needs to be tested
ORDER BY c.cname ASC
;


--sub q c X o then X l

-- END Query 6
-- START Query 7



-- END Query 7
-- START Query 8

SELECT c.cid as cid, c.cname as cname, SUM(o.quantity*o.price*r.commission) as comission
FROM customer c JOIN referral r
ON c.cid = r.custid
JOIN orders o
ON r.custref = o.cid
GROUP BY c.cid 
ORDER BY c.cname ASC
;


-- END Query 8
-- START Query 9

SELECT p.pid as pid , introdate as date, SUM(price*quantity) as totalsales --date is key word,
FROM product p JOIN orders o
ON p.pid = o.pid
WHERE introdate <= '20151231'
GROUP BY p.pid
ORDER BY introdate ASC
;



-- END Query 9
-- START Query 10
 
INSERT INTO Query10(
SELECT l.lid , lname, SUM(o.price*o.quantity) as totalsales 
FROM location l LEFT OUTER JOIN warehouse w
ON w.lid = l.lid
LEFT JOIN Orders o
ON w.wid = o.shipwid
WHERE status = 'S'
GROUP BY l.lid
ORDER BY lname ASC;
);



-- END Query 10
