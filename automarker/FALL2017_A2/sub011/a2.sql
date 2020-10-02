-- Add below your SQL statements. 
-- You can create intermediate views (as needed). Remember to drop these views after you have populated the result tables.
-- You can use the "\i a2.sql" command in psql to execute the SQL commands in this file.

-- Query 1 statements

create view q1 as (select * from customer);
create view q2 as (select * from customer);
INSERT INTO query1(select q1.cid, q1.cname, q2.cid, q2.cname from referral, q1, q2 where q1.cid = custref AND q2.cid = custid ORDER BY q1.cname ASC);
drop view q1;
drop view q2;


-- Query 2 statements

INSERT INTO query2(select oid, s.pid, s.wid, o.quantity, s.quantity from stock s, orders o where shipwid = s.wid and o.pid = s.pid and o.quantity > s.quantity and status = 'O');

-- Query 3 statements

INSERT INTO query3(select c.cid, cname, SUM(quantity*price) as sales from orders, customer c where orders.cid = c.cid and status = 'S' group by c.cid order by sales DESC);

-- Query 4 statements

INSERT into query4(select c.cid, cname, SUM(quantity*price) as sales from orders, customer c where orders.cid = c.cid and status = 'S' group by c.cid);

-- Query 5 statements

INSERT INTO query5 (select pid, pname, introdate from product p where not exists (select pid from orders where p.pid = pid) order by p.pname);


-- Query 6 statements

INSERT INTO query6 (select cid, cname, lid from customer c where not exists (select cid from orders where c.cid = cid) order by cname);

-- Query 7 statements



-- Query 8 statements

INSERT INTO query8 (select c.cid, c.cname, sum(r.commission * o.quantity * o.price) as com from customer c, referral r, orders o where c.cid = r.custid and o.cid = r.custref group by c.cid order by c.cname asc);

-- Query 9 statements

INSERT into query9 (select p.pid, p.introdate, SUM(o.quantity * o.price) as totalsales from orders o, product p where o.pid = p.pid and p.introdate <= '2015-12-31' and o.status = 'S' group by p.pid order by p.introdate asc);

-- Query 10 statements

create view q10 as (select shipwid, SUM(quantity*price) as sales from orders GROUP BY shipwid);
INSERT into query10 (select location.lid, location.lname, sales from warehouse, q10, location where wid = shipwid order by location.lname ASC);
drop view q10;


