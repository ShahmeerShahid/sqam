-- START Query 1


    SELECT cu.cid AS cuid, cu.cname AS cuname, ref.cid AS refid, ref.cname AS refname
    FROM referral
    JOIN customer cu ON referral.custid=cu.cid
    JOIN customer ref ON referral.custref=ref.cid
    ORDER BY cuname
;

-- END Query 1
-- START Query 2

--Note that ORDER BY was not specified in this question!

    SELECT ord.oid AS oid, ord.pid AS pid, ord.shipwid AS wid,
    CAST(ord.quantity AS NUMERIC(10,2)) AS ordqty, CAST(st.quantity AS NUMERIC(10,2)) AS stockqty
    FROM orders ord JOIN stock st ON (ord.pid=st.pid AND ord.shipwid=st.wid)
    WHERE ord.status='O' AND st.quantity < ord.quantity
;

-- END Query 2
-- START Query 3


    SELECT ord.cid AS cuid, cu.cname AS cuname, CAST(SUM(ord.quantity * ord.price) AS NUMERIC(12,2)) AS totalsales
    FROM orders ord JOIN customer cu ON ord.cid=cu.cid
    WHERE ord.status='S'
    GROUP BY ord.cid, cu.cname
    ORDER BY totalsales DESC
;

-- END Query 3
-- START Query 4


    SELECT pr.pid AS pid, pr.pname AS pname, CAST(SUM(ord.quantity * pr.cost) AS NUMERIC(12,2)) AS totalcost
    FROM orders ord JOIN product pr ON ord.pid=pr.pid
    WHERE ord.status='S'
    GROUP BY pr.pid
    ORDER BY totalcost
;

-- END Query 4
-- START Query 5


	SELECT pr.pid AS pid, pr.pname AS pname, pr.introdate AS introdate
	FROM product pr
	WHERE pr.pid NOT IN (SELECT pid FROM orders GROUP BY pid)
	ORDER BY pname
;

-- END Query 5
-- START Query 6


	SELECT cu.cid AS cid, cu.cname AS cname, lo.lname AS locname
	FROM customer cu JOIN location lo ON cu.lid = lo.lid
	WHERE cu.cid NOT IN (SELECT cid FROM orders GROUP BY cid)
	ORDER BY cname
;

-- END Query 6
-- START Query 7


	SELECT DATE_PART('YEAR', ord.odate) * 100 + DATE_PART('MONTH', ord.odate) AS period,
	CAST(SUM(ord.quantity * ord.price) AS NUMERIC(10,2)) AS sales,
	CAST(SUM(ord.quantity * pr.cost) AS NUMERIC(10,2)) AS cost
	FROM orders ord JOIN product pr ON ord.pid=pr.pid
	GROUP BY period
	ORDER BY period
;

-- END Query 7
-- START Query 8


    SELECT re.custid AS cid, cu.cname AS cname, CAST(SUM((temp.totalsales * re.commission/100)) AS NUMERIC(10,2)) AS commission
    FROM referral re JOIN (SELECT ord.cid cid, SUM(ord.price * ord.quantity) totalsales FROM orders ord GROUP BY ord.cid) temp
    ON re.custref = temp.cid JOIN customer cu ON re.custid=cu.cid
    GROUP BY re.custid, cu.cname
    ORDER BY cname
;

-- END Query 8
-- START Query 9


    SELECT pr.pid AS pid, pr.introdate AS date, CAST(SUM(ord.quantity * ord.price) AS NUMERIC(12,2)) AS totalsales
    FROM orders ord JOIN product pr ON ord.pid=pr.pid
    WHERE ord.status='S' AND pr.introdate<='2015-12-31'
    GROUP BY pr.pid, pr.introdate
    ORDER BY date
;

-- END Query 9
-- START Query 10


    SELECT temp.lid AS lid, lo.lname AS lname, CAST(COALESCE(temp.totalsales, 0) AS NUMERIC(12,2)) AS totalsales
    FROM (SELECT wa.lid AS lid, ord.shipwid, SUM(ord.quantity * ord.price) AS totalsales
    FROM orders ord FULL JOIN warehouse wa ON wa.wid=ord.shipwid
    GROUP BY ord.shipwid, wa.lid) temp
    JOIN location lo ON temp.lid=lo.lid
    ORDER BY lname
;

-- END Query 10
