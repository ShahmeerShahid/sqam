-- +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- BEGIN
-- +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

-- ++++++++++++++++++++
--  Q1
-- ++++++++++++++++++++

SELECT COUNT(*) AS "totalSeniors"
FROM Person 
WHERE DateOfBirth <= DATE("1954-11-14");

-- ++++++++++++++++++++
--  Q2
-- ++++++++++++++++++++

SELECT COUNT(*) AS "taken" 
FROM Person, Take, Ship 
WHERE Person.SIN = Take.SIN AND Person.Occupation = "student" AND Ship.ShipID = Take.ShipID AND Ship.RouteID = 1 AND Take.Date = DATE("2019-09-04");


-- ++++++++++++++++++++
--  Q3.A
-- ++++++++++++++++++++

SELECT ShipID, Age, Manufacturer 
FROM Ship 
WHERE AdvertisingRevenue > 10000;

-- ++++++++++++++++++++
--  Q3.B
-- ++++++++++++++++++++

SELECT Person.FirstName, Person.LastName, Pilot.YearsOfService, Ship.ShipID 
FROM Operate, Pilot, Ship, Person, (SELECT max(AdvertisingRevenue) AS revenue 
                                    FROM Ship) m 
WHERE Ship.AdvertisingRevenue = m.revenue AND  Ship.ShipID = Operate.ShipID AND Operate.SIN = Pilot.SIN AND Pilot.SIN = Person.SIN;

-- ++++++++++++++++++++
--  Q3.C
-- ++++++++++++++++++++

SELECT RouteID, sum(AdvertisingRevenue) AS 'Total Revenue' 
FROM Ship 
GROUP BY RouteID 
ORDER BY sum(AdvertisingRevenue) DESC;

-- ++++++++++++++++++++
--  Q4.A
-- ++++++++++++++++++++

SELECT Fare.Type, SUM(Fare.Fee) AS "revenue" 
FROM Fare, Passenger, Take 
WHERE Passenger.SIN = Take.SIN AND Fare.Type = Passenger.Type 
GROUP BY Fare.Type;

-- ++++++++++++++++++++
--  Q4.B
-- ++++++++++++++++++++

SELECT k.Type, k.revenue 
FROM (SELECT Fare.Type, SUM(Fare.Fee) AS "revenue" 
      FROM Fare, Passenger, Take 
      WHERE Passenger.SIN = Take.SIN AND Fare.Type = Passenger.Type 
      GROUP BY Fare.Type) k 
WHERE k.revenue > 500;

-- ++++++++++++++++++++
--  Q4.C
-- ++++++++++++++++++++

SELECT k.type, k.revenue
FROM (SELECT Fare.Type, SUM(Fare.Fee) AS "revenue" 
      FROM Fare, Passenger, Take 
      WHERE Passenger.SIN = Take.SIN AND Fare.Type = Passenger.Type AND Take.Date = DATE("2019-09-01") 
      GROUP BY Fare.Type) k 
ORDER BY k.revenue DESC 
LIMIT 1;

-- ++++++++++++++++++++
--  Q5.A
-- ++++++++++++++++++++

SELECT Person.SIN, Person.FirstName, Person.LastName, FLOOR(DATEDIFF(Person.DateOfBirth, CURDATE())/-365) AS "Age"
FROM Person, (SELECT k.SIN 
              FROM (SELECT Infraction.SIN, count(Infraction.SIN) AS "Count" 
                    FROM Infraction
                    GROUP BY Infraction.SIN) k 
              WHERE k.Count < 3) m
WHERE Person.SIN = m.SIN;

-- ++++++++++++++++++++
--  Q5.B
-- ++++++++++++++++++++

SELECT * 
FROM (SELECT Infraction.SIN, SUM(Infraction.Demerit) AS totalDemeritPoints, SUM(Infraction.Fine) AS "totalFine"
      FROM Pilot, Infraction 
      WHERE Pilot.SIN = Infraction.SIN 
      GROUP BY Infraction.SIN  
      ORDER BY totalDemeritPoints DESC, totalFine DESC) k 
WHERE k.totalDemeritPoints >= 2;

-- ++++++++++++++++++++
--  Q6.A
-- ++++++++++++++++++++

SELECT ShipID, m.Manufacturer 
FROM Ship, (SELECT Manufacturer, COUNT(Manufacturer) AS count 
            FROM Ship 
            GROUP BY Manufacturer) m 
WHERE m.count = 1 AND m.Manufacturer = Ship.manufacturer;

-- ++++++++++++++++++++
--  Q6.B
-- ++++++++++++++++++++

SELECT Ship.RouteID, COUNT(Ship.RouteID) AS "number of times" 
FROM Take, Ship 
WHERE Take.Date = DATE("2019-09-07") AND Take.ShipID = Ship.ShipID 
GROUP BY Ship.RouteID 
ORDER BY COUNT(Ship.RouteID) DESC
LIMIT 1;       

-- ++++++++++++++++++++
--  Q6.C
-- ++++++++++++++++++++

SELECT Take.Date, COUNT(Take.Date) AS "trips taken" 
FROM Take 
GROUP BY Take.Date 
ORDER BY COUNT(Take.Date) DESC 
LIMIT 1;

-- ++++++++++++++++++++
--  Q7.A
-- ++++++++++++++++++++

 SELECT Person.Occupation, count(Person.Occupation) as "occurrences" 
 FROM Ship, Take, Sites, Go, Person 
 WHERE Sites.Category = "Library" AND Sites.SIName = Go.SIName AND Go.RouteID = Ship.RouteID AND Ship.ShipID = Take.ShipID AND (Take.Date = DATE("2019-09-05") OR Take.Date = DATE("2019-09-06")) AND Take.SIN = Person.SIN 
 GROUP BY Person.Occupation;

-- ++++++++++++++++++++
--  Q7.B
-- ++++++++++++++++++++

SELECT Person.Occupation, Take.Date
FROM Ship, Take, Sites, Go, Person   
WHERE Sites.Category = "Library" AND Sites.SIName = Go.SIName AND Go.RouteID = Ship.RouteID AND Ship.ShipID = Take.ShipID AND (Take.Date = DATE("2019-09-05") OR Take.Date = DATE("2019-09-06")) AND Take.SIN = Person.SIN   
GROUP BY Person.Occupation, Take.Date
ORDER BY count(Person.Occupation) DESC 
LIMIT 2;

-- ++++++++++++++++++++
--  Q8
-- ++++++++++++++++++++

SELECT k.FirstName, k.LastName, k.SIN 
FROM (SELECT Infraction.SIN, Person.FirstName, Person.LastName, sum(Infraction.Demerit) AS totalDemeritPoints 
      FROM Pilot, Infraction, Person 
      WHERE Person.SIN = Infraction.SIN AND Pilot.SIN = Infraction.SIN AND Pilot.Salary > 75000 AND Pilot.YearsOfService > 5 
      GROUP BY Infraction.SIN) k 
WHERE k.totalDemeritPoints < 9;

-- ++++++++++++++++++++
--  Q9
-- ++++++++++++++++++++

SELECT k.FirstName, k.LastName, k.Sex, Phone.Number 
FROM (SELECT Person.SIN, Person.FirstName, Person.LastName, Person.Sex 
      FROM Take, Ship, Person, Event  
      WHERE Take.ShipID = Ship.ShipID AND RouteID = 4 AND Person.Occupation = "student" AND Person.SIN = Take.SIN AND Event.EName = "Jedi Knight Basketball") k  
LEFT JOIN Phone 
ON Phone.SIN = k.SIN;

-- ++++++++++++++++++++
--  Q10
-- ++++++++++++++++++++

SELECT Schedule.RouteID, Stop.SName, Schedule.ArrivalTime 
FROM Event, Stop, Schedule 
WHERE Event.SIName = Stop.SIName AND Event.EName = "YG 4hunnid Concert" AND Schedule.StopID = Stop.StopID AND Schedule.Date = Event.Date AND Schedule.ArrivalTime >= TIME("16:00:00") AND Schedule.ArrivalTime <= TIME("17:00:00");

-- +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- END
-- +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
