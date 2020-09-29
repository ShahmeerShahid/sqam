-- +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- BEGIN
-- +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

-- ++++++++++++++++++++
--  Q1
-- ++++++++++++++++++++
select H.HName, H.City  from Hospital H where H.AnnualBudget > 3000000 order by H.AnnualBudget desc;

-- ++++++++++++++++++++
--  Q2
-- ++++++++++++++++++++

select P1.FirstName, P1.LastName, P1.Gender , P1.DateOfBirth from Person P1, Patient P2, Diagnose D where P2.PatientID = P1.ID and D.PatientID = P2.PatientID and  P1.City = 'Toronto' and ('2019-11-14' - P1.DateofBirth)/365 < 41 and   D.Disease like '%cancer%';

-- ++++++++++++++++++++
--  Q3.A
-- ++++++++++++++++++++

select P.Specialty, avg(P.Salary) from Physician P  group by P.Specialty;

-- ++++++++++++++++++++
--  Q3.B
-- ++++++++++++++++++++

select P.Specialty, avg(P.Salary) from Physician P where P.HName in (select P1.HName from Physician P1, Hospital H1 where P1.HName = H1.HName and H1.City = 'Hamilton' union select P1.HName from Physician P1, Hospital H1 where P1.HName = H1.HName and H1.City = 'Toronto') group by P.Specialty having count(*) >= 5;

-- ++++++++++++++++++++
--  Q3.C
-- ++++++++++++++++++++
select N.YearsOfPractice, avg(N.Salary) from Nurse N  group by N.YearsOfPractice order by N.YearsOfPractice desc;

-- ++++++++++++++++++++
--  Q4
-- ++++++++++++++++++++
select A.HName, count(A.PatientID)  from Admission A where A.Date >= '2017-08-05' and A.Date <= '2017-08-10' group by A.HName;

-- ++++++++++++++++++++
--  Q5.A
-- ++++++++++++++++++++
select distinct(D.DName) from Department D where D.HName in (select H.HName from Hospital H) and (select count(distinct(D1.HName)) from Department D1 where D1.DName = D.DName) = (select count(*) from Hospital);

-- ++++++++++++++++++++
--  Q5.B
-- ++++++++++++++++++++
select Temp2.DName, Temp2.HName from (select Temp.DName, Temp.HName, (Temp.Num_nurse + Temp1.Num_Phy) as total from (select N.DName, N.HName, count(N.NurseID) as Num_nurse from Nurse_Work N group by N.DName, N.HName) as Temp , (select P.DName, P.HName, count(P.PhysicianID) as Num_Phy from Physician P group by P.DName, P.HName) as Temp1 where Temp1.HName = Temp.HName and Temp1.DName = Temp.DName) as Temp2 where Temp2.total = (select max(temp2.total) from (select temp.DName, temp.HName, (temp.Num_nurse + temp1.Num_Phy) as total from (select N.DName, N.HName, count(N.NurseID) as Num_nurse from Nurse_Work N group by N.DName, N.HName) as temp , (select P.DName, P.HName, count(P.PhysicianID) as Num_Phy from Physician P group by P.DName, P.HName) as temp1 where temp1.HName = temp.HName and temp1.DName = temp.DName) as temp2 );

-- ++++++++++++++++++++
--  Q5.C
-- ++++++++++++++++++++
select D.DName from Department D  where not exists (select * from  Department D1 where D1.DName = D.DName and D.HName <> D1.HName) ;

-- ++++++++++++++++++++
--  Q6.A
-- ++++++++++++++++++++
select P.FirstName, P.LastName from Person P, Nurse N where P.ID = N.NurseID and  N.NurseID in (select temp.NurseID from ( select Pa.NurseID, count(Pa.PatientID)  from Patient Pa  group by Pa.NurseID having count(*) <= 3) as temp) order by P.LastName;

-- ++++++++++++++++++++
--  Q6.B
-- ++++++++++++++++++++

select P.PatientID from Patient P, Diagnose D where P.PatientID = D.PatientID and D.Prognosis = 'poor' and P.NurseID in (select temp1. NurseID from (select P.FirstName, P.LastName, N.NurseID from Person P, Nurse N where P.ID = N.NurseID and  N.NurseID in (select temp.NurseID from ( select Pa.NurseID, count(Pa.PatientID)  from Patient Pa  group by Pa.NurseID having count(*) <= 3) as temp) order by P.LastName) as temp1);

-- ++++++++++++++++++++
--  Q7
-- ++++++++++++++++++++

select temp1.Date from (select A1.Date, count(A1.PatientID) as total from Admission A1 where A1.HName = 'Hamilton General Hospital' group by A1.Date) as temp1 where temp1.total in (select max(temp.total) from (select A1.Date, count(A1.PatientID) as total from Admission A1 where A1.HName = 'Hamilton General Hospital' group by A1.Date) as temp);

-- ++++++++++++++++++++
--  Q8
-- ++++++++++++++++++++
select temp_total.DrugCode, temp_total.total from (select temp_unitcost.DrugCode, (temp_unitcost.UnitCost *  temp_amount.amount) as total  from (select distinct(D.DrugCode), D.Unitcost  from Drug D , Prescription P where D.DrugCode = P.DrugCode) as temp_unitcost, (select P.DrugCode, count(*) as amount  from Prescription P group by P.DrugCode) as temp_amount where temp_unitcost.DrugCode = temp_amount.DrugCode)as temp_total where temp_total.total = (select max(Temp_total.total) from (select Temp_unitcost.DrugCode, (Temp_unitcost.UnitCost *  Temp_amount.amount) as total  from (select distinct(D.DrugCode), D.Unitcost  from Drug D , Prescription P where D.DrugCode = P.DrugCode) as Temp_unitcost, (select P.DrugCode, count(*) as amount  from Prescription P group by P.DrugCode) as Temp_amount where Temp_unitcost.DrugCode = Temp_amount.DrugCode) as Temp_total);

-- ++++++++++++++++++++
--  Q9
-- ++++++++++++++++++++
select P.ID, P.FirstName, P.LastName, P.Gender from Person P, Diagnose D where D.PatientID = P.ID  and D.Disease = 'Diabetes' and D.PatientID in (select T.PatientID  from Take T, MedicalTest M  where M.TestID = T.TestID  and M.Name <> 'Red Blood Cell'  union  ( select T.PatientID  from Take T, MedicalTest M  where M.TestID = T.TestID  and M.Name <> 'Lymphocytes' ));

-- ++++++++++++++++++++
--  Q10.A
-- ++++++++++++++++++++
select temp.Disease, temp.Prognosis from (select distinct(concat(D.Disease, D.Prognosis)), D.Disease, D.Prognosis  from Diagnose D where D.PhysicianID in (select PhysicianID from Physician  where DName = 'Intensive Care Unit' and HName = 'University of Toronto Medical Centre')) temp;
-- ++++++++++++++++++++
--  Q10.B
-- ++++++++++++++++++++
select T.PatientID, sum(M.Fee) as TotalCost  from Take T, MedicalTest M  where T.TestID = M.TestID and T.PatientID in (select distinct(D.PatientID) from  Diagnose D where D.PhysicianID in (select PhysicianID from Physician  where DName = 'Intensive Care Unit' and HName = 'University of Toronto Medical Centre')) group by T.PatientID order by TotalCost desc;

-- ++++++++++++++++++++
--  Q10.C
-- ++++++++++++++++++++
select P.PatientID, sum(D.UnitCost) as TotalCost from Prescription P, Drug D  where P.DrugCode = D.DrugCode and P.PatientID in (select distinct(D.PatientID) from  Diagnose D where D.PhysicianID in(select PhysicianID from Physician  where DName = 'Intensive Care Unit' and HName = 'University of Toronto Medical Centre')) group by P.PatientID order by TotalCost desc;

-- ++++++++++++++++++++
--  Q11
-- ++++++++++++++++++++
select P.ID, P.FirstName, P.LastName from Person P, Patient Pa where P.ID = Pa.PatientID and Pa.PatientID in (select temp1.PatientID from ((select A.PatientID, count(A.HName)  from Admission A, Hospital H where A.HName = H.HName  and A.PatientID in (select temp.PatientID from ((select A.PatientID from Admission A where Category = 'urgent') union  (select A.PatientID from Admission A where Category = 'standard')) as temp) group by A.PatientID having count(*) = 2) as temp1));

-- +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- END
-- +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
