DROP TABLE IF EXISTS orders CASCADE;
DROP TABLE IF EXISTS location CASCADE;
DROP TABLE IF EXISTS warehouse CASCADE;
DROP TABLE IF EXISTS customer CASCADE;
DROP TABLE IF EXISTS referral CASCADE;
DROP TRIGGER IF EXISTS insert_referral_trigger ON referral CASCADE;
DROP TABLE IF EXISTS product CASCADE;
DROP TABLE IF EXISTS stock CASCADE;
CREATE TABLE location(
    lid         INTEGER     PRIMARY KEY,
    lname       VARCHAR     NOT NULL,
	laddress    VARCHAR     NOT NULL
    );
CREATE TABLE warehouse(
    wid         INTEGER     PRIMARY KEY,
    wname       VARCHAR     NOT NULL,
	lid         INTEGER     REFERENCES location(lid) ON DELETE RESTRICT
    );
CREATE TABLE customer(
    cid         INTEGER     PRIMARY KEY,
    cname       VARCHAR     NOT NULL,
    lid         INTEGER     REFERENCES location(lid) ON DELETE RESTRICT
    );
CREATE TABLE referral(
    custid 	    INTEGER 	REFERENCES customer(cid) ON DELETE RESTRICT,
    custref 	INTEGER 	REFERENCES customer(cid) ON DELETE RESTRICT, 
    commission NUMERIC(6,2),
	PRIMARY KEY(custid, custref));
CREATE TABLE product(
    pid         INTEGER     PRIMARY KEY,
    pname       VARCHAR     NOT NULL,
    introdate   DATE        NOT NULL,
	um          VARCHAR     NOT NULL,
	cost        NUMERIC(6, 2) NOT NULL
    );
CREATE TABLE stock(
    pid         INTEGER           REFERENCES product(pid) ON DELETE RESTRICT,
	wid         INTEGER           REFERENCES warehouse(wid) ON DELETE RESTRICT,
    quantity    NUMERIC(10, 2)     NOT NULL,
    PRIMARY KEY(pid, wid));
CREATE TABLE orders(
    oid         SERIAL      PRIMARY KEY,
    cid         INTEGER     REFERENCES customer(cid) ON DELETE RESTRICT,
    pid         INTEGER     REFERENCES product(pid) ON DELETE RESTRICT,
	odate       DATE        NOT NULL,
	shipwid     INTEGER     REFERENCES warehouse(wid) ON DELETE RESTRICT,
	quantity    NUMERIC(10, 2) NOT NULL,
	price       NUMERIC(6, 2) NOT NULL,
	status      VARCHAR     NOT NULL,
	CHECK (status = 'O' OR status = 'S'));

    CREATE OR REPLACE FUNCTION insert_referral_check()
    RETURNS TRIGGER AS $insert_referral_trigger$
	BEGIN
	    IF NEW.custid = NEW.custref THEN
		   RAISE EXCEPTION 'A customer cannot refer self!';
		END IF;
		IF EXISTS(SELECT * FROM referral
		          WHERE custid = NEW.custref AND custref = NEW.custid) THEN
		   RAISE EXCEPTION 'No back reference allowed!';
		END IF;
		RETURN NEW;
	END;
$insert_referral_trigger$ LANGUAGE PLPGSQL;

CREATE TRIGGER insert_referral_trigger 
     BEFORE INSERT OR UPDATE ON referral 
   FOR EACH ROW EXECUTE PROCEDURE insert_referral_check();

INSERT INTO LOCATION VALUES(101, 'UTStG', '40 St George St');
INSERT INTO LOCATION VALUES(102, 'UTM', '3359 Mississauga Road');
INSERT INTO LOCATION VALUES(103, 'UTSC', '1095 Military Trail');
INSERT INTO LOCATION VALUES(104, 'UCB', 'Berkeley, CA');
INSERT INTO LOCATION VALUES(105, 'GATEC', 'Atlanta, GA');
INSERT INTO WAREHOUSE VALUES(1101, 'Bahen', 101);
INSERT INTO WAREHOUSE VALUES(2101, 'Sid Smith', 101);
INSERT INTO WAREHOUSE VALUES(1102, 'Deerfield Hall', 102);
INSERT INTO WAREHOUSE VALUES(2102, 'Davies', 102);
INSERT INTO WAREHOUSE VALUES(1103, 'IC', 103);
INSERT INTO WAREHOUSE VALUES(2103, 'Bladen', 103);
INSERT INTO WAREHOUSE VALUES(1104, 'Evans Hall', 104);
INSERT INTO WAREHOUSE VALUES(2104, 'Cody Hall', 104);
INSERT INTO CUSTOMER VALUES(3101, 'Newton', 101);
INSERT INTO CUSTOMER VALUES(4101, 'Pauli', 101);
INSERT INTO CUSTOMER VALUES(3102, 'Dijkstra', 102);
INSERT INTO CUSTOMER VALUES(4102, 'Turing', 102);
INSERT INTO CUSTOMER VALUES(3103, 'Knuth', 103);
INSERT INTO CUSTOMER VALUES(4103, 'Grothendieck', 103);
INSERT INTO CUSTOMER VALUES(3104, 'Leibnitz', 104);
INSERT INTO CUSTOMER VALUES(4104, 'Euclid', 104);
INSERT INTO REFERRAL VALUES(3101, 3104, 1.00);
INSERT INTO REFERRAL VALUES(3103, 3102, 0.05);
INSERT INTO REFERRAL VALUES(4101, 3102, 5.00);
INSERT INTO REFERRAL VALUES(3103, 4103, 0.50);
INSERT INTO PRODUCT VALUES(201, 'Calculus', '1687-07-05', 'PCS', 99.99);
INSERT INTO PRODUCT VALUES(202, 'C Language', '1972-01-01', 'LB', 12.00);
INSERT INTO PRODUCT VALUES(203, 'Python', '2017-10-05', 'KG', 12.00);
INSERT INTO PRODUCT VALUES(204, 'SQL', '2017-10-05', 'LB', 120.00);
INSERT INTO PRODUCT VALUES(205, 'FORTRAN', '1957-12-25', 'LB', 120.00);
INSERT INTO STOCK VALUES(204, 1101, 0.00);
INSERT INTO STOCK VALUES(201, 1101, 1000.00);
INSERT INTO STOCK VALUES(202, 1101, 11000.00);
INSERT INTO STOCK VALUES(204, 2104, 1.00);
INSERT INTO STOCK VALUES(201, 2104, 500.00);
INSERT INTO STOCK VALUES(203, 2104, 999.00);
INSERT INTO STOCK VALUES(205, 2104, 0.00);
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