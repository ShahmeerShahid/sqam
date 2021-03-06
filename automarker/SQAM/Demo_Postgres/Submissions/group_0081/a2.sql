-- Add below your SQL statements. 
-- You can create intermediate views (as needed). Remember to drop these views after you have populated the result tables.
-- You can use the "\i a2.sql" command in psql to execute the SQL commands in this file.

-- START Query 1
Select R1.custref as refid ,C2.cname as refname , R1.custid as cuid ,C1.cname as cuname
	From referral R1 , customer C1, customer C2
	Where R1.custid=C1.cid AND R1.custref=C2.cid
	Order By C2.cname;

-- END Query 1
-- START Query 2
Select o.oid as oid, o.pid as pid, o.shipwid as wid ,o.quantity as ordqty, s.quantity as stockqty
	From orders o , stock s
	Where o.quantity > s.quantity AND o.status = 'O' AND o.pid = s.pid AND o.shipwid = s.wid;


-- END Query 2
-- START Query 3
Select c.cid as cuid,c.cname as cuname,SUM(quantity*price) as totalsales
	From orders o,customer c
	Where status = 'S' AND c.cid = o.cid
	Group By c.cid
	Order By (totalsales) DESC;



-- END Query 3
-- START Query 4
Select p.pid as pid ,p.pname as pname ,SUM(cost*o.quantity) as totalcost
	From product p , orders o
	Where status = 'S' AND p.pid = o.pid
	Group By p.pid
	Order By totalcost ASC;


-- END Query 4
-- START Query 5
Select pid, pname, introdate
	From product
	Where pid not in (Select pid from orders)
	Order By pname;


-- END Query 5
-- START Query 6
Select c.cid as cid, c.cname as cname, l.lname as locname
	From customer c, location l
	Where c.lid = l.lid AND c.cid not in (select cid from orders)
	Order By cname;


-- END Query 6
-- START Query 7
Select cast(TO_CHAR(o.odate,'yyyymm') as integer)  as period, SUM(o.price*o.quantity) as sales , SUM(o.quantity * p.cost) as cost
	From orders o , product p
	Where o.pid = p.pid AND status = 'S'
	Group By period
	Order By period;



-- END Query 7
-- START Query 8
Create View one as
	Select r.custid as cid , r.custref as custref, sum(r.commission*o.quantity*o.price) as commission
	From referral r, orders o
	Where r.custref = o.cid
	Group BY r.custref, r.custid;

Select o.cid as cid, c.cname as cname, SUM(commission) as commission
	From one o, customer c
	Where c.cid = o.cid
	Group By o.cid , cname
	Order By cname;

DROP VIEW one;
-- END Query 8
-- START Query 9
Select o.pid as pid , p.introdate as date, SUM(o.quantity*o.price) as totalsales
	From product p , orders o
	Where p.pid = o.pid AND p.introdate<='31 Dec 2015' AND o.status ='S'
	Group By o.pid, p.introdate
	Order By introdate;


-- END Query 9
-- START Query 10

Create View kenny as
	Select l.lid as lid, l.lname as lname, o.shipwid as wid, sum(o.price*o.quantity) as totalsales
	FROM product p, orders o, warehouse w, location l
	Where p.pid = o.pid AND o.status = 'S' AND w.wid = o.shipwid AND l.lid = w.lid
	Group By o.shipwid, l.lid;

Create View kenny2 as
	Select wid From warehouse 
	EXCEPT
	Select wid From kenny;

Create View kenny4 as
	select w.lid
	From kenny2 k, warehouse w
	where k.wid = w.wid;

Create View kenny3 as
	Select l.lid , l.lname, 0 as totalsales
	From kenny4 k4 , location l
	Where l.lid = k4.lid;

Select lid,lname,totalsales from kenny UNION select * from kenny3
	Order By lname;
	
DROP view kenny3 , kenny4, kenny2, kenny;


-- END Query 10
