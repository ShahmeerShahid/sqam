-- ++++++++++++++++++++++++++++++++++++++++++++++
-- A2 CREATE TABLES: CSC343 - Winter 2020
-- Michael Liut, MCS Department
-- University of Toronto Mississauga
-- ++++++++++++++++++++++++++++++++++++++++++++++

-- ----------------------------------------------
--  DDL Statements for table Route
-- ----------------------------------------------
CREATE TABLE IF NOT EXISTS Route
(
RouteID int NOT NULL,
Name varchar(60),
PRIMARY KEY (RouteID)
);

-- ----------------------------------------------
--  DDL Statements for table Fare
-- ----------------------------------------------
CREATE TABLE IF NOT EXISTS Fare
(
  Type varchar(20) NOT NULL, 
  Fee decimal(10,2),
  PRIMARY KEY (Type)
);

-- ----------------------------------------------
--  DDL Statements for table Ship
-- ----------------------------------------------
CREATE TABLE IF NOT EXISTS Ship
(
ShipID int NOT NULL,
NumberOfSeats int,
Age int,
Manufacturer varchar(60),
AdvertisingRevenue int,
FuelType varchar(60),
RouteID int NOT NULL,
PRIMARY KEY (ShipID),
FOREIGN KEY (RouteID) REFERENCES Route(RouteID) ON DELETE CASCADE
);

-- ----------------------------------------------
--  DDL Statements for table Person
-- ----------------------------------------------
CREATE TABLE IF NOT EXISTS Person
(
SIN integer NOT NULL,
FirstName varchar(60),
LastName varchar(60),
Sex varchar(10),
Occupation varchar(60),
Street varchar(50),
City varchar(25),
Province varchar(25),
PostalCode varchar(6),
DateOfBirth date,
PRIMARY KEY (SIN)
);

-- ----------------------------------------------
--  DDL Statements for table Phone
-- ----------------------------------------------
CREATE TABLE IF NOT EXISTS Phone
(
SIN integer NOT NULL,
Number bigint NOT NULL,
Type varchar(20),
PRIMARY KEY (SIN, Number),
FOREIGN KEY (SIN) REFERENCES Person (SIN) ON DELETE CASCADE
);

-- ----------------------------------------------
--  DDL Statements for table Pilot
-- ----------------------------------------------
CREATE TABLE IF NOT EXISTS Pilot
(
SIN integer NOT NULL REFERENCES Person (SIN) ON DELETE CASCADE,
YearsOfService integer NOT NULL,
Salary integer NOT NULL,
PRIMARY KEY (SIN)
);
-- ----------------------------------------------
--  DDL Statements for table ServicePersonnel
-- ----------------------------------------------
CREATE TABLE IF NOT EXISTS ServicePersonnel
(
SIN integer NOT NULL,
YearsOfService integer NOT NULL,
Salary integer NOT NULL,
Level varchar(20),
AreaSpecialization varchar(100),
PRIMARY KEY (SIN),
FOREIGN KEY (SIN) REFERENCES Person (SIN) ON DELETE CASCADE
);


-- ----------------------------------------------
--  DDL Statements for table Passenger
-- ----------------------------------------------
CREATE TABLE IF NOT EXISTS Passenger
(
SIN integer NOT NULL,
Type varchar(20),
PRIMARY KEY (SIN),
FOREIGN KEY (SIN) REFERENCES Person (SIN) ON DELETE CASCADE,
FOREIGN KEY (Type) REFERENCES Fare (Type) ON DELETE CASCADE
);

-- ----------------------------------------------
--  DDL Statements for table Infraction
-- ----------------------------------------------
CREATE TABLE IF NOT EXISTS Infraction
(
SIN integer NOT NULL,
Date date NOT NULL,
Type varchar(60),
Demerit integer,
Fine integer,
PRIMARY KEY (SIN, Date),
FOREIGN KEY (SIN) REFERENCES Person (SIN) ON DELETE CASCADE
);

-- ----------------------------------------------
--  DDL Statements for table Operate
-- ----------------------------------------------
CREATE TABLE IF NOT EXISTS Operate
(
SIN integer NOT NULL,
ShipID integer NOT NULL,
PRIMARY KEY (SIN, ShipID),
FOREIGN KEY (SIN) REFERENCES Person (SIN) ON DELETE CASCADE,
FOREIGN KEY (ShipID) REFERENCES Ship (ShipID) ON DELETE CASCADE
);
-- ----------------------------------------------
--  DDL Statements for table Fix
-- ----------------------------------------------
CREATE TABLE IF NOT EXISTS Fix
(
ShipID int NOT NULL,
SIN int NOT NULL,
Date date,
RepairCost integer,
PRIMARY KEY (SIN, ShipID),
FOREIGN KEY (SIN) REFERENCES Person (SIN) ON DELETE CASCADE,
FOREIGN KEY (ShipID) REFERENCES Ship (ShipID) ON DELETE CASCADE
);

-- ----------------------------------------------
--  DDL Statements for table Take
-- ----------------------------------------------
CREATE TABLE IF NOT EXISTS Take
(
SIN integer NOT NULL,
ShipID integer NOT NULL,
Date date NOT NULL,
Time time NOT NULL,
PRIMARY KEY(SIN, ShipID, Date, Time),
FOREIGN KEY (SIN) REFERENCES Person (SIN) ON DELETE CASCADE,
FOREIGN KEY (ShipID) REFERENCES Ship (ShipID) ON DELETE CASCADE
);

-- ----------------------------------------------
--  DDL Statements for table Sites
-- ----------------------------------------------
CREATE TABLE IF NOT EXISTS Sites
(
SIName varchar(100) NOT NULL,
PhoneNumber bigint NOT NULL, 
Category varchar(100), 
Address varchar(100), 
Capacity int,
PRIMARY KEY (SIName, PhoneNumber)
);

-- ----------------------------------------------
--  DDL Statements for table Stop
-- ----------------------------------------------
CREATE TABLE IF NOT EXISTS Stop
(
StopID int NOT NULL,
SName varchar(60),
SIName varchar(60) NOT NULL,
PhoneNumber bigint NOT NULL,
PRIMARY KEY (StopID),
FOREIGN KEY (SIName, PhoneNumber) REFERENCES Sites(SIName, PhoneNumber) ON DELETE CASCADE
);

-- ----------------------------------------------
--  DDL Statements for table Event
-- ----------------------------------------------
CREATE TABLE IF NOT EXISTS Event
(
EName varchar(60) NOT NULL,
Time time NOT NULL,
SIName varchar(60) NOT NULL,
PhoneNumber bigint NOT NULL,
Date date NOT NULL,
NumParticipants int,
PRIMARY KEY (EName),
FOREIGN KEY (SIName, PhoneNumber) REFERENCES Sites(SIName, PhoneNumber) ON DELETE CASCADE
);

-- ----------------------------------------------
--  DDL Statements for table Schedule
-- ----------------------------------------------
CREATE TABLE IF NOT EXISTS Schedule
(
RouteID int NOT NULL,
StopID int NOT NULL,
ArrivalTime time NOT NULL,
Date date NOT NULL,
PRIMARY KEY (RouteID, StopID, ArrivalTime, Date),
FOREIGN KEY (RouteID) REFERENCES Route(RouteID) ON DELETE CASCADE,
FOREIGN KEY (StopID) REFERENCES Stop(StopID) ON DELETE CASCADE
);

-- ----------------------------------------------
--  DDL Statements for table Contain
-- ----------------------------------------------
CREATE TABLE IF NOT EXISTS Contain
(
RouteID int NOT NULL,
StopID int NOT NULL,
PRIMARY KEY (RouteID, StopID),
FOREIGN KEY (RouteID) REFERENCES Route(RouteID) ON DELETE CASCADE,
FOREIGN KEY (StopID) REFERENCES Stop(StopID) ON DELETE CASCADE
);

-- ----------------------------------------------
--  DDL Statements for table Go
-- ----------------------------------------------
CREATE TABLE IF NOT EXISTS Go
(
RouteID int NOT NULL,
SIName varchar(60) NOT NULL,
PhoneNumber bigint NOT NULL,
PRIMARY KEY (RouteID, SIName, PhoneNumber),
FOREIGN KEY (RouteID) REFERENCES Route(RouteID) ON DELETE CASCADE,
FOREIGN KEY (SIName, PhoneNumber) REFERENCES Sites(SIName, PhoneNumber) ON DELETE CASCADE
);