<<<<<<< HEAD
DevProfile Backend — Setup Guide

Requirements

1- Before running the project, install the following tools:

Git

Java 21

Docker Desktop

Maven

**THESE ARE A MUST TO INSTALL TOOLS**

2- Clone the repository

git clone https://github.com/iMedoV8/DevProfileProject.git

cd DevProfileProject

3- Start the database

The project uses PostgreSQL running inside Docker.

Run:

docker compose up -d

This will start the PostgreSQL container.

You can verify it is running with:

docker ps

You should see a container similar to:

devprofileproject-db
postgres:16

4- Run the backend

Start the Spring Boot application:

mvn spring-boot:run

or using the Maven wrapper:

./mvnw spring-boot:run

5- Access the API

Once the server starts, the backend runs at:

http://localhost:8086
=======
# DevProfile-Backend
>>>>>>> 826ef6d881f793cda1da033a4dc56138c190dae7
