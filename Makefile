build :
	docker build -t "admin_api" ./admin_api
	docker build -t "admin" ./admin

run : 
	docker-compose up
