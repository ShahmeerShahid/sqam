build :
	docker-compose up --build

run : 
	docker-compose up

prettier :
	cd admin && npx prettier --write .

test_frontend :
	cd admin && npm test
test_automarker:
	docker exec -it sqam_mysql_1 bash -c "mysql -uroot -psomewordpress wordpress < /var/lib/mysql-files/start.sql"
	cd automarker/SQAM/ && pip3 install -r requirements.txt && python3 SQAM_v3.py 