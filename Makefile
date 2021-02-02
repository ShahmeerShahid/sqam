build :
	docker-compose up --build

down :
	docker-compose down --remove-orphans

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
else ifeq ($(app), backend)
	docker-compose up -d
	docker-compose run admin_api npm test
	docker-compose down
else
	docker-compose up -d
	docker-compose run admin npm test
	docker-compose run admin_api npm test
	docker-compose down
endif

setup :
ifeq ($(app), sql)
	docker exec -it sqam_mysqlam_1 bash -c "mysql -uroot -pcsc499 c499 < /var/lib/mysql-files/start.sql"
endif

prettier :
	cd admin && npx prettier --write .
	cd admin_api && npx prettier --write .