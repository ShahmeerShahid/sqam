build :
	docker-compose up --build

run : 
ifeq ($(app), am_backend)
	cd automarker && python3 backend.py 
else ifeq ($(app), automarker)
	docker exec -it sqam_mysql_1 bash -c "mysql -uroot -pcsc499 c499 < /var/lib/mysql-files/start.sql"
	cd automarker/SQAM/ && pip3 install -r requirements.txt && python3 SQAM_v3.py 
else
	docker-compose up
endif

test :
ifeq ($(app), frontend)
	cd admin && npm test
endif

setup :
ifeq ($(app), sql)
	docker exec -it sqam_mysql_1 bash -c "mysql -uroot -pcsc499 c499 < /var/lib/mysql-files/start.sql"
endif

prettier :
	cd admin && npx prettier --write .