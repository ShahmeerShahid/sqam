FROM python:3.9.2
#Setup working directory
WORKDIR /automarker/SQAM
#Copy all files into container
COPY . .
#upgrade pip
RUN pip3 install --upgrade pip
# Install the requirements to run SQAM.
RUN pip3 install -r requirements.txt
# Start Automarker
CMD ["python3", "-u", "main.py"]