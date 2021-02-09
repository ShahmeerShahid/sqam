-- Add below your SQL statements. 
-- You can create intermediate views (as needed). Remember to drop these views after you have populated the result tables.
-- You can use the "\i a2.sql" command in psql to execute the SQL commands in this file.

-- Query 1 statements
select referral.custid as cuid, customer.cname as cuname, referral.custref as refid, customer2.cname as refname from referral, customer, customer as customer2 where customer.cid=referral.custid and customer2.cid=referral.custref order by cuname asc;



-- Query 2 statements
select oid, orders.pid, wid, orders.quantity as ordqty, stock.quantity as stockqty from orders, stock where stock.quantity < orders.quantity and status='O' and orders.pid = stock.pid;



-- Query 3 statements
select cid as cuid, cname as cuname, sum(quantity*price) as totalsale from orders natural join customer where status='S' group by cuid, cuname order by totalsale desc;



-- Query 4 statements
select pid, pname as pame, sum(cost*quantity) as totalcost from product natural join orders where status='S' group by pid order by totalcost asc;


-- Query 5 statements
select pid, pname,introdate from product where pid not in (select pid from orders) order by pname ASC;


-- Query 6 statements
select customer.cid, cname, lname as locname from location natural join customer where customer.cid not in (select cid from orders) order by cname asc;


-- Query 7 statements
select cast(to_char(odate, 'yyyymm') as integer) as period, sum(price*quantity) as sales, sum(cost*quantity) as cost from orders natural join product group by period, price order by period asc;


-- Query 8 statements
select customer.cid, cname, sum(commission*price*quantity) from customer, referral, orders where customer.cid=referral.custref and orders.cid=referral.custref group by customer.cid order by cname asc;


-- Query 9 statements
select pid, introdate as date, price*quantity as totalsales from orders natural join product where introdate <= '2015/12/31' and status = 'S' order by introdate asc;


-- Query 10 statements
select location.lid, lname, coalesce(sum(price), 0) as totalsales from location left join warehouse on location.lid=warehouse.lid left join orders on warehouse.wid=orders.shipwid group by lname, location.lid order by lname asc;


