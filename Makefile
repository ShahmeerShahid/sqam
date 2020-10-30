build :
	docker-compose up --build

run : 
	docker-compose up

prettier :
	cd admin && npx prettier --write .

test_frontend :
	cd admin && npm test

