build :
	docker-compose up --build

run : 
	docker-compose up

prettier :
	cd admin && npx prettier --write .

test_frontend :
	cd admin && npm test

setup_sql:
	docker exec -it sqam_mysql_1 bash -c "mysql -uroot -pcsc499 c499 < /var/lib/mysql-files/start.sql"

test_automarker:
	docker exec -it sqam_mysql_1 bash -c "mysql -uroot -pcsc499 c499 < /var/lib/mysql-files/start.sql"
	cd automarker/SQAM/ && pip3 install -r requirements.txt && python3 SQAM_v3.py 

test_backend: 
	cd /Users/vaishvik/Desktop/sqam/automarker && python3 backend.py 