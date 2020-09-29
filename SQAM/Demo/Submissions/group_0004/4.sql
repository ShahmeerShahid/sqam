-- +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- BEGIN
-- +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

-- ++++++++++++++++++++
--  Q1
-- ++++++++++++++++++++

SELECT HName, City
FROM Hospital
WHERE AnnualBudget > 3000000
ORDER BY AnnualBudget DESC;


-- ++++++++++++++++++++
--  Q2
-- ++++++++++++++++++++

SELECT DISTINCT FirstName, LastName, Gender, DateOfBirth
FROM Person P, Diagnose D
WHERE P.DateOfBirth >= '1978-11-16'
AND P.City = 'Toronto'
AND P.ID = D.PatientID
AND D.Disease LIKE '%Cancer';

-- ++++++++++++++++++++
--  Q3.A
-- ++++++++++++++++++++

SELECT Specialty, AVG(Salary)
FROM Physician
GROUP BY Specialty;

-- ++++++++++++++++++++
--  Q3.B
-- ++++++++++++++++++++

SELECT Specialty, AVG(Salary)
FROM Physician P, Hospital H
WHERE P.HName IN
(
    SELECT HName
    FROM Hospital
    WHERE City = 'Toronto'
    OR City = 'Hamilton'
)
AND P.Specialty IN
(
    SELECT Specialty
    FROM Physician
    GROUP BY Specialty
    HAVING COUNT(Specialty) > 4
)

GROUP BY Specialty
;

-- ++++++++++++++++++++
--  Q3.C
-- ++++++++++++++++++++

SELECT YearsOfPractice, AVG(Salary)
FROM Nurse
GROUP BY YearsOfPractice
ORDER BY YearsOfPractice DESC;

-- ++++++++++++++++++++
--  Q4
-- ++++++++++++++++++++

SELECT HName, COUNT(HName)
FROM
(
  SELECT HName
  FROM Admission
  WHERE Date BETWEEN '2017-08-05' AND '2017-08-10'
)August
GROUP BY HName;

-- ++++++++++++++++++++
--  Q5.A
-- ++++++++++++++++++++

SELECT DName
FROM Department
GROUP BY DName
HAVING COUNT(DName) >= (SELECT COUNT(*) from Hospital);


-- ++++++++++++++++++++
--  Q5.B
-- ++++++++++++++++++++


SELECT HName, DName, NumberOfStaff
FROM
(
    SELECT HName, DName, (Nurses+Doctors) AS NumberOfStaff
    FROM
    (
        SELECT * FROM
        (
            SELECT HName, DName, Count(*) AS Nurses
            FROM Nurse_Work
            GROUP BY HName, DName
        )A

        LEFT JOIN
        (
            SELECT HName as BH, DName as BD, Count(*) AS Doctors
            FROM Physician
            GROUP BY HName, DName
        )B

        ON A.HName = B.BH AND A.DName = B.BD
    )C
)D
WHERE NumberOfStaff =
(
  SELECT Max(NumberOfStaff)
  FROM
  (
      SELECT HName, DName, (Nurses+Doctors) AS NumberOfStaff
      FROM
      (
          SELECT * FROM
          (
              SELECT HName, DName, Count(*) AS Nurses
              FROM Nurse_Work
              GROUP BY HName, DName
          )E

          LEFT JOIN
          (
              SELECT HName as BH, DName as BD, Count(*) AS Doctors
              FROM Physician
              GROUP BY HName, DName
          )F

          ON E.HName = F.BH AND E.DName = F.BD
      )G
  )H
);

-- ++++++++++++++++++++
--  Q5.C
-- ++++++++++++++++++++

SELECT DName
FROM Department
GROUP BY DName
HAVING COUNT(DName) = 1;

-- ++++++++++++++++++++
--  Q6.A
-- ++++++++++++++++++++

SELECT FirstName, LastName
FROM Person
WHERE ID in
(
    SELECT NurseID
    FROM Patient
    GROUP BY NurseID
    HAVING COUNT(NurseID) < 3
)
ORDER BY LastName;

-- ++++++++++++++++++++
--  Q6.B
-- ++++++++++++++++++++

SELECT PatientID
FROM Patient
WHERE PatientID IN
(
    SELECT PatientID
    FROM Patient
    WHERE NurseID IN
    (
      SELECT NurseID
      FROM Patient
      GROUP BY NurseID
      HAVING COUNT(NurseID) < 3
    )
)
AND PatientID IN
(
    SELECT PatientID
    FROM Diagnose
    WHERE Prognosis = 'poor'
);

-- ++++++++++++++++++++
--  Q7
-- ++++++++++++++++++++

SELECT Date
FROM
(
  SELECT Date, COUNT(Date) AS NumOfAdmissions
  FROM Admission
  WHERE HName = 'Hamilton General Hospital'
  GROUP BY Date
)A
WHERE NumOfAdmissions =
(
    SELECT MAX(NumOfAdmissions)
    FROM
    (
        SELECT Date, COUNT(Date) AS NumOfAdmissions
        FROM Admission
        WHERE HName = 'Hamilton General Hospital'
        GROUP BY Date
    )B
);

-- ++++++++++++++++++++
--  Q8
-- ++++++++++++++++++++

SELECT DrugCode, Name
FROM
(
    SELECT DrugCode, Name, COUNT(DrugCode) * UnitCost AS Amount
    FROM
    (
        SELECT Prescription.DrugCode, Drug.Name, Drug.UnitCost
        FROM Prescription LEFT JOIN Drug
        ON Prescription.DrugCode = Drug.DrugCode
    )B
    GROUP BY DrugCode
)A
WHERE Amount =
(
    SELECT MAX(Amount)
    FROM
    (
        SELECT DrugCode, Name, COUNT(DrugCode) * UnitCost AS Amount
        FROM
        (
            SELECT Prescription.DrugCode, Drug.Name, Drug.UnitCost
            FROM Prescription LEFT JOIN Drug
            ON Prescription.DrugCode = Drug.DrugCode
        )C
        GROUP BY DrugCode
    )D
);

-- ++++++++++++++++++++
--  Q9
-- ++++++++++++++++++++

SELECT ID, FirstName, LastName, Gender
FROM Person
WHERE ID IN
(
    SELECT PatientID
    FROM Patient
    WHERE PatientID IN
    (
        SELECT PatientID
        FROM Diagnose
        WHERE Disease = 'Diabetes'
    )
  AND PatientID IN
  (
        SELECT PatientID
        FROM Take
        WHERE TestID NOT IN
        (
            SELECT TestID
            FROM MedicalTest
            WHERE Name = 'Lymphocytes'
            OR Name = 'Red Blood Cell'
        )
    )
);

-- ++++++++++++++++++++
--  Q10.A
-- ++++++++++++++++++++

SELECT DISTINCT Disease, Prognosis
FROM Diagnose
WHERE PhysicianID IN
(
  SELECT PhysicianID
  FROM Physician
  WHERE DName = 'Intensive Care Unit'
  AND HName = 'University of Toronto Medical Centre'
);

-- ++++++++++++++++++++
--  Q10.B
-- ++++++++++++++++++++

SELECT PatientID, sum(Fee) AS Total
FROM
(
  SELECT Take.PatientID, MedicalTest.Fee
  FROM Take
  LEFT JOIN MedicalTest MedicalTest ON Take.TestID = MedicalTest.TestID
)PatientAndFees
WHERE PatientID IN
(
  SELECT PatientID
  FROM Diagnose
  WHERE PhysicianID IN
    (
      SELECT PhysicianID
      FROM Physician
      WHERE DName = 'Intensive Care Unit'
      AND HName = 'University of Toronto Medical Centre'
    )
)
GROUP BY PatientID
ORDER BY Total DESC;

-- ++++++++++++++++++++
--  Q10.C
-- ++++++++++++++++++++

SELECT PatientID, sum(UnitCost) AS Total FROM
  (
    SELECT Prescription.PatientID, Drug.UnitCost
    FROM Prescription
    LEFT JOIN Drug ON Prescription.DrugCode = Drug.DrugCode
    WHERE Prescription.PatientID IN
    (
        SELECT PatientID
        FROM Diagnose
        WHERE PhysicianID IN
        (
            SELECT PhysicianID
            FROM Physician
            WHERE DName = 'Intensive Care Unit'
            AND HName = 'University of Toronto Medical Centre'
        )
    )
  )ICUUofT
  GROUP BY PatientID
  ORDER BY Total DESC;


-- ++++++++++++++++++++
--  Q11
-- ++++++++++++++++++++

SELECT ID, FirstName, LastName
FROM Person
WHERE ID IN
(
    SELECT PatientID
    FROM
    (
        SELECT PatientID, COUNT(PatientID)
        FROM Admission
        GROUP BY PatientID
        HAVING COUNT(PatientID = 2)
    )AdmitTwo
)
AND ID NOT IN
(
    SELECT PatientID
    FROM Admission
    WHERE Category = 'non-urgent'
    OR Category = 'immediate'
);


-- +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- END
-- +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
