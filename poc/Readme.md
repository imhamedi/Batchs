# Spring Batch POC

This project is a **Proof of Concept (POC)** designed to explore and test all features offered by **Spring Batch** through concrete examples. It includes a Java application built with Spring Boot and Spring Batch, along with Docker configurations for simplified execution.

---

## Features

- **Step Execution**: Demonstrates batch processing using Spring Batch.
- **Readers and Writers**: Processes data from input CSV files or DB to output CSV files or DB Table
- **Complete Examples**: Includes SQL scripts for the database and Spring Batch pipelines.
- **Docker Integration**: Containerized application with MySQL support.

---

## Prerequisites

Before getting started, make sure you have the following installed:

1. **Java** (JDK 17 or later): [Download JDK](https://adoptium.net/)
2. **Maven** (for building the `.jar` file): [Download Maven](https://maven.apache.org/download.cgi)
3. **Docker** and **Docker Compose**: [Install Docker](https://www.docker.com/products/docker-desktop)

---


---

## Installation and Usage

### 1. Clone the Project

Clone this repository to your local machine:
```bash
git clone https://github.com/imhamedi/Batchs.git
cd Batchs/poc

2. Generate the JAR File
The application must be compiled and packaged before it can be run using Docker. Use the following commands to generate the .jar file:

mvn clean package

The JAR file will be generated in the target folder with the following name: target/poc-0.0.1-SNAPSHOT.jar

3. Run the Application with Docker
The project comes with a docker-compose.yml file to start the following services:

A MySQL database
The Java application in a Docker container
Run the following command to start the application: docker-compose up --build

4. Test the Application

Once the containers are running:

Access the application at the following URL: http://localhost:8080/launchFirstJob/{id of the job} or  http://localhost:8080/launchJob/{id of the job} to test flows (you must change in the code to test all the flows)

5. Stop the Application
To stop and remove the containers:
docker-compose down

===================================================
For advanced flows please go to branch "advanced"

