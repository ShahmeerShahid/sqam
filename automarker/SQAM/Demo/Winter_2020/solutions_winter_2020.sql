-- Q1
select count(DISTINCT SIN) as totalSeniors 
from Person 
where (date('1954-11-14') >= date(Person.DateOfBirth));

-- Q2
select count(*) 
from Passenger p, Take t, Ship s 
where p.sin = t.sin and t.shipid = s.shipid and s.routeid = 1 and t.date = '2019-09-04' and p.type = 'ST';

-- Q3(a)
select ShipID, age, manufacturer 
from Ship 
where advertisingrevenue > 10000;

-- Q3(b)
select FirstName, LastName, YearsOfService, ShipID
from Ship natural join Operate natural join Pilot natural join Person
where AdvertisingRevenue=(
      select max(AdvertisingRevenue)
      from Ship natural join (
        select ShipID, age, manufacturer 
        from Ship 
        where advertisingrevenue > 10000
        ) ProfitableShips
);

-- Q3(c)
select routeid, sum(advertisingrevenue) as "Total Revenue" 
from Ship 
group by routeid 
order by "Total Revenue" desc;

-- Q4(a)
select p.type, sum(f.fee) as revenue
from Passenger p, Fare f, Take t
where t.sin = p.sin and p.type = f.type
group by p.type;

-- Q4(b)
select p.type, sum(f.fee) as revenue
from Passenger p, Fare f, Take t
where t.sin = p.sin and p.type = f.type
group by p.type
having sum(f.fee) > 500;

-- Q4(c)
select n.type, n.revenue
from (select p.type as type, sum(f.fee) as revenue
    from Passenger p, Fare f, Take t
    where t.sin = p.sin and p.type = f.type and t.date = '2019-9-1'
    group by p.type) n
where n.revenue = (select max(m.revenue)
from (select p.type as type, sum(f.fee) as revenue
    from Passenger p, Fare f, Take t
    where t.sin = p.sin and p.type = f.type and t.date = '2019-9-1'
    group by p.type
    order by sum(f.fee) DESC) m);

-- Q5(a)
select p.sin, p.firstName, p.lastName, floor(datediff(curdate(), DateOfBirth) / 365) as "Age"
from Person p, Infraction i
where p.sin = i.sin
group by p.sin, p.firstName, p.lastName
having count(p.sin) < 3;

-- Q5(b)
select p.sin, sum(i.demerit) as totalPoints, sum(i.fine) as totalFine
from Pilot p, Infraction i
where p.sin = i.sin
group by p.sin
having sum(i.demerit) >=2
order by sum(i.demerit) DESC, sum(i.fine) DESC;


-- Q6(a)
select s.ShipID, s.manufacturer
from Ship s
where s.ShipID not in
  (select a.ShipID 
   from Ship a, Ship c
   where a.ShipID <> c.ShipID and a.manufacturer = c.manufacturer);

-- Q6(b)
select s.routeid, count(s.routeid) as "number of times" 
from Take t, Ship s 
where t.date = '2019-09-07' and s.ShipID = t.ShipID 
group by s.routeid 
order by count("number of times") DESC LIMIT 1;

-- Q6(c)
select Take.Date, count(Take.Date) as "trips taken"
from Take
group by Take.Date
order by count("trips taken") desc limit 1;

-- Q7(a)
SELECT
    p.Occupation,
    COUNT(p.Occupation) AS occurrences
FROM
    Person p,
    ( -- Remove duplicate ventures to library on same d
        SELECT DISTINCT
            ta.SIN,
            ta.Date,
            ta.ShipID
        FROM
            Take ta
        WHERE
            Date IN ("2019-09-05", "2019-09-06")

    ) AS t
WHERE
    p.SIN = t.SIN AND
    t.ShipID IN
    (
        SELECT
            sh.ShipID
        FROM
            Ship AS sh
        WHERE
            sh.RouteID IN
            (
                SELECT DISTINCT
                    g.RouteID
                FROM
                    Go g,
                    Sites si
                WHERE
                    si.SIName = g.SIName AND
                    si.PhoneNumber = g.PhoneNumber AND
                    si.Category = 'Library'
            )
    )
GROUP BY
    p.Occupation;

-- Q7(b)
SELECT
    sq1.Occupation,
    sq1.Date
FROM
    (
        SELECT
            t.Date,
            p.Occupation,
            COUNT(p.Occupation) AS occurrences
        FROM
            Person p,
            (
                SELECT DISTINCT
                    ta.SIN,
                    ta.Date,
                    ta.ShipID
                FROM
                    Take ta
                WHERE
                    Date IN ("2019-09-05", "2019-09-06")

            ) AS t
        WHERE
            p.SIN = t.SIN AND
            t.ShipID IN
            (
                SELECT
                    sh.ShipID
                FROM
                    Ship AS sh
                WHERE
                    sh.RouteID IN
                    (
                        SELECT DISTINCT
                            g.RouteID
                        FROM
                            Go g,
                            Sites si
                        WHERE
                            si.SIName = g.SIName AND
                            si.PhoneNumber = g.PhoneNumber AND
                            si.Category = 'Library'
                    )
            )
        GROUP BY
            t.Date,
            p.Occupation

    ) AS sq1
WHERE
    sq1.occurrences =
    (
        SELECT
            MAX(sq2.occurrences)
        FROM
            (
                SELECT
                    t.Date,
                    p.Occupation,
                    COUNT(p.Occupation) AS occurrences
                FROM
                    Person p,
                    (
                        SELECT DISTINCT
                            ta.SIN,
                            ta.Date,
                            ta.ShipID
                        FROM
                            Take ta
                        WHERE
                            Date IN ("2019-09-05", "2019-09-06")

                    ) AS t
                WHERE
                    p.SIN = t.SIN AND
                    t.ShipID IN
                    (
                        SELECT
                            sh.ShipID
                        FROM
                            Ship AS sh
                        WHERE
                            sh.RouteID IN
                            (
                                SELECT DISTINCT
                                    g.RouteID
                                FROM
                                    Go g,
                                    Sites si
                                WHERE
                                    si.SIName = g.SIName AND
                                    si.PhoneNumber = g.PhoneNumber AND
                                    si.Category = 'Library'
                            )
                    )
                GROUP BY
                    t.Date,
                    p.Occupation
            ) AS sq2
        WHERE
            sq2.Date = sq1.Date
    )
;

-- Q8
select FirstName, LastName, SIN
from Pilot natural join Person natural join Infraction
where Salary > 75000 and YearsOfService > 5
group by SIN
having SUM(Demerit) < 9;

-- Q9
select FirstName, LastName, Sex, Number
from Event NATURAL JOIN Go NATURAL JOIN Ship NATURAL JOIN
(select ShipID, Date, SIN from Take) t NATURAL JOIN Person left join Phone on Phone.SIN = Person.SIN
where EName = 'Jedi Knight Basketball' and SIName = 'Jedi Temple' and Occupation = 'student' and RouteID = 4;

-- Q10
SELECT RouteID, SName, ArrivalTime
FROM Event NATURAL JOIN Stop NATURAL JOIN Schedule
WHERE Schedule.Date = '2019-09-06' and ArrivalTime >= time('16:00:00') and ArrivalTime <= time('17:00:00') and EName = 'YG 4hunnid Concert';
