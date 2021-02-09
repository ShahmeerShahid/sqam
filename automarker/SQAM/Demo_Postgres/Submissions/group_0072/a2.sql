-- Add below your SQL statements. 
-- You can create intermediate views (as needed). Remember to drop these views after you have populated the result tables.
-- You can use the "\i a2.sql" command in psql to execute the SQL commands in this file.

-- Query 1 statements

    SELECT c.cid, c.cname, r.cid, r.cname
    FROM referral ref, customer c, customer r
    WHERE ref.custid = r.cid AND ref.custref = c.cid
    ORDER BY c.cname;


-- Query 2 statements

    SELECT o.oid, o.pid, s.wid, o.quantity, s.quantity
    FROM stock s, orders o
    WHERE o.status = 'O' AND o.shipwid = s.wid AND 
	o.pid = s.pid AND o.quantity > s.quantity;


-- Query 3 statements

    SELECT cid, cname, SUM(price*quantity)
    FROM orders NATURAL JOIN customer
    WHERE status = 'S'
    GROUP BY cid, cname
    ORDER BY SUM(price*quantity) DESC;


-- Query 4 statements

    SELECT pid, pname, SUM(cost*quantity)
    FROM orders NATURAL JOIN product
    WHERE status = 'S'
    GROUP BY pid, pname
    ORDER BY SUM(cost*quantity);


-- Query 5 statements

    SELECT pid, pname, introdate
    FROM product
    WHERE pid NOT IN(SELECT pid FROM orders)
    ORDER BY pname;


-- Query 6 statements

    SELECT cid, cname, lname
    FROM customer NATURAL JOIN location
    WHERE cid NOT IN(SELECT cid FROM orders)
    ORDER BY cname;


-- Query 7 statements

    SELECT CAST(TO_CHAR(odate, 'YYYYMM') AS INT) as period, SUM(quantity*price), 
	SUM(quantity*cost)
    FROM orders NATURAL JOIN product
    GROUP BY period
    ORDER BY period;


-- Query 8 statements

    SELECT ref.custid, c.cname, 
	SUM(ref.commission/100 * (o.quantity * o.price))
    FROM referral ref, customer c, customer r, orders o
    WHERE ref.custid = c.cid AND ref.custref = r.cid AND o.cid = r.cid
    GROUP BY ref.custid, c.cname
    ORDER BY c.cname;


-- Query 9 statements

    SELECT pid, introdate, SUM(quantity*price)
    FROM product NATURAL JOIN orders
    WHERE status = 'S' AND introdate <= '2015-12-31'
    GROUP BY pid, introdate
    ORDER BY introdate;


-- Query 10 statements

    SELECT l.lid, lname, COALESCE(SUM(quantity*price), 0)
    FROM (warehouse w RIGHT OUTER JOIN location l ON w.lid = l.lid) LEFT OUTER JOIN 
	(SELECT * FROM orders WHERE status = 'S') AS ord ON wid = ord.shipwid 
    GROUP BY l.lid, lname
    ORDER BY lname;


