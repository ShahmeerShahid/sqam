-- Add below your SQL statements. 
-- You can create intermediate views (as needed). Remember to drop these views after you have populated the result tables.
-- You can use the "\i a2.sql" command in psql to execute the SQL commands in this file.

-- START Query 1

-----Refferer View
CREATE VIEW referrerview AS SELECT c.cid, c.cname 
				FROM customer c, referral r
				WHERE r.custid = c.cid;

-----Referred view
CREATE VIEW referredview AS SELECT c.cid, c.cname
				FROM customer c, referral r
				WHERE r.custref = c.cid;


SELECT DISTINCT referredview.cid AS "cuid", referredview.cname AS "cuname", referrerview.cid AS "refid", referrerview.cname AS "refname"
			FROM referrerview, referredview, referral
			WHERE referrerview.cid  = referral.custid AND referredview.cid = referral.custref
			ORDER BY referredview.cname;
DROP VIEW referrerview;
DROP VIEW referredview;
-- END Query 1
-- START Query 2

SELECT o.oid, o.pid, s.wid, o.quantity AS "ordqty", s.quantity AS "stockqty"
		  	 FROM orders o, stock s
		   	WHERE o.pid = s.pid AND o.quantity > s.quantity AND o.shipwid = s.wid;

-- END Query 2
-- START Query 3

SELECT c.cid AS "cuid", c.cname AS "cuname", sum(o.quantity*o.price) AS "totalsales" 
		   	FROM customer c, orders o 
			WHERE c.cid = o.cid AND o.status = 'S'
			GROUP BY cuid 
			ORDER BY totalsales DESC; 

-- END Query 3
-- START Query 4

SELECT p.pid, p.pname, sum(o.quantity*p.cost) AS "totalcost"
			FROM product p, orders o
			WHERE p.pid = o.pid AND o.status = 'S'
			GROUP BY p.pid
			ORDER BY totalcost;

-- END Query 4
-- START Query 5

SELECT pid, pname, introdate
			FROM product
			WHERE pid NOT IN (SELECT DISTINCT pid FROM orders)
			ORDER BY pname;

-- END Query 5
-- START Query 6

SELECT c.cid, c.cname, l.lname AS "locname"
			FROM customer c, location l
			WHERE c.cid NOT IN (SELECT DISTINCT cid FROM orders) AND c.lid = l.lid
			ORDER BY c.cname;

-- END Query 6
-- START Query 7
SELECT CAST(TO_CHAR(o.odate, 'YYYYMM') AS NUMERIC(10,2)) AS "period", SUM(o.quantity*o.price) AS "sales", SUM(p.cost * o.quantity) AS "cost"
			FROM orders o, product p
			WHERE o.pid = p.pid AND o.status = 'S'
			GROUP BY period
			ORDER BY period; 

-- END Query 7
-- START Query 8

----------------Total price view
CREATE VIEW totalpriceview AS (SELECT cid, SUM(price*quantity) AS "sales" 
				FROM orders
				GROUP BY cid);

SELECT c.cid, c.cname, SUM(tp.sales*r.commission) AS "commission"
			FROM referral r, totalpriceview tp, customer c
			WHERE r.custref = tp.cid AND r.custid = c.cid
			GROUP BY c.cid
			ORDER BY c.cname;
DROP VIEW totalpriceview;

--------------------------------

-- END Query 8
-- START Query 9

SELECT p.pid, p.introdate AS "date", SUM(o.quantity*o.price) AS "totalsales"
			FROM orders o, product p
			WHERE p.pid = o.pid AND o.status = 'S' AND p.introdate <= to_date('31 Dec 2015', 'DD Mon YYYY')
			GROUP BY p.pid 
			ORDER BY p.introdate; 
			
-- END Query 9
-- START Query 10

-------------Total sales view
CREATE VIEW totalsalesview AS (SELECT orders.shipwid, SUM(orders.quantity*orders.price) AS "totalsales"
				FROM orders	
				WHERE orders.status = 'S'
				GROUP BY orders.shipwid);
CREATE VIEW Query10View AS (SELECT l.lid, l.lname, SUM(tpv.totalsales) AS "totalsales"
                        FROM warehouse w
                        LEFT OUTER JOIN totalsalesview tpv ON tpv.shipwid = w.wid
                        RIGHT OUTER JOIN location l ON w.lid = l.lid
                        GROUP BY l.lid
                        ORDER BY l.lname);


SELECT lid, lname, COALESCE(totalsales,0)
			FROM Query10View;
DROP VIEW Query10View;
DROP VIEW totalsalesview;
-- END Query 10
