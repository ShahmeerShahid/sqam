-- +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- BEGIN
-- +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

-- ++++++++++++++++++++
--  Q1
-- ++++++++++++++++++++

select count(*) as totalSeniors
from Person p
where datediff("2019-11-14", date(p.DateOfBirth))/365.25 >=65;

-- ++++++++++++++++++++
--  Q2
-- ++++++++++++++++++++

select count(p.SIN) as taken
from Ship s, Take t, Passenger p
where s.RouteID=1 AND t.ShipID=s.ShipID AND p.Type="ST" AND t.SIN=p.SIN AND t.Date="2019-09-04";

-- ++++++++++++++++++++
--  Q3.A
-- ++++++++++++++++++++

select ShipID, Age, Manufacturer 
from Ship 
where AdvertisingRevenue>10000;

-- ++++++++++++++++++++
--  Q3.B
-- ++++++++++++++++++++

select pe.FirstName, pe.LastName, pi.YearsOfService, mostProfit.ShipID 
from Operate op, Pilot pi, Person pe, (
  select ShipID 
  from Ship 
  where AdvertisingRevenue =  (
    select max(AdvertisingRevenue) as maxRevenue
    from Ship)) mostProfit 
where op.ShipID=mostProfit.ShipID AND op.SIN=pi.SIN and pe.SIN=pi.SIN;

-- ++++++++++++++++++++
--  Q3.C
-- ++++++++++++++++++++

select RouteID, SUM(AdvertisingRevenue) as TotalRevenue 
from Ship 
group by RouteID 
order by TotalRevenue DESC;

-- ++++++++++++++++++++
--  Q4.A
-- ++++++++++++++++++++

select p.Type, SUM(f.Fee) as revenue 
from Passenger p 
inner join Fare f 
on p.Type= f.Type 
group by p.Type;

-- ++++++++++++++++++++
--  Q4.B
-- ++++++++++++++++++++

select p.Type, SUM(f.Fee) as revenue 
from Passenger p 
inner join Fare f 
on p.Type= f.Type 
group by p.Type
having revenue>500;

-- ++++++++++++++++++++
--  Q4.C
-- ++++++++++++++++++++

select x.Type, x.revenue as revenue 
from (
	select p.Type, sum(f.Fee) as revenue 
  from Passenger p, Take t, Fare f 
  where t.Date="2019-09-01" AND t.SIN=p.SIN AND p.Type=f.Type 
  group by p.Type) x 
where x.revenue =  (
  select max(y.revenue) as maxRevenue
  from (
    select p.Type, sum(f.Fee) as revenue 
    from Passenger p, Take t, Fare f 
    where t.Date="2019-09-01" AND t.SIN=p.SIN AND p.Type=f.Type 
    group by p.Type) y 
);

-- ++++++++++++++++++++
--  Q5.A
-- ++++++++++++++++++++

select p.SIN, p.FirstName, p.LastName, floor(datediff("2019-11-14", date(p.DateOfBirth))/365.25) as Age 
from Person p, (select SIN
	from Infraction 
	group by SIN 
	having count(SIN)<3) inf 
where p.SIN=inf.SIN;

-- ++++++++++++++++++++
--  Q5.B
-- ++++++++++++++++++++

select SIN, sum(Demerit), sum(Fine) as totalFine 
from Infraction  
group by SIN 
having sum(Demerit)>2 
order by sum(Demerit) DESC, totalFine DESC;

-- ++++++++++++++++++++
--  Q6.A
-- ++++++++++++++++++++

SELECT s.ShipID, s.Manufacturer 
FROM Ship s
WHERE s.Manufacturer IN (SELECT s.Manufacturer From Ship s Group By s.Manufacturer HAVING count(s.ShipID)=1);

-- ++++++++++++++++++++
--  Q6.B
-- ++++++++++++++++++++

SELECT s.RouteID , COUNT(s.RouteID) AS  "number of times"
FROM Ship s, Take t    
WHERE t.ShipID=s.ShipID AND t.Date="2019-09-07" 
GROUP BY s.RouteID 
HAVING COUNT(t.ShipID) =(SELECT MAX(tbl.c) 
                         FROM (SELECT s.RouteID , COUNT(s.RouteID) AS c 
                               FROM Ship s, Take t    
                               WHERE t.ShipID=s.ShipID AND t.Date="2019-09-07" 
                               GROUP BY s.RouteID) AS tbl);

-- ++++++++++++++++++++
--  Q6.C
-- ++++++++++++++++++++

SELECT t.DATE, Count(t.ShipID) AS "trips taken"
FROM Take t
Group BY t.Date
HAVING COUNT(t.ShipID) =(SELECT MAX(tbl.c)
                         FROM (SELECT COUNT(t.ShipID) AS c
                               From Take t
                               GROUP BY t.Date) as tbl);

-- ++++++++++++++++++++
--  Q7.A
-- ++++++++++++++++++++

Select pass.Occupation , Count(pass.Occupation) AS occurrences
FROM (SELECT DISTINCT p.SIN AS SIN, p.Occupation 
      FROM Go g, Sites s, Take t, Ship sh , Person p
      Where g.SIName = s.SIName AND s.Category ="Library" AND sh.RouteID = g.RouteID AND t.ShipID =sh.ShipID AND p.SIN = t.SIN AND (t.Date ="2019-09-05" OR t.Date ="2019-09-06")
      ) pass
GROUP BY pass.Occupation;

-- ++++++++++++++++++++
--  Q7.B
-- ++++++++++++++++++++

SELECT m2.Occ as "Most Visits From", m1.Date
FROM(SELECT Max(tbl.occurrences) as mxs, tbl.Date
     FROM (Select pass.Occupation as Occ, Count(pass.Occupation) AS occurrences, pass.Date
           FROM (SELECT DISTINCT p.SIN AS SIN, p.Occupation, t.Date
                 FROM Go g, Sites s, Take t, Ship sh , Person p
                 Where g.SIName = s.SIName AND s.Category ="Library" AND sh.RouteID = g.RouteID AND t.ShipID =sh.ShipID 
                 AND p.SIN = t.SIN AND (t.Date ="2019-09-05" OR t.Date ="2019-09-06")) pass
           GROUP BY pass.Occupation, pass.Date
           ORDER By pass.Date) as tbl
     GROUP BY tbl.Date) as m1
     Left Join (Select pass.Occupation as Occ, Count(pass.Occupation) AS occurrences, pass.Date
                FROM (SELECT DISTINCT p.SIN AS SIN, p.Occupation, t.Date
                            FROM Go g, Sites s, Take t, Ship sh , Person p
                      Where g.SIName = s.SIName AND s.Category ="Library" AND sh.RouteID = g.RouteID AND t.ShipID =sh.ShipID 
                      AND p.SIN = t.SIN AND (t.Date ="2019-09-05" OR t.Date ="2019-09-06")) pass
GROUP BY pass.Occupation, pass.Date
ORDER BY pass.Date) as m2 ON m2.occurrences=m1.mxs;

-- ++++++++++++++++++++
--  Q8
-- ++++++++++++++++++++

SELECT p.FirstName, p.LastName, pil.SIN
FROM Pilot pil, Person p 
WHERE pil.YearsOfService > 5 AND pil.Salary > 75000 AND pil.SIN = p.SIN
HAVING 9 > (SELECT Sum(inf.Demerit) as DEM
FROM  Infraction inf
Where inf.SIN =pil.SIN);

-- ++++++++++++++++++++
--  Q9
-- ++++++++++++++++++++

SELECT  tbl.FirstName, tbl.LastName, tbl.Sex, ph.Number
FROM (SELECT p.FirstName, p.LastName, p.Sex, p.SIN
      FROM Person p, Take t, Ship s, Go g, Event e
      Where p.Occupation = 'Student' AND p.SIN = t.SIN AND t.ShipID = s.ShipID AND s.RouteID = 4 
      AND g.SIName ="Jedi Temple" AND g.SIName = e.SIName AND e.EName = "Jedi Knight Basketball" AND t.Date =e.Date) as tbl
Left Join (Phone ph) ON tbl.SIN = ph.SIN; 

-- ++++++++++++++++++++
--  Q10
-- ++++++++++++++++++++

SELECT DISTINCT s.RouteID, st.SName, s.ArrivalTIme
From Schedule s, Sites si, Ship sh, Go g, Stop st, Event e 
Where e.EName ="YG 4hunnid Concert" AND si.SIName = e.SIName 
AND g.SIName = si.SIName AND g.RouteID = sh.RouteID AND st.SIName = g.SIName 
AND s.ArrivalTIme < '17:00:00' AND s.ArrivalTIme > '16:00:00'; 

-- +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- END
-- +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
