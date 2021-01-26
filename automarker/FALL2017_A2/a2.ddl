DROP SCHEMA IF EXISTS A2 CASCADE;
CREATE SCHEMA A2;
SET search_path TO A2;

DROP TABLE IF EXISTS location CASCADE;
DROP TABLE IF EXISTS warehouse CASCADE;
DROP TABLE IF EXISTS customer CASCADE;
DROP TABLE IF EXISTS referral CASCADE;
DROP TRIGGER IF EXISTS insert_referral_trigger ON referral CASCADE;
DROP TABLE IF EXISTS product CASCADE;
DROP TABLE IF EXISTS stock CASCADE;
DROP TABLE IF EXISTS orders CASCADE;
DROP TABLE IF EXISTS Query1 CASCADE;
DROP TABLE IF EXISTS Query2 CASCADE;
DROP TABLE IF EXISTS Query3 CASCADE;
DROP TABLE IF EXISTS Query4 CASCADE;
DROP TABLE IF EXISTS Query5 CASCADE;
DROP TABLE IF EXISTS Query6 CASCADE;
DROP TABLE IF EXISTS Query7 CASCADE;
DROP TABLE IF EXISTS Query8 CASCADE;
DROP TABLE IF EXISTS Query9 CASCADE;
DROP TABLE IF EXISTS Query10 CASCADE;

-- The location table contains some locations where EverythingStore conducts business.
-- 'lid' is the id of the location.
-- 'lname' is the name of the location.
CREATE TABLE location(
    lid         INTEGER     PRIMARY KEY,
    lname       VARCHAR     NOT NULL,
	laddress    VARCHAR     NOT NULL
    );

-- The warhouse table contains some warhouses where EverythingStore maintains its merchandise.
-- 'wid' is the id of the warehouse.
-- 'wname' is the name of the warehouse.
CREATE TABLE warehouse(
    wid         INTEGER     PRIMARY KEY,
    wname       VARCHAR     NOT NULL,
	lid         INTEGER     REFERENCES location(lid) ON DELETE RESTRICT
    );
    
-- The customer table contains information about some EverythingStore customers.
-- 'cid' is the id of the customer.
-- 'cname' is the name of the customer.
-- 'lid' is the location id of the customer.
CREATE TABLE customer(
    cid         INTEGER     PRIMARY KEY,
    cname       VARCHAR     NOT NULL,
    lid         INTEGER     REFERENCES location(lid) ON DELETE RESTRICT
    );

-- The referral table provides information about the customers who refer other customers.
-- If custid refers custref, then customer custid receives a commission every time
-- custref makes a purchase from EverythingStore.
-- 'custid' refers to the customer id who makes the referal.
-- 'custref' refers to the customer id referred by custid.
-- 'commission' is the percent of sales of custref awarded to custid.
CREATE TABLE referral (
    custid 	    INTEGER 	REFERENCES customer(cid) ON DELETE RESTRICT,
    custref 	INTEGER 	REFERENCES customer(cid) ON DELETE RESTRICT, 
    commission	NUMERIC(6,2),
	PRIMARY KEY(custid, custref));	
	
-- The product table contains information about some EverythingStore products.
-- 'pid' is the id of the product.
-- 'pname' is the name of the product.
-- 'introdate' is the introduction date of the product.
-- 'um' is the unit of measure of the product.
-- 'cost' is the cost of one unit for the product.
CREATE TABLE product(
    pid         INTEGER     PRIMARY KEY,
    pname       VARCHAR     NOT NULL,
    introdate   DATE        NOT NULL,
	um          VARCHAR     NOT NULL,
	cost        NUMERIC(6, 2) NOT NULL
    );	
	
-- The stock table contains the stock level for each product in each warehouse.
-- 'pid' is the id of the product.
-- 'wid' is the warehouse id.
-- 'quantity' is the number of units of the specific product, expressed in the unit of measure for that product.
CREATE TABLE stock(
    pid         INTEGER           REFERENCES product(pid) ON DELETE RESTRICT,
	wid         INTEGER           REFERENCES warehouse(wid) ON DELETE RESTRICT,
    quantity    NUMERIC(10, 2)     NOT NULL,
    PRIMARY KEY(pid, wid));

-- The orders table contains information about an sales orders.
-- 'oid' is the id of the order.
-- 'cid' is the id of the customer.
-- 'pid' is the id of the sold product.
-- 'odate' is the sales date.
-- 'shipwid' is the actual warehouse where the product was shipped from.
-- 'price' is the sales price.
-- 'quantity' is the sales quantity.
-- 'status' O means Ordered, S means Shipped
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


-- The following tables will be used to store the results of your queries. 
-- Each of them should be populated by your last SQL statement that looks like:
-- "INSERT INTO QueryX (SELECT ...<complete your SQL query here> ... )"

CREATE TABLE query1(
    cuid     INTEGER,
    cuname   VARCHAR,
    refid    INTEGER,
    refname  VARCHAR    
);

CREATE TABLE query2(
    oid   INTEGER,
    pid   INTEGER,
    wid   INTEGER,
    ordqty NUMERIC(10,2),
    stockqty NUMERIC(10,2)    
);

CREATE TABLE query3(
    cuid    INTEGER,
    cuname  VARCHAR,
    totalsales   NUMERIC(12,2)
);

CREATE TABLE query4(
    pid     INTEGER,
    pname   VARCHAR,
    totalcost NUMERIC(12,2)	
);

CREATE TABLE query5(
    pid      INTEGER,
    pname    VARCHAR,
    introdate DATE
);

CREATE TABLE query6(
    cid    INTEGER,
    cname   VARCHAR,
    locname VARCHAR	
);

CREATE TABLE query7(
    period    INTEGER,
    sales     NUMERIC(10,2),
	cost      NUMERIC(10,2)
);

CREATE TABLE query8(
    cid  INTEGER,
    cname  VARCHAR,
    comission   NUMERIC(10,2)   
);

CREATE TABLE query9(
    pid       INTEGER,
    date      DATE,
	totalsales NUMERIC(10,2)
);

CREATE TABLE query10(
    lid       INTEGER,
	lname VARCHAR,
	totalsales NUMERIC(12,2)
);
