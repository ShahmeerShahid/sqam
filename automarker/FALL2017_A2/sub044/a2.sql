-- Add below your SQL statements.
-- You can create intermediate views (as needed). Remember to drop these views after you have populated the result tables.
-- You can use the "\i a2.sql" command in psql to execute the SQL commands in this file.

-- Query 1 statements
CREATE VIEW mytable AS
    SELECT C1.cid AS cuid, C1.cname AS cuname, C2.cid AS refid, C2.cname AS refname, R.custid, R.custref
    FROM customer C1, customer C2, referral R
    WHERE C1.cid = R.custref AND C2.cid = R.custid;

INSERT INTO QUERY1
    (SELECT cuid, cuname, refid, refname
    FROM mytable
    ORDER BY cuname ASC);

DROP VIEW mytable;

-- Query 2 statements
CREATE VIEW stock_orders AS SELECT o.oid as oid, o.pid as pid, s.wid as wid, o.quantity as ordqty, s.quantity as stockqty FROM orders AS o JOIN stock AS s ON o.pid = s.pid WHERE o.status='O';
INSERT INTO QUERY2 (SELECT * FROM stock_orders WHERE ordqty > stockqty);
DROP VIEW stock_orders;

-- Query 3 statements
CREATE VIEW sales AS
    SELECT cid, SUM(price*quantity) AS totalsales
    FROM orders
    WHERE status = 'S'
    GROUP BY cid;

CREATE VIEW custSales AS
    SELECT cid, cname, totalsales
    FROM customer NATURAL JOIN sales;

INSERT INTO QUERY3
    (SELECT cid AS cuid, cname AS cuname, totalsales
    FROM custSales
    ORDER BY totalsales DESC);

DROP VIEW custSales;
DROP VIEW sales;

-- Query 4 statements
CREATE VIEW sales AS SELECT * FROM product NATURAL JOIN orders WHERE status = 'S';
CREATE VIEW costs AS SELECT pid, pname, sum(cost * quantity) AS totalcost FROM sales GROUP BY (pid, pname);
INSERT INTO QUERY4 (SELECT * FROM costs ORDER BY totalcost ASC);
DROP VIEW costs;
DROP VIEW sales;

-- Query 5 statements
CREATE VIEW prods AS
    SELECT pid, pname, introdate
    FROM product
    WHERE pid NOT IN (SELECT pid FROM orders);

INSERT INTO Query5
    (SELECT *
    FROM prods
    ORDER BY pname ASC);

DROP VIEW prods;

-- Query 6 statements
CREATE VIEW customer_orders AS SELECT * FROM customer WHERE cid NOT IN (SELECT cid FROM orders);
INSERT INTO QUERY6 (SELECT cid, cname, lid as locname FROM customer_orders ORDER BY cname ASC);
DROP VIEW customer_orders;

-- Query 7 statements
CREATE VIEW tableA AS
    SELECT CAST(((to_char(odate,'yyyy')::text)||to_char(odate,'mm')::text) AS integer) AS period,
    SUM(quantity*price) AS sales, SUM(quantity*cost) AS cost
    FROM product NATURAL JOIN orders
    GROUP BY period; 

INSERT INTO QUERY7
    (SELECT *
    FROM tableA
    ORDER BY period ASC);

DROP VIEW tableA;

-- Query 8 statements
CREATE VIEW referrals AS SELECT * FROM customer NATURAL JOIN referral WHERE customer.cid = referral.custid;
CREATE VIEW sales as SELECT referrals.cid, referrals.cname, sum(commission*quantity*price)
    FROM referrals JOIN orders ON referrals.custref = orders.cid
    GROUP BY(cname, referrals.cid);
INSERT INTO QUERY8 (SELECT * FROM sales ORDER BY cname ASC);
DROP VIEW sales;
DROP VIEW referrals;


-- Query 9 statements

CREATE VIEW salesTable AS
    SELECT pid, SUM(quantity*price) AS sales
    FROM orders
    WHERE status = 'S'
    GROUP BY pid;

CREATE VIEW prods AS
    SELECT pid, introdate AS date, sales AS totalsales
    FROM product NATURAL JOIN salesTable
    WHERE introdate <= '2015-12-31';

INSERT INTO QUERY9
    (SELECT *
    FROM prods
    ORDER BY date ASC);

DROP VIEW prods;
DROP VIEW salesTable;

-- Query 10 statements

CREATE VIEW warehouseSales AS
    SELECT shipwid AS wid, sum(price*quantity) AS sales
    FROM orders
    WHERE status = 'S'
    GROUP BY shipwid;

CREATE VIEW loc AS
    SELECT lid, lname, wid
    FROM location NATURAL LEFT OUTER JOIN warehouse;

CREATE VIEW locSales AS
    SELECT lid, lname, coalesce(sales, 0) AS sales
    FROM loc NATURAL LEFT OUTER JOIN warehouseSales;

INSERT INTO QUERY10
    (SELECT lid, lname, SUM(sales) AS totalsales
    FROM locSales
    GROUP BY (lid, lname)
    ORDER BY lname ASC);

DROP VIEW locSales;
DROP VIEW loc;
DROP VIEW warehouseSales;
