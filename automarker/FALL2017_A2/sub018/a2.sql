-- Add below your SQL statements. 
-- You can create intermediate views (as needed). Remember to drop these views after you have populated the result tables.
-- You can use the "\i a2.sql" command in psql to execute the SQL commands in this file.

-- Query 1 statements
INSERT INTO Query1 (
SELECT ref.custid AS cuid, c1.cname AS cuname, ref.custref AS refid, c2.cname AS refname
FROM referral AS ref
JOIN customer c1 ON c1.cid = ref.custid
JOIN customer c2 ON c2.cid = ref.custref
ORDER BY cuname ASC);


-- Query 2 statements
INSERT INTO Query2 (
SELECT o.oid AS oid, o.pid AS pid, o.shipwid AS wid, o.quantity AS ordqty, s.quantity AS stockqty
FROM orders o
JOIN stock s ON o.pid = s.pid
WHERE o.status = 'O' AND o.quantity > s.quantity);


-- Query 3 statements
INSERT INTO Query3 (
SELECT cuid, c.cname AS cuname, totalsales
FROM ( 
	SELECT cid AS cuid, sum(quantity * price) AS totalsales
	FROM orders
	WHERE status = 'S'
	GROUP BY cid
	) sales 
JOIN customer c ON sales.cuid = c.cid
ORDER BY totalsales DESC);


-- Query 4 statements
INSERT INTO Query4 (
SELECT t.pid AS pid, p.pname AS pname, t.totalqty * p.cost AS totalcost
FROM (
	SELECT pid,sum(quantity) AS totalqty
	FROM orders
	WHERE status = 'S'
	GROUP BY pid
	) t
JOIN product p ON t.pid = p.pid
ORDER BY totalcost ASC);


-- Query 5 statements
INSERT INTO Query5 (
SELECT notordered.pid AS pid, p.pname AS pname, p.introdate AS introdate
FROM (
	SELECT pr.pid AS pid FROM product pr
	EXCEPT
	SELECT DISTINCT o.pid AS pid FROM orders o
	) notordered
JOIN product p ON notordered.pid = p.pid
ORDER BY p.pname ASC);


-- Query 6 statements
INSERT INTO Query6 (
SELECT notordered.cid AS cid, c.cname AS cname, l.lname AS locname
FROM (
	SELECT cu.cid FROM customer cu
	EXCEPT
	SELECT DISTINCT o.cid FROM orders o
	) notordered
JOIN customer c ON notordered.cid = c.cid
JOIN location l ON c.lid = l.lid
ORDER BY c.cname ASC);

-- Query 7 statements
INSERT INTO Query7 (
SELECT period, sum(sale), sum(qty * p.cost)
FROM (
	SELECT to_number(to_char(o.odate, 'YYYYMM'), '999999') AS period, o.quantity AS qty, o.quantity * o.price AS sale, o.oid AS oid, o.pid AS pid
	FROM orders o
	WHERE status = 'S'
	) t
JOIN product p ON t.pid = p.pid
GROUP BY period
ORDER BY period ASC
);

-- Query 8 statements
INSERT INTO Query8 (
SELECT custid, cname, totall
FROM(
	SELECT custid, sum((total * r.commission) / 100) AS totall
	FROM ((
		SELECT referees.cid AS rcid, sum(sale) AS total
		FROM (
			SELECT o.cid, o.quantity * o.price AS sale
			FROM orders o
			WHERE o.cid IN (SELECT custref FROM referral)
			) referees
		GROUP BY referees.cid
		) sales

	JOIN referral r ON rcid = r.custref)
	JOIN customer c ON r.custid = c.cid
	GROUP BY custid) AS sub
JOIN customer cc ON sub.custid = cc.cid
);


-- Query 9 statements
INSERT INTO Query9 (
SELECT u.pid AS pid, v.introdate AS date, u.totalsales AS totalsales
FROM (
	SELECT t.pid AS pid, sum(sale) AS totalsales
	FROM (
		SELECT o.pid AS pid, introdate AS date, o.quantity * o.price AS sale
		FROM orders o JOIN product p ON o.pid = p.pid
		WHERE introdate <= to_date('2015-12-31', 'YYYY-MM-DD')
			AND o.status = 'S'
		ORDER BY o.oid
		) t
	GROUP BY t.pid
	) u
JOIN product v ON v.pid = u.pid
ORDER BY date ASC
);


-- Query 10 statements
INSERT INTO Query10 (
SELECT l.lid AS lid, l.lname AS lname, CASE WHEN totalsales IS NULL THEN 0 ELSE totalsales END AS totalsales
FROM ((
	SELECT t.wid AS wid, sum(sale) AS totalsales
	FROM (
		SELECT o.shipwid AS wid, o.quantity * o.price AS sale
		FROM orders o
		WHERE o.status = 'S'
		) t
	GROUP BY t.wid
	) u
JOIN warehouse w ON u.wid = w.wid) v
RIGHT OUTER JOIN location l ON v.lid = l.lid
ORDER BY lname ASC
);

