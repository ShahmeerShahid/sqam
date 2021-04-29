-- START Query 1

SELECT cu.cid, cu.cname, cref.cid, cref.cname
   FROM referral, customer AS cu, customer AS cref
   WHERE custref = cu.cid AND custid = cref.cid
   ORDER BY cu.cname;
-- END Query 1

-- START Query 2
SELECT o.oid, o.pid, o.shipwid, o.quantity, s.quantity
   FROM orders AS o, stock AS s
   WHERE s.wid = shipwid 
     AND o.pid = s.pid 
	 AND o.status = 'O'
	 AND o.quantity > s.quantity;
-- END Query 2

-- START Query 3
SELECT cid, cname, sum(price*quantity) as totalsales
   FROM (SELECT * FROM orders WHERE status='S') as orders 
        NATURAL JOIN customer
   GROUP BY cid, cname
   ORDER BY totalsales DESC;
-- END Query 3

-- START Query 4

SELECT product.pid, pname, sum(quantity*cost) as totalcost
   FROM orders NATURAL JOIN product
   WHERE status = 'S'
   GROUP BY product.pid
   ORDER BY totalcost;
-- END Query 4

-- START Query 5

SELECT product.pid, pname, introdate
   FROM (SELECT pid FROM orders) AS orders
        NATURAL FULL OUTER JOIN product
   WHERE orders.pid IS NULL
   GROUP BY product.pid
   ORDER BY pname;
-- END Query 5

-- START Query 6

SELECT customer.cid, customer.cname, location.lname
   FROM location NATURAL JOIN
     (SELECT cid, cname, lid
      FROM customer NATURAL FULL OUTER JOIN
         (SELECT cid FROM orders) AS orders
      WHERE orders.cid IS NULL) as customer
   GROUP BY customer.cid, customer.cname, location.lname
   ORDER BY customer.cname;
-- END Query 6

-- START Query 7

SELECT (EXTRACT(YEAR FROM orders.odate)*100+EXTRACT(MONTH FROM orders.odate)) as period,
          SUM(orders.sales) AS sales, SUM((product.cost*orders.quantity)) AS cost
   FROM product NATURAL JOIN
   (SELECT pid, odate, quantity, (quantity*price) AS sales
   FROM orders WHERE status = 'S') AS orders
   GROUP BY period
   ORDER BY period;
-- END Query 7

-- START Query 8

SELECT customer.cid, customer.cname, SUM(commission.commission)
   FROM
     (SELECT custid as cid, (commission*orders.sales/100) AS commission
      FROM referral NATURAL JOIN
          (SELECT cid as custref, (quantity*price) AS sales
          FROM orders) AS orders) AS commission
     NATURAL JOIN customer
     GROUP BY customer.cid, customer.cname
     ORDER BY customer.cid;
-- END Query 8

-- START Query 9

SELECT product.pid, product.introdate, orders.sales
    FROM product NATURAL JOIN
    (SELECT pid, SUM(quantity*price) AS sales
	FROM orders  WHERE status = 'S'
	GROUP BY pid) AS orders
	WHERE product.introdate <= '2015-12-31'
	ORDER BY product.introdate;
-- END Query 9

-- START Query 10

SELECT location.lid, location.lname, COALESCE(salesloc.sales, 0)
   FROM location NATURAL FULL OUTER JOIN
   (SELECT warehouse.lid AS lid, orders.sales AS sales
    FROM warehouse NATURAL JOIN
        (SELECT shipwid as wid, sum(quantity*price) AS sales 
		FROM orders WHERE status = 'S' GROUP BY shipwid) as orders) AS salesloc
   ORDER BY location.lname;
   -- END Query 10
