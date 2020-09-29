-- Add below your SQL statements. 
-- You can create intermediate views (as needed). Remember to drop these views after you have populated the result tables.
-- You can use the "\i a2.sql" command in psql to execute the SQL commands in this file.

-- Query 1 statements
    INSERT INTO Query1 (
    SELECT  c1.cid as cuid,
            c1.cname as cuname,
            c2.cid as refid,
            c2.cname as refname

    FROM    referral r,
            customer c1,
            customer c2

    WHERE   c1.cid = r.custref And
            c2.cid = r.custid

    ORDER BY cuname ASC
    );


-- Query 2 statements
    INSERT INTO Query2 (
    SELECT  o.oid as oid,
            o.pid as pid,
            s.wid as wid,
            o.quantity as ordqty,
            s.quantity as stockqty

    FROM    orders o,
            stock s

    WHERE   o.status = 'O' AND
            s.pid = o.pid AND
            o.shipwid = s.wid AND
            o.quantity > s.quantity
    );



-- Query 3 statements
    INSERT INTO Query3 (
    SELECT  c.cid as cuid,
            c.cname as cuname,
            CAST(SUM(o.quantity*o.price) as NUMERIC(12,2)) as totalsales

    FROM    orders o,
            customer c

    WHERE   o.status = 'S' AND
            o.cid = c.cid

    GROUP BY c.cid, c.cname
    ORDER BY totalsales DESC
    );



-- Query 4 statements
    INSERT INTO Query4 (
    SELECT  p.pid as pid,
            p.pname as pname,
            CAST(SUM(o.quantity*p.cost) AS NUMERIC(12,2)) as totalcost

    FROM    orders o,
            product p

    WHERE   o.status = 'S' AND
            p.pid = o.pid

    GROUP BY p.pid, p.pname
    ORDER BY totalcost ASC
    );


-- Query 5 statements
    INSERT INTO Query5 (
    SELECT  pid,
            pname,
            introdate

    FROM product

    WHERE pid <> ANY( SELECT DISTINCT pid
                        FROM orders)
    ORDER BY pname ASC
    );


-- Query 6 statements
    INSERT INTO Query6 (
    SELECT  c.cid as cid,
            c.cname as cname,
            l.lname as locname

    FROM    location l, customer c

    WHERE   l.lid = c.lid AND
            c.cid <> ANY(SELECT DISTINCT cid
                            FROM orders)
    ORDER BY cname ASC
    );


-- Query 7 statements
    INSERT INTO Query7 (
    SELECT  ym AS period,
            CAST(SUM(sale) as NUMERIC(10,2)) AS sales,
            CAST(SUM(cost) as NUMERIC(10,2)) AS cost

    FROM (
        SELECT  CAST(to_char(now(),'YYYYMM') as INTEGER) AS ym,
                o.price*o.quantity AS sale,
                p.cost * o.quantity AS cost

        FROM    product p,
                orders o

        WHERE   p.pid = o.oid AND
                o.status = 'S'
        ) as newtable

    GROUP BY ym
    ORDER BY period ASC
    );



-- Query 8 statements
    INSERT INTO Query8 (
    SELECT  c.cid as cid,
            c.cname as cname,
            CAST(SUM(r.commission * o.quantity * o.price) as NUMERIC(10,2))AS commission

    FROM    orders o,
            referral r,
            customer c

    WHERE   r.custref = o.cid AND
            r.custid = c.cid

    GROUP BY c.cid, c.cname
    ORDER BY cname ASC
	);


-- Query 9 statements
    INSERT INTO Query9 (
    SELECT  pid,
            date,
            CAST(SUM(sale) as NUMERIC(12,2)) AS totalsales

    FROM (
            SELECT  o.pid,
                    introdate AS date,
                    o.quantity * o.price AS sale

            FROM orders o,
                    product p

            WHERE   p.pid = o.pid AND
                    p.introdate <= CAST('2015-12-31' as DATE) AND
                    o.status = 'S'
            ) as newtable

    GROUP BY pid, date
    ORDER BY date ASC
    );

-- Query 10 statements
    INSERT INTO Query10 (
    SELECT lid,
            lname,
            CAST(SUM(sale) as NUMERIC(12,2)) AS totalsales

    FROM (
        SELECT l.lid as lid,
                l.lname as lname,
                o.quantity * o.price as sale
        FROM orders o, warehouse w, location l
        WHERE o.shipwid = w.wid AND
                w.lid = l.lid AND
                o.status = 'S'
        ) as newtable

    GROUP BY lid, lname
    ORDER BY lname ASC
    );



