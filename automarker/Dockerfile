#Flask server image
FROM tiangolo/uwsgi-nginx-flask:python3.6-alpine3.7
#Setup working directory
WORKDIR /automarker
#Copy all files into container
COPY . .
#Install the essential libraries on alpine 
RUN apk add --no-cache gcc musl-dev
#upgrade pip
RUN pip3 install --upgrade pip
# Install the requirements to run flask endpoint and SQAM.
RUN pip3 install -r ./requirements.txt
#Run the flask backend
CMD ["python3","backend.py"]