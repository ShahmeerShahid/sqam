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