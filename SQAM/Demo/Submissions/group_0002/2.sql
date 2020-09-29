-- +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- BEGIN
-- +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

-- ++++++++++++++++++++
--  Q1
-- ++++++++++++++++++++

SELECT H.HName, H.City
FROM Hospital H
WHERE H.AnnualBudget > 3000000
ORDER BY H.AnnualBudget DESC;

-- ++++++++++++++++++++
--  Q2
-- ++++++++++++++++++++

select distinct p.FirstName, p.LastName, p.Gender, p.DateOfBirth
from Person p, Diagnose d where
p.ID = d.PatientID and p.City="Toronto" and d.Disease like "%Cancer%" and 40 >= TIMESTAMPDIFF(year, CURRENT_TIMESTAMP, p.DateOfBirth);

-- ++++++++++++++++++++
--  Q3.A
-- ++++++++++++++++++++

SELECT P.Specialty, AVG(P.Salary)
FROM Physician P
GROUP BY P.Specialty;

-- ++++++++++++++++++++
--  Q3.B
-- ++++++++++++++++++++

SELECT P.Specialty, AVG(P.Salary)
FROM Physician P, Hospital H
WHERE H.HName = P.HName AND ( H.City = "Toronto" OR H.City = "Hamilton")
GROUP BY P.Specialty
HAVING COUNT(*) >= 5;

-- ++++++++++++++++++++
--  Q3.C
-- ++++++++++++++++++++

SELECT N.YearsOfPractice, AVG(N.Salary)
FROM Nurse N
GROUP BY N.YearsOfPractice
ORDER BY N.YearsOfPractice DESC;

-- ++++++++++++++++++++
--  Q4
-- ++++++++++++++++++++

SELECT A.HName, COUNT(*) as NumPatients
FROM Admission A
WHERE A.Date >= DATE('2017-08-5') AND A.Date <= DATE('2017-08-10')
GROUP BY A.HName;

-- ++++++++++++++++++++
--  Q5.A
-- ++++++++++++++++++++

SELECT DISTINCT D.DName
FROM Department D
WHERE NOT EXISTS
  (SELECT H.HName
    FROM Hospital H
    WHERE H.HName NOT IN
                  (SELECT D2.HName
                    FROM Department D2
                    WHERE D2.DName = D.DName));

-- ++++++++++++++++++++
--  Q5.B
-- ++++++++++++++++++++

select t.DName, t.HName from
(select (JD.JDC + JP.JPC) AS countCol, JD.DName, JD.HName from
(select count(*) AS JDC, P.HName, P.DName from Physician P group by P.HName, P.DName) JD inner join (select count(*) AS JPC, N.HName, N.DName from
Nurse_Work N  group by N.HName, N.DName) JP on JP.HName=JD.HName and JP.DName=JD.DName) t
where t.countCol =
(select MAX(JD.JDC + JP.JPC) from
(select count(*) AS JDC, P.HName, P.DName from Physician P group by P.HName, P.DName) JD inner join (select count(*) AS JPC, N.HName, N.DName from
Nurse_Work N  group by N.HName, N.DName) JP on JP.HName=JD.HName and JP.DName=JD.DName);

-- ++++++++++++++++++++
--  Q5.C
-- ++++++++++++++++++++

SELECT D1.DName
FROM Department D1, Hospital H1
WHERE D1.HName = H1.HName AND D1.DName NOT IN (SELECT D2.DName
                                               FROM Department D2, Hospital H2
                                               WHERE H2.HName <> H1.HName AND D2.HName = H2.HName);

-- ++++++++++++++++++++
--  Q6.A
-- ++++++++++++++++++++

select temp.FirstName, temp.LastName from ((SELECT P1.FirstName, P1.LastName
FROM Patient P, Person P1
WHERE P.NurseID = P1.ID
GROUP BY P.NurseID
HAVING COUNT(*) < 3)
UNION
(SELECT P2.FirstName, P2.LastName
FROM Person P2, Nurse N
WHERE N.NurseID = P2.ID AND N.NurseID NOT IN (SELECT P3.NurseID FROM Patient P3))) temp order by temp.LastName;

-- ++++++++++++++++++++
--  Q6.B
-- ++++++++++++++++++++

SELECT P.PatientID
FROM Patient P, Diagnose D
WHERE D.PatientID = P.PatientID AND D.Prognosis = 'poor' AND P.NurseID IN (SELECT P1.ID
         FROM Patient P, Person P1
         WHERE P.NurseID = P1.ID
         GROUP BY P.NurseID
         HAVING COUNT(*) < 3)
         UNION
         (SELECT P2.ID
           FROM Person P2, Nurse N
           WHERE N.NurseID = P2.ID AND N.NurseID NOT IN (SELECT P3.NurseID FROM Patient P3));

-- ++++++++++++++++++++
--  Q7
-- ++++++++++++++++++++

SELECT d.date FROM (SELECT COUNT(*) as c, A1.Date FROM Admission A1 
  WHERE A1.HName = "Hamilton General Hospital" 
  GROUP BY A1.Date ORDER BY count(*) desc) d 
  where d.c = 
  (SELECT temp.c FROM 
    (SELECT COUNT(*) as c, A1.Date FROM Admission A1 
      WHERE A1.HName = "Hamilton General Hospital" GROUP BY A1.Date ORDER BY count(*) desc) temp limit 1);

-- ++++++++++++++++++++
--  Q8
-- ++++++++++++++++++++
SELECT temp.DrugCode, temp.Name, (temp.qty * temp.UnitCost) as Revenue
FROM (SELECT * FROM
     (SELECT P.DrugCode, COUNT(*) AS QTY
      FROM Prescription P
      GROUP BY P.DrugCode) PD
NATURAL JOIN Drug D) temp
ORDER BY Revenue DESC LIMIT 1;

-- ++++++++++++++++++++
--  Q9
-- ++++++++++++++++++++

SELECT P1.ID, P1.FirstName, P1.LastName, P1.Gender
FROM Person P1, Patient P2, Diagnose D
WHERE P2.PatientID = P1.ID AND D.PatientID = P2.PatientID AND D.Disease = 'Diabetes'
      AND P2.PatientID NOT IN
                            (SELECT T.PatientID
                            FROM Take T, MedicalTest M
                            WHERE (M.Name = 'Red Blood Cell' OR M.Name = 'Lymphocytes') AND M.TestID=T.TestID);

-- ++++++++++++++++++++
--  Q10.A
-- ++++++++++++++++++++

SELECT DISTINCT D.Disease, D.Prognosis
FROM Diagnose D, Physician P
WHERE D.PhysicianID = P.PhysicianID AND P.DName = 'Intensive Care Unit' AND P.HName = 'University of Toronto Medical Centre';

-- ++++++++++++++++++++
--  Q10.B
-- ++++++++++++++++++++

SELECT temp.PatientID, IFNULL(SUM(temp.Fee), 0) as Cost 
from 
(select t.*, M.Fee as Fee 
from 
(select p.*, T.TestID 
from 
(SELECT DISTINCT D.PatientID
FROM Diagnose D, Physician P WHERE D.PhysicianID = P.PhysicianID AND P.DName = 'Intensive Care Unit' AND P.HName = 'University of Toronto Medical Centre') as p 
inner JOIN Take T on T.PatientID = p.PatientID) t 
inner JOIN MedicalTest M on M.TestID = t.TestID) temp
GROUP BY temp.PatientID
ORDER BY Cost DESC;

-- ++++++++++++++++++++
--  Q10.C
-- ++++++++++++++++++++


SELECT temp.PatientID, IFNULL(SUM(temp.UnitCost), 0) as Cost from 
(select t.*, D.UnitCost as UnitCost from (select p.*, pr.DrugCode from 
  (SELECT DISTINCT D.PatientID FROM Diagnose D, Physician P WHERE D.PhysicianID = P.PhysicianID AND P.DName = 'Intensive Care Unit' AND P.HName = 'University of Toronto Medical Centre') as p 
  inner JOIN Prescription pr on pr.PatientID = p.PatientID) t 
  inner JOIN Drug D on D.DrugCode = t.DrugCode) temp 
  GROUP BY temp.PatientID 
  ORDER BY Cost DESC;

-- ++++++++++++++++++++
--  Q11
-- ++++++++++++++++++++

SELECT P.ID, P.FirstName, P.LastName
FROM Patient Pt, Person P, Hospital H1, Admission A1
WHERE Pt.PatientID = P.ID AND A1.HName = H1.HName AND A1.PatientID = Pt.PatientID
      AND (A1.Category='urgent' OR A1.Category='standard')
GROUP BY P.ID
HAVING COUNT(*) = 2;

-- +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- END
-- +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++












