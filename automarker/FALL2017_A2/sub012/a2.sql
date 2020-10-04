-- Add below your SQL statements. 
-- You can create intermediate views (as needed). Remember to drop these views after you have populated the result tables.
-- You can use the "\i a2.sql" command in psql to execute the SQL commands in this file.

-- Set search path
SET search_path TO A2;

-- Query 1 statements
INSERT INTO Query1 (
  SELECT
    ref.custref refid,
    cref.cname  refname,
    ref.custid  cuid,
    c.cname     cuname
  FROM referral ref, customer c, customer cref
  WHERE c.cid = ref.custid AND cref.cid = ref.custref
  ORDER BY cuname ASC
);

-- Query 2 statements
INSERT INTO Query2 (
  SELECT
    o.oid,
    o.pid,
    o.shipwid  wid,
    o.quantity ordqty,
    s.quantity stockqty
  FROM orders o, stock s
  WHERE o.shipwid = s.wid
        AND o.pid = stock.pid
        AND o.status = 'O'
        AND o.quantity > s.quantity
);

-- Query 3 statements
INSERT INTO Query3 (
  SELECT
    c.cid                     cuid,
    c.cname                   cuname,
    SUM(o.quantity * o.price) totalsales
  FROM customer c, orders o
  WHERE c.cid = o.cid
        AND o.status = 'S'
  GROUP BY c.cid, c.cname
  ORDER BY totalsales DESC
);

-- Query 4 statements
INSERT INTO Query4 (
  SELECT
    o.pid,
    p.pname,
    SUM(o.quantity * p.cost) totalcost
  FROM orders o, product p
  WHERE o.pid = p.pid
        AND status = 'S'
  GROUP BY o.pid, p.pname
  ORDER BY totalcost ASC
);

-- Query 5 statements
INSERT INTO Query5 (
  SELECT
    pid,
    pname,
    introdate -- asgt says 'pame' but prof said pname on Piazza
  FROM product p
  WHERE NOT EXISTS(-- get only products which have never been ordered
      SELECT *
      FROM orders o
      WHERE o.pid = p.pid
  )
  ORDER BY pname ASC
);

-- Query 6 statements
INSERT INTO Query6 (
  SELECT
    c.cid,
    c.cname,
    l.lname locname
  FROM customer c, location l
  WHERE NOT EXISTS(-- choose only customers without orders
      SELECT *
      FROM orders o
      WHERE o.cid = c.cid
  )
  ORDER BY cname ASC
);

-- Query 7 statements
INSERT INTO Query7 (
  SELECT
    to_char(o.odate, 'YYYYMM') period,
    SUM(o.quantity * o.price)  sales,
    SUM(o.quantity * p.cost)   cost
  FROM orders o, product p
  WHERE o.pid = p.pid
        AND o.status = 'S'
  GROUP BY period
  ORDER BY period ASC
);

-- Query 8 statements
INSERT INTO Query8 (
  SELECT
    c.cid,
    c.cname,
    SUM(oref.quantity * oref.price) * (r.commission / 100) commission
  FROM customer c, referral r, orders oref
  WHERE c.cid = r.custid
        AND oref.cid = r.custref
  GROUP BY c.cid, c.cname
  ORDER BY c.cname ASC
);

-- Query 9 statements
INSERT INTO Query9 (
  SELECT
    p.pid,
    p.introdate               date,
    SUM(o.quantity * o.price) totalsales
  FROM product p, orders o
  WHERE p.pid = o.pid
        AND o.status = 'S'
        AND p.introdate < '2015-12-31' :: DATE
  GROUP BY p.pid, p.introdate
  ORDER BY date ASC
);

-- Query 10 statements
INSERT INTO Query10 (
  SELECT
    w.lid,
    location.lname,
    SUM(o.quantity * o.price) totalsales
  FROM warehouse w, location l, orders o, product p
  WHERE o.pid = p.pid
        AND o.shipwid = w.wid
        AND w.lid = l.lid
        AND o.status = 'S'
  GROUP BY w.lid, location.lname
  ORDER BY lname ASC
);

