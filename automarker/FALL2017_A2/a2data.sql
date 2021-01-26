SET search_path TO A2;
-- location id (int, primary key), location name(varchar not null), location address (varchar not null)
INSERT INTO LOCATION VALUES(101, 'UTStG', '40 St George St');
INSERT INTO LOCATION VALUES(102, 'UTM', '3359 Mississauga Road');
INSERT INTO LOCATION VALUES(103, 'UTSC', '1095 Military Trail');
INSERT INTO LOCATION VALUES(104, 'UCB', 'Berkeley, CA');
INSERT INTO LOCATION VALUES(105, 'GATEC', 'Atlanta, GA');

-- wid (int primary key), wname (varchar not null), lid (ref location id)
INSERT INTO WAREHOUSE VALUES(1101, 'Bahen', 101);
INSERT INTO WAREHOUSE VALUES(2101, 'Sid Smith', 101);
INSERT INTO WAREHOUSE VALUES(1102, 'Deerfield Hall', 102);
INSERT INTO WAREHOUSE VALUES(2102, 'Davies', 102);
INSERT INTO WAREHOUSE VALUES(1103, 'IC', 103);
INSERT INTO WAREHOUSE VALUES(2103, 'Bladen', 103);
INSERT INTO WAREHOUSE VALUES(1104, 'Evans Hall', 104);
INSERT INTO WAREHOUSE VALUES(2104, 'Cody Hall', 104);

-- cid (int primary key), cname (varchar not null), lid (ref location id)
INSERT INTO CUSTOMER VALUES(3101, 'Newton', 101);
INSERT INTO CUSTOMER VALUES(4101, 'Pauli', 101);
INSERT INTO CUSTOMER VALUES(3102, 'Dijkstra', 102);
INSERT INTO CUSTOMER VALUES(4102, 'Turing', 102);
INSERT INTO CUSTOMER VALUES(3103, 'Knuth', 103);
INSERT INTO CUSTOMER VALUES(4103, 'Grothendieck', 103);
INSERT INTO CUSTOMER VALUES(3104, 'Leibnitz', 104);
INSERT INTO CUSTOMER VALUES(4104, 'Euclid', 104);

-- custid, custref, commission (% 6.2)
INSERT INTO REFERRAL VALUES(3101, 3104, 1.00);
INSERT INTO REFERRAL VALUES(3103, 3102, 0.05);
INSERT INTO REFERRAL VALUES(4101, 3102, 5.00);
INSERT INTO REFERRAL VALUES(3103, 4103, 0.50);

	
-- pid int primary key, pname varchar not null, introdate not null, um varchar not null, cost num 6.2 not null
INSERT INTO PRODUCT VALUES(201, 'Calculus', '1687-07-05', 'PCS', 99.99);
INSERT INTO PRODUCT VALUES(202, 'C Language', '1972-01-01', 'LB', 12.00);
INSERT INTO PRODUCT VALUES(203, 'Python', '2017-10-05', 'KG', 12.00);
INSERT INTO PRODUCT VALUES(204, 'SQL', '2017-10-05', 'LB', 120.00);
INSERT INTO PRODUCT VALUES(205, 'FORTRAN', '1957-12-25', 'LB', 120.00);
	
-- pid int, wid int, qty 10.2, primary key (pid,wid)
INSERT INTO STOCK VALUES(204, 1101, 0.00);
INSERT INTO STOCK VALUES(201, 1101, 1000.00);
INSERT INTO STOCK VALUES(202, 1101, 11000.00);
INSERT INTO STOCK VALUES(204, 2104, 1.00);
INSERT INTO STOCK VALUES(201, 2104, 500.00);
INSERT INTO STOCK VALUES(203, 2104, 999.00);
INSERT INTO STOCK VALUES(205, 2104, 0.00);

-- oid serial primary key, cid int, pid int, odate, shipwid, qty 10.2, price 6.2, status (O,S)
INSERT INTO ORDERS(cid, pid, odate,shipwid, quantity, price, status) 
            VALUES(3101, 201, '1687-07-06', 1101, 2.00, 101.00, 'O');
INSERT INTO ORDERS(cid, pid, odate,shipwid, quantity, price, status) 
            VALUES(3102, 201, '2017-05-05', 1101, 200.00, 11.00, 'S');
INSERT INTO ORDERS(cid, pid, odate,shipwid, quantity, price, status)  
            VALUES(4103, 204, '2017-05-12', 1101, 200.00, 1.00, 'S');
INSERT INTO ORDERS(cid, pid, odate,shipwid, quantity, price, status)  
            VALUES(3101, 202, '1687-07-06', 2104, 2.00, 101.00, 'S');
INSERT INTO ORDERS(cid, pid, odate,shipwid, quantity, price, status) 
            VALUES(3101, 202, '2017-07-06', 2104, 6.00, 101.00, 'S');
INSERT INTO ORDERS(cid, pid, odate,shipwid, quantity, price, status)
            VALUES(3101, 205, '1687-07-06', 2104, 2.00, 101.00, 'O');
INSERT INTO ORDERS(cid, pid, odate,shipwid, quantity, price, status) 
            VALUES(3103, 205, '1687-07-06', 2104, 20.00, 101.00, 'O');
