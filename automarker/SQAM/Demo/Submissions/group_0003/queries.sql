-- +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- BEGIN
-- +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

-- ++++++++++++++++++++
--  Q1
-- ++++++++++++++++++++

SELECT COUNT(SIN) totalSeniors
FROM Person
WHERE TIMESTAMPDIFF(YEAR, DateOfBirth, '2019-11-14') >= 65;

-- ++++++++++++++++++++
--  Q2
-- ++++++++++++++++++++

SELECT COUNT(*) taken 
FROM Passenger p, Take t, Ship s
WHERE 
		p.Type = 'ST'
    AND p.SIN = t.SIN
    AND t.ShipID = s.ShipID
    AND t.Date = DATE('2019-09-04')
    AND s.RouteID = 1;

-- ++++++++++++++++++++
--  Q3.A
-- ++++++++++++++++++++

SELECT ShipID, Age, Manufacturer
FROM Ship
WHERE AdvertisingRevenue > 10000;

-- ++++++++++++++++++++
--  Q3.B
-- ++++++++++++++++++++

SELECT DISTINCT FirstName, LastName, YearsOfService, s.ShipID
FROM Person p1, Pilot p2, Operate o, (SELECT ShipID FROM Ship WHERE AdvertisingRevenue = (SELECT MAX(AdvertisingRevenue) FROM Ship)) s
WHERE 
	p1.SIN = p2.SIN
    AND p2.SIN = o.SIN
    AND o.ShipID = s.ShipID;
    
-- ++++++++++++++++++++
--  Q3.C
-- ++++++++++++++++++++

SELECT RouteID, SUM(AdvertisingRevenue) 'Total Revenue'
FROM Ship 
GROUP BY RouteID
ORDER BY SUM(AdvertisingRevenue) DESC;

-- ++++++++++++++++++++
--  Q4.A
-- ++++++++++++++++++++

SELECT f.Type, SUM(Fee) revenue
FROM Passenger p, Take t, Fare f
WHERE
	t.SIN = p.SIN
	AND f.Type = p.Type
GROUP BY f.Type;

-- ++++++++++++++++++++
--  Q4.B
-- ++++++++++++++++++++

SELECT f.Type, SUM(Fee) revenue
FROM Passenger p, Take t, Fare f 
WHERE 
	p.SIN = t.SIN
    AND p.Type = f.Type
GROUP BY f.Type
HAVING SUM(Fee) > 500;

-- ++++++++++++++++++++
--  Q4.C
-- ++++++++++++++++++++

SELECT f.Type, SUM(Fee) revenue
FROM Passenger p, Take t, Fare f 
WHERE 
	p.SIN = t.SIN
  AND p.Type = f.Type
  AND Date = DATE('2019-09-01')
GROUP BY f.Type
HAVING revenue =
  (SELECT MAX(revenue)
  FROM (
    SELECT f.Type, SUM(Fee) revenue
    FROM Passenger p, Take t, Fare f 
    WHERE 
      p.SIN = t.SIN
      AND p.Type = f.Type
      AND Date = DATE('2019-09-01')
  	GROUP BY f.Type) s);

-- ++++++++++++++++++++
--  Q5.A
-- ++++++++++++++++++++
SELECT p1.SIN, FirstName, LastName, (TIMESTAMPDIFF(YEAR, DateOfBirth, '2019-11-14')) Age
FROM Pilot p1, Person p2, Infraction i
WHERE 
		p1.SIN = i.SIN 
    AND p1.SIN = p2.SIN
GROUP BY p1.SIN 
HAVING SUM(Demerit) < 3;

-- ++++++++++++++++++++
--  Q5.B
-- ++++++++++++++++++++

SELECT p.SIN, SUM(Demerit) totalDemerit, SUM(Fine) totalFine
FROM Pilot p, Infraction i
WHERE p.SIN = i.SIN 
GROUP BY p.SIN 
HAVING totalDemerit > 2
ORDER BY
	totalDemerit DESC,
  totalFine DESC;

-- ++++++++++++++++++++
--  Q6.A
-- ++++++++++++++++++++

SELECT ShipID, Manufacturer
FROM Ship 
WHERE Manufacturer
IN
	(SELECT Manufacturer
	 FROM Ship
	 GROUP BY Manufacturer 
     HAVING COUNT(*) = 1);

-- ++++++++++++++++++++
--  Q6.B
-- ++++++++++++++++++++

SELECT RouteID, COUNT(RouteID) 'number of times'
FROM Take t, Ship s
WHERE
	t.ShipID = s.ShipID
	AND Date = DATE('2019-09-07')
GROUP BY RouteID
HAVING COUNT(RouteID) =
	(SELECT MAX(times)
  FROM 
    (SELECT RouteID, COUNT(RouteID) times
    FROM Take t, Ship s
    WHERE
    	t.ShipID = s.ShipID
    AND Date = DATE('2019-09-07')
  GROUP BY RouteID) s);

-- ++++++++++++++++++++
--  Q6.C
-- ++++++++++++++++++++

SELECT Date, COUNT(Date) 'trips taken'
FROM Take 
GROUP BY Date
HAVING COUNT(Date) =
  (SELECT MAX(times)
    FROM
      (SELECT Date, COUNT(Date) times
      FROM Take
      GROUP BY Date) s);

-- ++++++++++++++++++++
--  Q7.A
-- ++++++++++++++++++++

SELECT DISTINCT Occupation, COUNT(Occupation) occurrences 
FROM 
  (SELECT DISTINCT SIN 
    FROM Stop st, Sites si, Schedule sc, Ship sh, Take t
    WHERE 
      si.Category = 'Library' -- [1] The site has to be a library
      AND st.SIName = si.SIName -- [2] Get the Stops at the site
      AND sc.StopID = st.StopID -- [3] Get the Schedule of the Stops at the site
      AND sh.RouteID = sc.RouteID -- [4] Get the Ships that Go on this Route
      AND (t.Date = '2019-09-05' OR t.Date = '2019-09-06') -- [5] The Stop has to be on these days
      AND t.ShipID = sh.ShipID -- [6] Get the Ships that have been Taken by people
    ) s, Person p
WHERE s.SIN = p.SIN -- [7] Get the information on those people
GROUP BY Occupation;

-- ++++++++++++++++++++
--  Q7.B
-- ++++++++++++++++++++

SELECT DISTINCT Occupation, COUNT(Occupation) occurrences, s.Date
FROM (
	SELECT DISTINCT SIN, t.Date FROM
		Stop st, Sites si, Schedule sc, Ship sh, Take t
	WHERE 
		si.Category = 'Library' -- [1] The site has to be a library
		AND st.SIName = si.SIName -- [2] Get the Stops at the site
		AND sc.StopID = st.StopID -- [3] Get the Schedule of the Stops at the site
		AND sh.RouteID = sc.RouteID -- [4] Get the Ships that Go on this Route
		AND (t.Date = '2019-09-05' OR t.Date = '2019-09-06') -- [5] The Stop has to be on these days
		AND t.ShipID = sh.ShipID -- [6] Get the Ships that have been Taken by people
    ) s, Person p
WHERE s.SIN = p.SIN -- [7] Get the information on those people
GROUP BY Occupation, s.Date
HAVING occurrences IN 
  (SELECT MAX(occurrences)
   FROM
    (SELECT DISTINCT Occupation, COUNT(Occupation) occurrences, s.Date
     FROM (
      SELECT DISTINCT SIN, t.Date
      FROM Stop st, Sites si, Schedule sc, Ship sh, Take t
      WHERE 
        si.Category = 'Library' -- [1] The site has to be a library
        AND st.SIName = si.SIName -- [2] Get the Stops at the site
        AND sc.StopID = st.StopID -- [3] Get the Schedule of the Stops at the site
        AND sh.RouteID = sc.RouteID -- [4] Get the Ships that Go on this Route
        AND (t.Date = '2019-09-05' OR t.Date = '2019-09-06') -- [5] The Stop has to be on these days
        AND t.ShipID = sh.ShipID -- [6] Get the Ships that have been Taken by people
        ) s, Person p
    WHERE s.SIN = p.SIN -- [7] Get the information on those people
    GROUP BY Occupation, s.Date) r
   GROUP BY Date);

-- ++++++++++++++++++++
--  Q8
-- ++++++++++++++++++++

SELECT p1.SIN, FirstName, LastName
FROM Person p1, Pilot p2, Infraction i
WHERE 
		p1.SIN = p2.SIN
    AND p2.SIN = i.SIN
    AND Salary > 75000
    AND YearsOfService > 5
GROUP BY p1.SIN 
HAVING SUM(Demerit) < 9;

-- ++++++++++++++++++++
--  Q9
-- ++++++++++++++++++++

SELECT DISTINCT FirstName, LastName , Sex, ph.Number
FROM Sites s, Event e, Go g, Route r, Take t, Ship sh, Passenger p1, Person p2
LEFT JOIN Phone ph ON ph.SIN = p2.SIN
WHERE 
  EName='Jedi Knight Basketball' -- [1] The site name
  AND s.SINAME = e.SIName -- [2] Check site name is same as the event site name
  AND g.RouteID = 4 -- [3] Check all trips on route 4
  AND g.RouteID = r.RouteID
  AND g.SIName = e.SIName -- [4]  Check that all trips go to event
  AND t.Date = e.Date -- [10]
  AND t.SIN = p1.SIN -- [12]
  AND t.ShipID = sh.ShipID -- [13]
  AND sh.RouteID = r.RouteID -- [14]
  AND p1.SIN = p2.SIN -- [15]
  AND Occupation = 'student';

-- ++++++++++++++++++++
--  Q10
-- ++++++++++++++++++++

SELECT DISTINCT RouteID, SName, ArrivalTime
FROM Stop st, Schedule sc
WHERE
	SIName = 'Senate District'
    AND st.StopID = sc.StopID AND
    Date = '2019-09-06'
    AND ArrivalTime BETWEEN TIME('16:00:00') AND TIME('17:00:00');

-- +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- END
-- +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
