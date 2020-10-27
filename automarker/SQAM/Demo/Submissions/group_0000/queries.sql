-- +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- BEGIN
-- +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

-- ++++++++++++++++++++
--  Q1
-- ++++++++++++++++++++

SELECT COUNT(*) AS "TotalSeniors"
FROM Person
WHERE DateOfBirth <= "1954-11-14";

-- ++++++++++++++++++++
--  Q2
-- ++++++++++++++++++++

SELECT COUNT(*) AS "Taken"
FROM Take
WHERE "2019-09-04" = Date AND ShipID IN (SELECT ShipID
		 			 FROM Ship
		 			 WHERE RouteID = 1);
					
-- ++++++++++++++++++++
--  Q3.A
-- ++++++++++++++++++++

SELECT ShipID, Age, Manufacturer 
FROM Ship
WHERE AdvertisingRevenue > 10000;

-- ++++++++++++++++++++
--  Q3.B
-- ++++++++++++++++++++
SELECT FirstName, LastName, YearsOfService, ShipID
FROM Pilot, Person, Ship
WHERE Pilot.SIN = Person.SIN AND (Pilot.SIN, Ship.ShipID) IN (SELECT *
				                               FROM Operate
							       WHERE ShipID IN (SELECT ShipID 
							       			FROM Ship
							       		   	WHERE AdvertisingRevenue = (SELECT MAX(AdvertisingRevenue) 
													    FROM Ship)));  
-- ++++++++++++++++++++
--  Q3.C
-- ++++++++++++++++++++
SELECT RouteID, SUM(AdvertisingRevenue) AS "Total Revenue"
FROM Ship GROUP BY RouteID ORDER BY SUM(AdvertisingRevenue) DESC;
-- ++++++++++++++++++++
--  Q4.A
-- ++++++++++++++++++++

SELECT Passenger.Type, SUM(Fare.Fee) AS revenue
FROM Passenger, Fare
WHERE Passenger.Type = Fare.Type
GROUP BY Passenger.Type;
-- ++++++++++++++++++++
--  Q4.B
-- ++++++++++++++++++++

SELECT Passenger.Type ,SUM(Fare.Fee) AS revenue
FROM Passenger, Fare
WHERE Passenger.Type = Fare.Type
GROUP BY Passenger.Type
HAVING revenue > 500;

-- ++++++++++++++++++++
--  Q4.C
-- ++++++++++++++++++++

SELECT REV.Type AS Type, MAX(revenue) AS revenue
FROM (SELECT Passenger.Type AS Type ,SUM(Fare.Fee) AS revenue
      FROM Passenger, Fare
      WHERE Passenger.Type = Fare.Type AND Passenger.SIN IN (SELECT SIN FROM Take WHERE Take.Date = "2019-09-01" )
      GROUP BY Passenger.Type) REV ;
    
-- ++++++++++++++++++++
--  Q5.A
-- ++++++++++++++++++++

SELECT pilot.SIN, FirstName, LastName, FLOOR(datediff("2019-11-04 12:00:00", DateOfBirth)/365) AS "Age"
FROM person, pilot
WHERE person.SIN = pilot.SIN AND pilot.SIN IN (SELECT SIN
					       FROM infraction
					       GROUP BY SIN
					       HAVING COUNT(*) < 3) ;


-- ++++++++++++++++++++
--  Q5.B
-- ++++++++++++++++++++

SELECT SIN, SUM(Demerit) AS TotalDemerit, SUM(Fine) AS TotalFine
FROM infraction
GROUP BY SIN
HAVING TotalDemerit > 2
ORDER BY TotalDemerit DESC ;
-- ++++++++++++++++++++
--  Q6.A
-- ++++++++++++++++++++

SELECT ShipID, Manufacturer
FROM Ship
GROUP BY Manufacturer
HAVING COUNT(ShipID) = 1 ;
-- ++++++++++++++++++++
--  Q6.B
-- ++++++++++++++++++++


SELECT RouteID, Total as "number of times"
FROM (SELECT RouteID, COUNT(*) as Total
	  FROM Take, ship
	  WHERE Take.shipID = ship.shipID AND Date = "2019-09-07"
	  GROUP BY RouteID) p
WHERE p.Total = (SELECT MAX(temp.Total)
				 FROM (SELECT RouteID, COUNT(*) as Total
					   FROM Take, ship
					   WHERE Take.shipID = ship.shipID AND Date = "2019-09-07"
					   GROUP BY RouteID)temp  );
-- ++++++++++++++++++++
--  Q6.C
-- ++++++++++++++++++++

SELECT pass.Date, MAX(pass.total_pass) AS "trips taken"
FROM (SELECT Take.Date, COUNT(SIN) AS total_pass
      FROM Take
      GROUP BY Take.Date) pass;

-- ++++++++++++++++++++
--  Q7.A
-- ++++++++++++++++++++

SELECT Person.Occupation, COUNT(*) AS occurrences
FROM Person
WHERE SIN IN (SELECT temp_SIN
			  FROM (SELECT DISTINCT Person.SIN AS temp_SIN, Go.SIname, Take.Date
					FROM Take, Person, Go, Ship
					WHERE Go.SIName REGEXP "Library" AND Go.RouteID = Ship.RouteID AND Take.ShipID = Ship.ShipID AND Take.Date IN ("2019-09-05", "2019-09-06") AND Person.SIN = Take.SIN) uniqtable)
GROUP BY Person.Occupation;
-- ++++++++++++++++++++
--  Q7.B
-- ++++++++++++++++++++

SELECT Person.Occupation, uniqtable.temp_DATE
FROM Person, (SELECT DISTINCT Person.SIN AS temp_SIN, Go.SIname, Take.Date AS temp_DATE
			  FROM Take, Person, Go
			  WHERE Go.SIName REGEXP "Library" AND Go.RouteID = Run.RouteID AND Take.ShipID = Run.ShipID AND temp_DATE IN ("2019-09-05", "2019-09-06") AND Person.SIN = Take.SIN) uniqtable
WHERE Person.SIN = uniqtable.temp_SIN
GROUP BY Person.Occupation
ORDER BY COUNT(*);
-- ++++++++++++++++++++
--  Q8
-- ++++++++++++++++++++

SELECT FirstName, LastName, Person.SIN
FROM Pilot, Person
WHERE Pilot.SIN = Person.SIN AND YearsOfService > 5 AND Salary > 75000 AND Pilot.SIN IN (SELECT SIN
											 FROM Infraction
											 GROUP BY SIN
											 HAVING SUM(Demerit) < 9);
-- ++++++++++++++++++++
--  Q9
-- ++++++++++++++++++++

SELECT FirstName, LastName, Sex, Number, Person.SIN
FROM Person, Phone
WHERE Person.SIN = Phone.SIN AND Person.SIN IN (SELECT Take.SIN
						FROM Take, Ship, Go, Sites, Event
						WHERE Take.ShipID = Ship.ShipID AND Ship.RouteID = Go.RouteID AND Go.RouteID = 4 AND Sites.SIName = Go.SIName 
                                                AND Sites.SIName ="Jedi Temple" AND Event.SIName = Sites.SIName AND Event.EName = "Jedi Knight Basketball");
-- ++++++++++++++++++++
--  Q10
-- ++++++++++++++++++++

-- Your code goes here (replace this line with your query)

-- +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- END
-- +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
