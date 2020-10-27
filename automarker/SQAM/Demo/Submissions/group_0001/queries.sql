-- +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- BEGIN
-- +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

-- ++++++++++++++++++++
--  Q1
-- ++++++++++++++++++++


SELECT COUNT(*) AS totalSeniors FROM Person WHERE Date("2019-11-14") - Person.DateOfBirth > "650000";

-- ++++++++++++++++++++
--  Q2
-- ++++++++++++++++++++

SELECT COUNT(*) AS taken FROM Passenger, Take, 
			(SELECT Ship.ShipID
				FROM Ship
				WHERE Ship.RouteID = 1) AS temp
	WHERE Passenger.Type = "ST" AND Passenger.SIN = Take.SIN
		AND Take.Date = 20190904 AND Take.ShipID = temp.ShipID;

-- ++++++++++++++++++++
--  Q3.A
-- ++++++++++++++++++++

SELECT Ship.ShipID, Ship.Age, Ship.Manufacturer FROM Ship
	WHERE Ship.AdvertisingRevenue > 10000;

-- ++++++++++++++++++++
--  Q3.B
-- ++++++++++++++++++++

SELECT Person.FirstName, Person.LastName, Pilot.YearsOfService, Ship.ShipID
	FROM Person, Pilot, Ship, Operate
	WHERE Person.SIN = Pilot.SIN AND Ship.ShipID = Operate.ShipID
		AND Operate.SIN = Pilot.SIN AND
		Ship.AdvertisingRevenue = (SELECT MAX(AdvertisingRevenue) FROM Ship);

-- ++++++++++++++++++++
--  Q3.C
-- ++++++++++++++++++++

SELECT SUM(Ship.AdvertisingRevenue) As `Total Revenue`, Ship.RouteID
	FROM Ship
	GROUP BY ShipRouteID
	ORDER BY `Total Revenue` DESC;

-- ++++++++++++++++++++
--  Q4.A
-- ++++++++++++++++++++

SELECT SUM(Fare.Fee) AS Revenue, Passenger.Type
	FROM Passenger, Fare
	GROUP BY Passenger.Type;

-- ++++++++++++++++++++
--  Q4.B
-- ++++++++++++++++++++

SELECT temp.Revenue, temp.Type
	FROM (SELECT SUM(Fare.Fee) AS Revenue, Passenger.type
		FROM Passenger, Fare
		GROUP BY Passenger.Fare) AS temp
	WHERE temp.Revenue > 500;


-- ++++++++++++++++++++
--  Q4.C
-- ++++++++++++++++++++

SELECT final.type
	FROM (SELECT SUM(Fare.Fee) AS sum, temp.Type AS type 
		FROM Fare, (SELECT Passenger.type as Type 
				FROM Take, Passenger 
				WHERE Take.SIN = Passenger.SIN AND Take.Date = 20190901) AS temp 
		WHERE Fare.type = temp.Type GROUP BY temp.Type) AS final 
	WHERE final.sum = (SELECT MAX(maximum.SUM) 
						FROM (SELECT SUM(Fare.Fee) AS SUM, temp.type AS type 
								FROM Fare, (SELECT Passenger.type as Type 
											FROM Take, Passenger 
											WHERE Take.SIN = Passenger.SIN AND Take.Date = 20190901) AS temp 
								WHERE Fare.type = temp.Type GROUP BY temp.Type) AS maximum);
-- ++++++++++++++++++++
--  Q5.A
-- ++++++++++++++++++++

SELECT p.SIN, FirstName, LastName, DATEDIFF(NOW(), DateOfBirth) DIV 365 as 'Age' 
    FROM Pilot p JOIN Person p2 ON p.SIN = p2.SIN JOIN (SELECT SIN, COUNT(*) as count FROM Infraction GROUP BY SIN) i ON p.SIN = i.SIN 
    WHERE count>=3;


-- ++++++++++++++++++++
--  Q5.B
-- ++++++++++++++++++++

SELECT p.SIN, FirstName, LastName, DATEDIFF(NOW(), DateOfBirth) DIV 365 as 'Age', totalDemerit, totalFine 
    FROM Pilot p JOIN Person p2 ON p.SIN = p2.SIN JOIN 
        (SELECT SIN, COUNT(*) as count, SUM(Demerit) as totalDemerit, SUM(Fine) as totalFine FROM Infraction GROUP BY SIN ORDER BY totalDemerit DESC, totalFine DESC)
    i ON p.SIN = i.SIN 
    WHERE totalDemerit>=2;

-- ++++++++++++++++++++
--  Q6.A
-- ++++++++++++++++++++

SELECT ShipID, s.Manufacturer FROM Ship s JOIN (SELECT Manufacturer, COUNT(*) as 'count' FROM Ship s1 GROUP BY Manufacturer) u ON s.Manufacturer = u.Manufacturer WHERE count = 1;

-- ++++++++++++++++++++
--  Q6.B
-- ++++++++++++++++++++

SELECT r.RouteID, COUNT(*) as 'number of times' FROM Route r JOIN Ship s ON s.RouteID = r.routeID
JOIN Take t ON t.ShipID = s.ShipId WHERE Date = '2019-09-07' GROUP BY r.RouteID ORDER BY COUNT(*) LIMIT 1;

-- ++++++++++++++++++++
--  Q6.C
-- ++++++++++++++++++++

SELECT Date, COUNT(*) as 'trips taken' FROM Route r JOIN Ship s ON s.RouteID = r.routeID JOIN Take t ON t.ShipID = s.ShipId GROUP BY Date ORDER BY COUNT(*) DESC LIMIT 1;

-- ++++++++++++++++++++
--  Q7.A
-- ++++++++++++++++++++

SELECT occupation, COUNT(*) as 'occurences' FROM 
    (SELECT DISTINCT SIN, occupation FROM 
        Sites s JOIN Stop USING (SIName) 
        JOIN Schedule USING (StopId) 
        JOIN Ship USING (RouteID) 
        JOIN Take USING (Date, ShipID) 
        JOIN Person USING(SIN) 
        WHERE SIName LIKE '%Library' AND (Date = '2019-09-05' OR Date = '2019-09-06'))
    x GROUP BY occupation;

-- ++++++++++++++++++++
--  Q7.B
-- ++++++++++++++++++++

SELECT occupation, Date, COUNT(*) as 'occurences' FROM 
    (SELECT DISTINCT SIN, Date, occupation FROM 
        Sites s JOIN Stop USING (SIName) 
        JOIN Schedule USING (StopId) 
        JOIN Ship USING (RouteID) 
        JOIN Take USING (Date, ShipID) 
        JOIN Person USING(SIN) 
        WHERE SIName LIKE '%Library' AND (Date = '2019-09-05' OR Date = '2019-09-06')) 
    x GROUP BY occupation, Date ORDER BY count DESC LIMIT 2;

-- ++++++++++++++++++++
--  Q8
-- ++++++++++++++++++++

SELECT FirstName, LastName, SIN FROM 
    Pilot JOIN Person USING(SIN) 
    JOIN (SELECT SIN, SUM(Demerit) as 'Demerit' FROM Infraction GROUP BY SIN) x USING (SIN) 
    WHERE Salary > 75000 AND YearsOfService > 5 AND Demerit < 7;

-- ++++++++++++++++++++
--  Q9
-- ++++++++++++++++++++

SELECT FirstName, LastName, p.Number FROM 
    (SELECT DISTINCT FirstName, LastName, SIN FROM 
        Event JOIN Go USING (SIName) 
        JOIN Ship USING (RouteID) 
        JOIN Take USING(Date, ShipID) 
        JOIN Person USING (SIN) 
        WHERE Ename = 'Jedi Knight Basketball') x
    LEFT JOIN Phone p ON x.SIN = p.SIN;

-- ++++++++++++++++++++
--  Q10
-- ++++++++++++++++++++

SELECT RouteID, SName, ArrivalTime FROM
    Event JOIN Stop USING (SIName) 
    JOIN Schedule USING (StopID, Date) 
    WHERE EName = 'YG 4hunnid Concert' AND ArrivalTime BETWEEN '16:00:00' AND '17:00:00';

-- +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- END
-- +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
