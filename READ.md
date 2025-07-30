# Item Microservice API

## Description

Item Microservice API is a Spring Boot web application for managing users and items. It supports user registration, JWT-based authentication, and creation and getting list of owned items.

## Technologies

- Java 17+
- Spring Boot
- Spring Security
- io.jsonwebtoken
- Spring Data JPA / Hibernate
- MySQL (Docker)
- Maven

## Requirements

- Java 17+
- Maven
- Docker + Docker Compose

## Getting Started

### 1. Clone the repository

```bash
git clone 
cd 
```

### 2. Create a `.env` file

Inside the `docker` folder, create a `.env` file with the following environment variables:

```
MYSQL_ROOT_PASSWORD=your_root_password
MYSQL_DATABASE=your_database_name
```

Example:

```
MYSQL_ROOT_PASSWORD=root123
MYSQL_DATABASE=taskdb
```

### 3. Start the MySQL container using Docker

Navigate to the `docker` folder and run:

```bash
docker-compose up -d
```

### 4. Run the application

From the root of the project, run:

```bash
./mvnw spring-boot:run
```

Or if you have Maven installed globally:

```bash
mvn spring-boot:run
```

## API – Endpoints

| Method | Endpoint | Description                                              | Requires JWT |
|--------|----------|----------------------------------------------------------|--------------|
| POST   | `/register` | Register a new user                                      | No           |
| POST   | `/login` | Authenticate and receive a JWT                           | No           |
| GET    | `/items` | Get a list of items owned by user authenticated with JWT | Yes          |
| POST   | `/items` | Add a new item                                           | Yes          |

## Running Tests

To run unit tests:

```bash
mvn test
```

## Author
Kacper Knuth \
This project was created as part of a recruitment process for Betacom S.A.
