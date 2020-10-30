build :
	docker build -t "admin_api" ./admin_api
	docker build -t "admin" ./admin
run : 
	docker-compose up
start:
	docker exec -it sqam_mysql_1 bash -c "mysql -uroot -psomewordpress wordpress < /var/lib/mysql-files/start.sql"
	cd automarker/SQAM/ && pip3 install -r requirements.txt && python3 SQAM_v3.py 