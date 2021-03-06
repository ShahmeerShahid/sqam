-- Add below your SQL statements. 
-- You can create intermediate views (as needed). Remember to drop these views after you have populated the result tables.
-- You can use the "\i a2.sql" command in psql to execute the SQL commands in this file.

-- START Query 1
create view customer_names as 
select * from referral 
JOIN customer 
on referral.custid = customer.cid;

select customer_names.custref as cuid,
customer.cname as cuname, 
customer_names.cid as refid, 
customer_names.cname as refname 
from customer INNER JOIN 
customer_names ON customer.cid = customer_names.custref
order by cuname;

drop view customer_names;


-- END Query 1
-- START Query 2

select oid, orders.pid as pid,
orders.shipwid as wid, 
orders.quantity as ordqty, 
stock.quantity as stockqty 
from orders join stock 
on orders.pid = stock.pid 
where orders.shipwid = stock.wid 
AND orders.status = 'O' 
AND orders.quantity > stock.quantity;



-- END Query 2
-- START Query 3

select customer.cid as cuid,
customer.cname as cuname, 
sum(orders.price*orders.quantity) as totalsales 
from customer left join orders 
on customer.cid = orders.cid 
where orders.status = 'S'  
group by customer.cid, customer.cname 
order by totalsales DESC;


-- END Query 3
-- START Query 4

select product.pid as pid,
product.pname as cuname, 
sum(product.cost*orders.quantity) as totalcost 
from product left join orders 
on product.pid = orders.pid 
where orders.status = 'S'  
group by product.pid, product.pname 
order by totalcost ASC;

-- END Query 4
-- START Query 5

select product.pid, pname, introdate
from product join 
(select pid from product 
EXCEPT select orders.pid 
from orders JOIN product 
ON orders.pid = product.pid 
group by orders.pid) 
as prod on product.pid = prod.pid 
order by pname ASC; 


-- END Query 5
-- START Query 6

select cid, cname, lname from
(select customer.cid, cname, lid 
from customer join 
(select cid 
from customer EXCEPT select orders.cid 
from orders JOIN customer 
ON orders.cid = customer.cid 
group by orders.cid) 
as cust on customer.cid = cust.cid) 
as cstm left join location 
on cstm.lid = location.lid 
order by cname ASC; 



-- END Query 6
-- START Query 7
select to_char(orders.odate, 'YYYYMM') as period,
sum(orders.quantity*orders.price) as sales, 
sum(product.cost*orders.quantity) as cost 
from product left join orders 
on product.pid = orders.pid 
where orders.status = 'S'  
group by period 
order by period ASC;


-- END Query 7
-- START Query 8

create view ref_cust_sales as
select referral.custref as cid,
sum(orders.price*orders.quantity) as sales 
from referral
left join orders 
on referral.custref = orders.cid  
group by referral.custref;

select customer.cid as cid,
customer.cname as cname, 
cust_com.commission 
from customer join 
(select referral.custid, 
sum(referral.commission*ref_cust_sales.sales) 
as commission from ref_cust_sales 
left join referral 
on referral.custref = ref_cust_sales.cid 
group by referral.custid) 
as cust_com on customer.cid = cust_com.custid
order by cname ASC;

drop view ref_cust_sales;


-- END Query 8
-- START Query 9

select product.pid, introdate,
sum(orders.quantity*orders.price) as totalsales
from product 
join orders on product.pid = orders.pid 
where orders.status = 'S' 
AND introdate < '2015-12-31' 
group by product.pid, introdate 
order by introdate ASC;


-- END Query 9
-- START Query 10

create view warehouse_location as
select wid, location.lid as lid,  lname from warehouse 
join location on warehouse.lid = location.lid 
group by wid, lname, location.lid;


select warehouse_location.lid, warehouse_location.lname,  sum(orders.quantity*orders.price) as totalsales from orders join warehouse_location on warehouse_location.wid = orders.shipwid where orders.status = 'S' group by warehouse_location.lid, warehouse_location.lname order by warehouse_location.lname; 

drop view warehouse_location;
-- END Query 10
