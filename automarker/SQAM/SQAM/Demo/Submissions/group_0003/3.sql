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

SELECT DISTINCT Person.FirstName, Person.LastName, Person.Gender, Person.DateOfBirth
FROM Person, Diagnose
WHERE (DATEDIFF(CURDATE(), Person.DateOfBirth) / 365.25) < 41
AND Person.City = 'Toronto'
AND Person.ID = Diagnose.PatientID
AND Diagnose.Disease LIKE '%Cancer%';

-- ++++++++++++++++++++
--  Q3.A
-- ++++++++++++++++++++

SELECT Specialty, AVG(Salary)
FROM Physician
GROUP BY Specialty;

-- ++++++++++++++++++++
--  Q3.B
-- ++++++++++++++++++++

SELECT P.Specialty, AVG(Salary)
FROM Physician P, Hospital H
WHERE P.HName = H.HName
AND (H.City = 'Toronto' OR H.City = 'Hamilton')
AND P.Specialty IN (SELECT Specialty
                FROM Physician
                GROUP BY Specialty
                HAVING COUNT(PhysicianID) >= 5)
GROUP BY P.Specialty;

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

SELECT HName, Count(PatientID)
FROM Admission
WHERE Date >= '2017-08-05' AND Date <= '2017-08-10'
GROUP BY HName;

-- ++++++++++++++++++++
--  Q5.A
-- ++++++++++++++++++++

SELECT DName
FROM Department
GROUP BY DName
HAVING COUNT(HName) IN (SELECT COUNT(*) FROM Hospital);

-- ++++++++++++++++++++
--  Q5.B
-- ++++++++++++++++++++

SELECT DName, HName
FROM (SELECT DName, HName, COUNT(*) AS count
        FROM ((SELECT PhysicianID, DName, HName 
                FROM Physician) 
                UNION (SELECT NurseID, DName, HName 
                        FROM Nurse_Work)) Q1
        GROUP BY DName, HName) Q2
WHERE Q2.count IN (SELECT MAX(count)
                    FROM (SELECT DName, HName, COUNT(*) AS count
                            FROM ((SELECT PhysicianID, DName, HName 
                                    FROM Physician) 
                                    UNION (SELECT NurseID, DName, HName 
                                            FROM Nurse_Work)) Q3
                            GROUP BY DName, HName) Q4);

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

SELECT P.FirstName, P.LastName
FROM Person P, (SELECT NurseID, COUNT(PatientID)
                FROM Patient
                GROUP BY NurseID
                HAVING COUNT(PatientID) < 3) Q
WHERE P.ID = Q.NurseID
ORDER BY P.LastName ASC;

-- ++++++++++++++++++++
--  Q6.B
-- ++++++++++++++++++++

SELECT Pe.FirstName, Pe.LastName
FROM Person Pe, Patient Pa, Diagnose D
WHERE Pe.ID = Pa.PatientID
AND Pa.PatientID = D.PatientID
AND D.Prognosis = 'poor'
AND Pa.NurseID IN (SELECT NurseID
                    FROM Patient
                    GROUP BY NurseID
                    HAVING COUNT(PatientID) < 3);

-- ++++++++++++++++++++
--  Q7
-- ++++++++++++++++++++

SELECT Date
FROM (SELECT Date, Count(PatientID) AS count
        FROM Admission
        WHERE HName = 'Hamilton General Hospital'
        GROUP BY Date) Q1
WHERE Q1.count IN (SELECT MAX(count)
                    FROM (SELECT Date, Count(PatientID) AS count
                            FROM Admission
                            WHERE HName = 'Hamilton General Hospital'
                            GROUP BY Date) Q2);

-- ++++++++++++++++++++
--  Q8
-- ++++++++++++++++++++

SELECT DrugCode, Name, TotalSales
FROM (SELECT P.DrugCode, Name, (UnitCost * COUNT(P.DrugCode)) AS TotalSales
        FROM Prescription P LEFT OUTER JOIN Drug D on P.DrugCode = D.DrugCode
        GROUP BY P.DrugCode) Q1
WHERE Q1.TotalSales IN (SELECT MAX(TotalSales)
                        FROM (SELECT P.DrugCode, Name, (UnitCost * COUNT(P.DrugCode)) AS TotalSales
                                FROM Prescription P LEFT OUTER JOIN Drug D on P.DrugCode = D.DrugCode
                                GROUP BY P.DrugCode) Q2);

-- ++++++++++++++++++++
--  Q9
-- ++++++++++++++++++++

SELECT DISTINCT P.ID, P.FirstName, P.LastName, P.Gender
FROM Person P, Diagnose D
WHERE P.ID = D.PatientID
AND D.Disease = 'Diabetes'
AND D.PatientID NOT IN (SELECT DISTINCT T.PatientID
                        FROM Take T
                        WHERE T.TestID IN (SELECT TestID
                                        FROM MedicalTest
                                        WHERE Name = 'Red Blood Cell' OR Name = 'Lymphocytes'));

-- ++++++++++++++++++++
--  Q10.A
-- ++++++++++++++++++++

SELECT DISTINCT D.Disease, D.Prognosis
FROM Physician Ph, Diagnose D
WHERE Ph.PhysicianID = D.PhysicianID
AND Ph.HName = 'University of Toronto Medical Centre'
AND Ph.DName = 'Intensive Care Unit';

-- ++++++++++++++++++++
--  Q10.B
-- ++++++++++++++++++++

SELECT PatientID, SUM(Fee)
FROM (SELECT Q2.PatientID, Q3.Fee
        FROM (SELECT T.PatientID, T.TestID
                FROM Take T) Q2
        LEFT OUTER JOIN (SELECT M.TestID, M.Fee 
                        FROM MedicalTest M) Q3
        ON Q2.TestID = Q3.TestID) Q1
WHERE PatientID IN (SELECT DISTINCT D.PatientID
                        FROM Physician Ph, Diagnose D
                        WHERE Ph.PhysicianID = D.PhysicianID
                        AND Ph.HName = 'University of Toronto Medical Centre'
                        AND Ph.DName = 'Intensive Care Unit')
GROUP BY PatientID
ORDER BY SUM(Fee) DESC;

-- ++++++++++++++++++++
--  Q10.C
-- ++++++++++++++++++++

SELECT PatientID, SUM(UnitCost)
FROM (SELECT Q2.PatientID, Q3.UnitCost
        FROM (SELECT P.PatientID, P.DrugCode
                FROM Prescription P) Q2
        LEFT OUTER JOIN (SELECT D.DrugCode, D.UnitCost 
                        FROM Drug D) Q3
        ON Q2.DrugCode = Q3.DrugCode) Q1
WHERE PatientID IN (SELECT DISTINCT D.PatientID
                        FROM Physician Ph, Diagnose D
                        WHERE Ph.PhysicianID = D.PhysicianID
                        AND Ph.HName = 'University of Toronto Medical Centre'
                        AND Ph.DName = 'Intensive Care Unit')
GROUP BY PatientID
ORDER BY SUM(UnitCost) DESC;

-- ++++++++++++++++++++
--  Q11
-- ++++++++++++++++++++

SELECT P.ID, P.FirstName, P.LastName
FROM Person P, Admission A
WHERE A.PatientID = P.ID
AND (A.Category = 'urgent' OR A.Category = 'standard')
GROUP BY ID
HAVING COUNT(A.HName) = 2;

-- +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- END
-- +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
