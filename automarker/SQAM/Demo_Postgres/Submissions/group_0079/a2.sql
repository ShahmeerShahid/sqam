-- Add below your SQL statements. 
-- You can create intermediate views (as needed). Remember to drop these views after you have populated the result tables.
-- You can use the "\i a2.sql" command in psql to execute the SQL commands in this file.

-- START Query 1
select r.custref as cuid, c1.cname as cuname, r.custid as refid, c2.cname as refname from referral r, customer c1, customer c2 where r.custref = c1.cid and r.custid = c2.cid order by c1.cname;

-- END Query 1
-- START Query 2
select o.oid as oid, o.pid as pid, s.wid as wid, CAST(o.quantity as NUMERIC(10,2)) as ordqty, CAST(s.quantity as NUMERIC(10,2)) as stockqty from orders o, stock s where o.status = 'O' and o.pid = s.pid and o.quantity > s.quantity;

-- END Query 2
-- START Query 3
select distinct c.cid as cuid, c.cname as cuname, CAST(SUM(o.quantity*o.price) as NUMERIC(12,2)) as totalsales from orders o, customer c where status = 'S' and o.cid = c.cid group by cuid order by totalsales desc;

-- END Query 3
-- START Query 4
 select distinct p.pid as pid, p.pname as pname, CAST(SUM(p.cost*o.quantity) as NUMERIC(12,2)) as totalcost from orders o, product p where status = 'S' and o.pid = p.pid group by p.pid order by totalcost;

-- END Query 4
-- START Query 5
select pid, pname, introdate from product where pid not in (select pid from orders) order by pname;

-- END Query 5
-- START Query 6
select cid, cname, lname as locname from customer, location where customer.lid = location.lid and cid not in (select cid from orders) order by cname;

-- END Query 6
-- START Query 7
select distinct CAST(CAST(EXTRACT(year from o.odate) as text) || CAST(EXTRACT(month from o.odate) as text) as INTEGER) as period, CAST(SUM(o.price * o.quantity) as NUMERIC(10,2)) as sales, CAST(SUM(p.cost * o.quantity) as NUMERIC(10,2)) as cost from orders o, product p where o.pid = p.pid group by period order by period;

-- END Query 7
-- START Query 8
select distinct customer.cid as cid, customer.cname as cname, CAST(SUM(commission*price*quantity) as NUMERIC(10,2)) as commission from referral, customer, orders where custid = customer.cid and orders.cid = custref group by customer.cid order by cname;

-- END Query 8
-- START Query 9
select product.pid as pid, introdate as date, CAST(SUM(price*quantity) as NUMERIC(12,2)) as totalsales from product, orders where status = 'S' and product.pid = orders.pid and introdate <= '2015-12-31' group by product.pid order by date;

-- END Query 9
-- START Query 10
SELECT * FROM (select l.lid, l.lname, CAST(SUM(price*quantity) as NUMERIC(12,2)) as sales FROM location l, orders o, warehouse w WHERE w.wid = o.shipwid and w.lid= l.lid and status='S' group by l.lid UNION select lid, lname, 0 as sales FROM location where lid NOT IN (Select lid FROM orders o, warehouse w WHERE w.wid = o.shipwid and status='S')) as foo ORDER BY lname;
-- END Query 10
