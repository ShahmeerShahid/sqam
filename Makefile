run : 
	docker-compose up

build :
	docker-compose build

manage :
	docker-compose run web python manage.py ${cmd}

migrate :
	docker-compose run web python manage.py migrate

migrations :
	docker-compose run web python manage.py makemigrations

test :
	docker-compose run web python manage.py test