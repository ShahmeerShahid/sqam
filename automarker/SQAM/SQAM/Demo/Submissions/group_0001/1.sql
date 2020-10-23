-- +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- BEGIN
-- +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

-- ++++++++++++++++++++
--  Q1
-- ++++++++++++++++++++

SELECT h.HName AS name, h.City AS 'city'
FROM Hospital h
WHERE h.AnnualBudget > 3000000
ORDER BY h.AnnualBudget DESC;

-- ++++++++++++++++++++
--  Q2
-- ++++++++++++++++++++

SELECT DISTINCT p.FirstName AS 'first name', p.LastName AS 'last name',
	p.Gender AS 'gender', p.DateOfBirth AS 'date of birth'
FROM Diagnose d, Person p
WHERE d.Disease LIKE '%Cancer' AND d.PatientID = p.ID
	AND p.City = 'Toronto' AND p.DateOfBirth >= 1979-11-09;

-- ++++++++++++++++++++
--  Q3.A
-- ++++++++++++++++++++

SELECT AVG(p.Salary) AS 'average salary', p.Specialty
FROM Physician p
GROUP BY p.Specialty;

-- ++++++++++++++++++++
--  Q3.B
-- ++++++++++++++++++++

SELECT AVG(p.Salary) AS 'average salary', p.Specialty
FROM Physician p, Hospital h
WHERE p.HName = h.HName AND h.City IN ('Toronto', 'Hamilton')
GROUP BY p.Specialty
HAVING COUNT(p.PhysicianID) >= 5;

-- ++++++++++++++++++++
--  Q3.C
-- ++++++++++++++++++++

SELECT AVG(n.Salary) AS 'average salary', n.YearsOfPractice
FROM Nurse n
GROUP BY n.YearsOfPractice
ORDER BY n.YearsOfPractice DESC;

-- ++++++++++++++++++++
--  Q4
-- ++++++++++++++++++++

SELECT COUNT(a.PatientID) AS 'number of patients', a.HName AS 'hospital name'
FROM Admission a
WHERE a.Date > 2017-08-05 AND a.Date > 2017-08-10
GROUP BY a.HName;

-- ++++++++++++++++++++
--  Q5.A
-- ++++++++++++++++++++

SELECT DISTINCT d.DName AS 'department name'
FROM Department d
WHERE NOT EXISTS(SELECT h.HName
FROM Hospital h
WHERE NOT h.HName IN(SELECT d2.HName
FROM Department d2
WHERE d2.DName = d.DName));

-- ++++++++++++++++++++
--  Q5.B
-- ++++++++++++++++++++

SELECT DISTINCT d.DName
FROM Department d, Nurse_Work n, Physician p
WHERE n.DName = d.DName OR p.DName = d.DName
GROUP BY d.HName, d.DName
HAVING COUNT(DISTINCT p.PhysicianID) + COUNT(DISTINCT n.NurseID) = (SELECT MAX(num)
FROM (SELECT COUNT(DISTINCT p.PhysicianID) + COUNT(DISTINCT n.NurseID) AS 'num'
	FROM Department d, Nurse_Work n, Physician p
	WHERE n.DName = d.DName OR p.DName = d.DName
	GROUP BY d.HName, d.DName) JD);

-- ++++++++++++++++++++
--  Q5.C
-- ++++++++++++++++++++

SELECT d.DName
FROM Department d
GROUP BY d.DName
HAVING COUNT(d.HName) = 1;

-- ++++++++++++++++++++
--  Q6.A
-- ++++++++++++++++++++

SELECT p.FirstName AS 'first name', p.LastName AS 'last name'
FROM Person p, Nurse n
WHERE n.NurseID = p.ID AND (SELECT COUNT(pa.PatientID)
	FROM Patient pa
	WHERE pa.NurseID = n.NurseID) < 3;

-- ++++++++++++++++++++
--  Q6.B
-- ++++++++++++++++++++

SELECT pe.FirstName AS 'first name', pe.LastName AS 'last name'
FROM Patient p, Diagnose d, Person pe
WHERE p.PatientID = pe.ID AND p.PatientID = d.PatientID AND d.Prognosis = 'poor' AND p.NurseID IN (SELECT n.NurseID
	FROM Nurse n
	WHERE (SELECT COUNT(pa.PatientID)
	FROM Patient pa
	WHERE pa.NurseID = n.NurseID) < 3);

-- ++++++++++++++++++++
--  Q7
-- ++++++++++++++++++++

SELECT ao.Date
FROM Admission ao
WHERE ao.HName = 'Hamilton General Hospital'
GROUP BY ao.Date
HAVING COUNT(ao.PatientID) = (SELECT MAX(JD.num)
FROM (SELECT COUNT(a.PatientID) AS 'num'
	FROM Admission a
	WHERE a.HName = 'Hamilton General Hospital'
	GROUP BY a.Date) JD);

-- ++++++++++++++++++++
--  Q8
-- ++++++++++++++++++++

SELECT do.DrugCode AS 'drugcode', do.Name AS 'name'
FROM Prescription po, Drug do
WHERE po.DrugCode = do.DrugCode
GROUP BY po.DrugCode
HAVING SUM(do.UnitCost) = (SELECT MAX(JD.revenue)
FROM (SELECT SUM(d.UnitCost) AS 'revenue'
	FROM Prescription p, Drug d
	WHERE p.DrugCode = d.DrugCode
	GROUP BY p.DrugCode) JD);

-- ++++++++++++++++++++
--  Q9
-- ++++++++++++++++++++

SELECT p.ID, p.FirstName AS 'first name', p.LastName AS 'last name', p.Gender AS 'gender'
FROM Diagnose d, Person p
WHERE d.Disease = 'Diabetes' AND p.ID = d.PatientID AND NOT EXISTS(SELECT *
	FROM MedicalTest m, Take t
	WHERE p.ID = t.PatientID AND t.TestID = m.TestID AND m.Name IN ('Red Blood Cell', 'Lymphocytes'));

-- ++++++++++++++++++++
--  Q10.A
-- ++++++++++++++++++++

SELECT DISTINCT d.PhysicianID, d.PatientID, d.Disease, d.Prognosis
FROM Physician ph, Diagnose d
WHERE ph.HName = 'University OF Toronto Medical Centre'
	AND ph.DName = 'Intensive Care Unit'
	AND d.PhysicianID = ph.PhysicianID;

-- ++++++++++++++++++++
--  Q10.B
-- ++++++++++++++++++++

SELECT t.PatientID, SUM(m.fee) AS 'total cost'
FROM Physician ph, Diagnose d, MedicalTest m, Take t
WHERE ph.HName = 'University OF Toronto Medical Centre'
	AND ph.DName = 'Intensive Care Unit'
	AND d.PhysicianID = ph.PhysicianID
	AND d.PatientID = t.PatientID
	AND t.TestID = m.TestID
GROUP BY t.PatientID
ORDER BY SUM(m.fee) DESC;

-- ++++++++++++++++++++
--  Q10.C
-- ++++++++++++++++++++

SELECT pr.PatientID, SUM(dr.UnitCost) AS 'total cost'
FROM Physician ph, Diagnose d, Prescription pr, Drug dr
WHERE ph.HName = 'University OF Toronto Medical Centre'
	AND ph.DName = 'Intensive Care Unit'
	AND d.PhysicianID = ph.PhysicianID
	AND d.PatientID = pr.PatientID
	AND pr.DrugCode = dr.DrugCode
GROUP BY pr.PatientID
ORDER BY SUM(dr.UnitCost) DESC;

-- ++++++++++++++++++++
--  Q11
-- ++++++++++++++++++++

SELECT p.ID, p.FirstName AS 'first name', p.LastName AS 'last name'
FROM Person p
WHERE p.ID IN
	(SELECT a.PatientID
FROM Admission a
WHERE a.Category IN ('urgent', 'standard')
GROUP BY a.PatientID
HAVING COUNT(DISTINCT a.HName) = 2);

-- +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- END
-- +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
