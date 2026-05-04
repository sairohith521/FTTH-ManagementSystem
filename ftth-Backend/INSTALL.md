# Aaha Telecom – Backend (Spring Boot)

This folder contains the Spring Boot backend for the FTTH Management System.

---

## ✅ Prerequisites

Install the following before starting:

- Java JDK 17 or higher
- Maven
- MySQL Server
- Git

Verify installation:

```bash
java -version
mvn -version
mysql --version

✅ Step 1: Clone Repository

git clone https://github.com/sairohith521/FTTHProject.git
cd FTTHProject/ftth-backend

✅ Step 2: Open CMD / Git Bash and run:
mysql -u root -p

👉 Then you’ll see:

mysql>

(optional)  ✅ Step 2: if using  MySQL Shell
                 ✅ Step 1: Connect
                           \connect root@localhost
                 ✅ Step 2: Switch to SQL mode
                           \sql
                 👉 Now you’ll see:
                 MySQL SQL >

✅ Step 3: Create Database
CREATE DATABASE testdb;
USE testdb;

✅ Step 4: Run Database Schema
SOURCE sql/schema.sql;
#This creates all required tables.

✅ Step 5: Run Seed Data
SOURCE sql/seed.sql;

✅ Step 6: Configure Database Connection
Open:
src/main/resources/application.properties

spring.datasource.url=jdbc:mysql://localhost:3306/testdb
spring.datasource.username=root
spring.datasource.password=YOUR_PASSWORD

✅ Step 7: Build Backend
mvn clean install

✅ Step 8: Run Backend Server
Shellmvn spring-boot:run

#Successful startup message:
#Tomcat started on port(s): 8080
#Started ApiApplication


✅ Backend URL
http://localhost:8080