-- Add below your SQL statements. 
-- You can create intermediate views (as needed). Remember to drop these views after you have populated the result tables.
-- You can use the "\i a2.sql" command in psql to execute the SQL commands in this file.

-- START Query 1

create view q1 as (select * from customer);
create view q2 as (select * from customer);
select q1.cid, q1.cname, q2.cid, q2.cname from referral, q1, q2 where q1.cid = custref AND q2.cid = custid ORDER BY q1.cname ASC;
drop view q1;
drop view q2;


-- END Query 1
-- START Query 2

select oid, s.pid, s.wid, o.quantity, s.quantity from stock s, orders o where shipwid = s.wid and o.pid = s.pid and o.quantity > s.quantity and status = 'O';

-- END Query 2
-- START Query 3

select c.cid, cname, SUM(quantity*price) as sales from orders, customer c where orders.cid = c.cid and status = 'S' group by c.cid order by sales DESC;

-- END Query 3
-- START Query 4

select c.cid, cname, SUM(quantity*price) as sales from orders, customer c where orders.cid = c.cid and status = 'S' group by c.cid;

-- END Query 4
-- START Query 5

select pid, pname, introdate from product p where not exists (select pid from orders where p.pid = pid) order by p.pname;


-- END Query 5
-- START Query 6

select cid, cname, lid from customer c where not exists (select cid from orders where c.cid = cid) order by cname;

-- END Query 6
-- START Query 7



-- END Query 7
-- START Query 8

select c.cid, c.cname, sum(r.commission * o.quantity * o.price) as com from customer c, referral r, orders o where c.cid = r.custid and o.cid = r.custref group by c.cid order by c.cname asc;

-- END Query 8
-- START Query 9

select p.pid, p.introdate, SUM(o.quantity * o.price) as totalsales from orders o, product p where o.pid = p.pid and p.introdate <= '2015-12-31' and o.status = 'S' group by p.pid order by p.introdate asc;

-- END Query 9
-- START Query 10

create view q10 as (select shipwid, SUM(quantity*price) as sales from orders GROUP BY shipwid);
select location.lid, location.lname, sales from warehouse, q10, location where wid = shipwid order by location.lname ASC;
drop view q10;


-- END Query 10
