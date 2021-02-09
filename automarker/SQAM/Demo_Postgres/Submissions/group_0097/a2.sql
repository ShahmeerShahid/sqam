-- TODO: Verify Joins, Write Test cases, 

-- Add below your SQL statements. 
-- You can create intermediate views (as needed). Remember to drop these views after you have populated the result tables.
-- You can use the "\i a2.sql" command in psql to execute the SQL commands in this file.

-- Query 1 statements
 SELECT custid cuid, C1.cname cuname, custref refid, C2.cname refname FROM referral, customer C1, customer C2 WHERE C1.cid=custid AND C2.cid=custref ORDER BY C1.cname ASC; 


-- Query 2 statements
--  Find unshipped orders which cannot be shipped from their designated warehouse, that is, order with a quantity strictly less than
-- the quantity found in the stock for the product in this order. Report the order ID, product id, the warehouse ID, order quantity, and available
-- stock quantity.
-- order quantity > warehouse quantity
-- order ID, product id, the warehouse ID, order quantity, 
-- and available stock quantity

SELECT oid, o.pid, shipwid wid, o.quantity ordqty, s.quantity stockqty from orders o join stock s ON s.wid = o.shipwid WHERE s.quantity < o.quantity AND s.pid = o.pid AND o.status = 'O';

-- Query 3 statements
-- Find total sales by customer and sort in descending order
-- Order must be shipped

SELECT o.cid AS cuid, c.cname AS cuname, SUM(o.quantity * o.price) AS totalsales FROM orders o NATURAL JOIN customer c WHERE o.status = 'S' GROUP BY o.cid, c.cname ORDER BY totalsales DESC;;

-- Query 4 statements
-- Cost of sales by product so number sold * cost of the product??
-- Sort in increasing order
-- Must be shipped

SELECT o.pid AS pid, p.pname AS pame, SUM(o.quantity * p.cost) AS totalcost from orders o NATURAL JOIN product p WHERE o.status = 'S' GROUP BY o.pid, p.pname ORDER BY totalcost ASC;


-- Query 5 statements

SELECT p.pid AS pid, p.pname AS pame, p.introdate AS introdate FROM product p WHERE p.pid NOT IN (SELECT pid from orders) ORDER BY p.pname ASC;


-- Query 6 statements

SELECT c.cid AS cid, c.cname AS cname, l.lname AS locname FROM customer c NATURAL JOIN location l WHERE c.cid NOT IN (SELECT cid FROM orders) ORDER BY c.cname ASC;


-- Query 7 statements

-- SELECT period, sales, cost FROM orders;
 SELECT o.exdate period, SUM(o.sales) AS sales, SUM(p.cost * o.quantity) AS cost FROM (SELECT extract(year from odate)*100 + extract(month from odate) AS exdate, sum(price*quantity) AS sales, status, pid, quantity FROM orders GROUP BY pid, odate, status, quantity) o NATURAL JOIN product p WHERE o.status = 'S' GROUP BY period ORDER BY period ;

-- Query 8 statements
-- Report commission earned by customers, doesn't matter if shipped'
-- ARE we supposed to report the total amount earned, the commission amount or some other value??

SELECT custid cid, cname, SUM(totalsales*commission) commision FROM referral R JOIN customer C ON R.custid=C.cid JOIN (SELECT o.cid cid, SUM(o.quantity * o.price) totalsales FROM orders o WHERE o.status = 'S' GROUP BY o.cid) S ON S.cid=C.cid GROUP BY R.custref, C.cname, R.custid ORDER BY cname ASC;

-- Check referral

-- Query 9 statements
-- Sales introduced on/before 31 DEC 2015
-- Sort in increasing order of introdate

SELECT o.pid AS pid, p.introdate AS date, SUM(o.quantity * o.price) AS totalsales FROM orders o JOIN product p ON o.pid = p.pid.introdate <= '2015-12-31' AND o.status = 'S' GROUP BY o.pid, p.introdate ORDER BY date ASC;


-- Query 10 statements
-- Sales grouped by location
-- use lid in customer to get location

SELECT l.lid AS lid, l.lname AS lname, SUM(o.price * o.quantity) AS totalsales FROM orders o JOIN warehouse w ON o.shipwid = w.wid JOIN location l ON w.lid = l.lid GROUP BY l.lid, l.lname ORDER BY lname ASC;
