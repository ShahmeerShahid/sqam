SET search_path TO A2;


-- Query1
--
INSERT INTO query1(
   SELECT cu.cid, cu.cname, cref.cid, cref.cname
   FROM referral, customer AS cu, customer AS cref
   WHERE custref = cu.cid AND custid = cref.cid
   ORDER BY cu.cname
);
--
-- Query2
-- oid pid wid ordqty stockqty
INSERT INTO query2(
   SELECT o.oid, o.pid, o.shipwid, o.quantity, s.quantity
   FROM orders AS o, stock AS s
   WHERE s.wid = shipwid 
     AND o.pid = s.pid 
	 AND o.status = 'O'
	 AND o.quantity > s.quantity
);
--
-- Query3
--

INSERT INTO Query3(
   SELECT cid, cname, sum(price*quantity) as totalsales
   FROM (SELECT * FROM orders WHERE status='S') as orders 
        NATURAL JOIN customer
   GROUP BY cid, cname
   ORDER BY totalsales DESC);
   
-- Query4
-- pid, pname, total cost
INSERT INTO Query4(
   SELECT product.pid, pname, sum(quantity*cost) as totalcost
   FROM orders NATURAL JOIN product
   WHERE status = 'S'
   GROUP BY product.pid
   ORDER BY totalcost);
   
-- Query 5
-- pid, pname, introdate
INSERT INTO Query5(
   SELECT product.pid, pname, introdate 
   FROM (SELECT pid FROM orders) AS orders
        NATURAL FULL OUTER JOIN product
   WHERE orders.pid IS NULL
   GROUP BY product.pid
   ORDER BY pname);
   
-- Query 6
-- cid, cname, locname
 INSERT INTO Query6(
   SELECT customer.cid, customer.cname, location.lname
   FROM location NATURAL JOIN
     (SELECT cid, cname, lid
      FROM customer NATURAL FULL OUTER JOIN
         (SELECT cid FROM orders) AS orders
      WHERE orders.cid IS NULL) as customer
   GROUP BY customer.cid, customer.cname, location.lname
   ORDER BY customer.cname);

--Query 7
-- period, sales, cost
INSERT INTO Query7(
   SELECT (EXTRACT(YEAR FROM orders.odate)*100+EXTRACT(MONTH FROM orders.odate)) as period, 
          SUM(orders.sales) AS sales, SUM((product.cost*orders.quantity)) AS cost
   FROM product NATURAL JOIN
   (SELECT pid, odate, quantity, (quantity*price) AS sales
   FROM orders WHERE status = 'S') AS orders
   GROUP BY period
   ORDER BY period);
   
-- Query 8
-- cid, cname, commission
-- see post in piazza that explains calculation
 INSERT INTO Query8(SELECT customer.cid, customer.cname, SUM(commission.commission)
   FROM
     (SELECT custid as cid, (commission*orders.sales/100) AS commission
      FROM referral NATURAL JOIN
          (SELECT cid as custref, (quantity*price) AS sales
          FROM orders) AS orders) AS commission
     NATURAL JOIN customer
     GROUP BY customer.cid, customer.cname
     ORDER BY customer.cid);
	 
-- Query 9
-- pid, date, totalsales 
INSERT INTO Query9(
    SELECT product.pid, product.introdate, orders.sales
    FROM product NATURAL JOIN
    (SELECT pid, SUM(quantity*price) AS sales
	FROM orders  WHERE status = 'S'
	GROUP BY pid) AS orders
	WHERE product.introdate <= '2015-12-31'
	ORDER BY product.introdate);
	
-- Query 10
-- lid, lname, totalsales
-- note here we take outer join as explained in piazza and course web site.
INSERT INTO Query10(
   SELECT location.lid, location.lname, COALESCE(salesloc.sales, 0)
   FROM location NATURAL FULL OUTER JOIN
   (SELECT warehouse.lid AS lid, orders.sales AS sales
    FROM warehouse NATURAL JOIN
        (SELECT shipwid as wid, sum(quantity*price) AS sales 
		FROM orders WHERE status = 'S' GROUP BY shipwid) as orders) AS salesloc
   ORDER BY location.lname);