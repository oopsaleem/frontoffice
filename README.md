# Frontoffice Application

This document provides instructions for building, running, and generating documentation for the Frontoffice application.

## Building the Application

To start the application run below command: 

```bash
mvn spring-boot:run
```

## Running the Application with Docker Compose

To run the application using Docker Compose, use the following command:

```bash
docker compose up -d
```

This command will: Create and start the containers defined in the `docker-compose.yml` file. Both will run in the background.

## Generating Site Documentation

To generate the site documentation for the application, use the following command:

```bash
mvn site
```

This command will generate a website with project information, reports, and documentation. The generated site can be found in the `target/site` directory.
