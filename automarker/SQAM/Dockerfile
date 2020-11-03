# https://docs.docker.com/engine/reference/builder/

# Use an official Python runtime as a parent image
# See https://hub.docker.com/ and search for python:2.7-slim
FROM python:3

# Set the working directory to /app
# The WORKDIR instruction sets the working directory (inside the container) 
# for any RUN, CMD, ENTRYPOINT, COPY and ADD instructions that 
# follow it in the Dockerfile. 
WORKDIR /app

# Copy the current directory contents into the container at /app
ADD ./ /app

# Install any needed packages specified in requirements.txt
# RUN during image build
RUN pip install -r requirements.txt

# Make port 80 available to the world outside this container
EXPOSE 80

# Run python app.py when the container launches
# This happens if no command is specified
# CMD ["python", "app.py"]
