# Library Management System

A comprehensive Java Spring Boot application that provides a REST API and a web interface for managing a library's book collection.

---

## ✨ Core Features

* **Complete Book Management:** Full CRUD (Create, Read, Update, Delete) functionality for books.
* **Secure Access:** Robust user registration and login system.
* **Dual Interface:** Offers both a RESTful API for programmatic access and a user-friendly web interface.
* **Enhanced Security:** Protected by Spring Security, featuring password encryption with BCrypt and CSRF protection.
* **Standardized Error Handling:** Centralized and consistent JSON-formatted error responses.

---

## 📚 Technology Stack

* Java
* Spring Boot
* **Spring Data JPA:** For data persistence.
* **Spring Security:** For authentication and authorization.
* **Thymeleaf:** As the server-side Java template engine for the web interface.
* **Lombok:** To reduce boilerplate code.
* **Maven / Gradle:** For project build and dependency management.

---

## 🚀 Getting Started

### Prerequisites

* Java Development Kit (JDK)
* Maven or Gradle
* PostgreSQL database

### 1. Clone the Repository

```bash
git clone https://github.com/ridvansevik/spring-library-system.git
cd spring-library-system
```

### 2. Database Setup

#### Create the Database

Create a new PostgreSQL database named `library_db`:

```bash
createdb library_db
```

#### Configure the Connection

Edit `src/main/resources/application.properties`:

```properties
# Project name
spring.application.name=library-app

# PostgreSQL connection
spring.datasource.url=jdbc:postgresql://localhost:5432/library_db
spring.datasource.username=<your_username>
spring.datasource.password=<your_password>

# JPA and Hibernate settings
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=update
```

### 3. Build the Project

With Maven:

```bash
mvn clean install
```

With Gradle:

```bash
gradle clean build
```

### 4. Run the Application

```bash
java -jar target/library-app-0.0.1-SNAPSHOT.jar
```

---

## 🏠 Accessing the Application

* **Web Interface:** [http://localhost:8080/web/books](http://localhost:8080/web/books)
* **API Root:** [http://localhost:8080/api/books](http://localhost:8080/api/books)

---

## 📄 Default Administrator Account

* **Username:** `admin`
* **Password:** `VerySecretPassword`

---

## 🔍 API Endpoints

| Method | Endpoint           | Description                    | Authentication |
| ------ | ------------------ | ------------------------------ | -------------- |
| POST   | /api/auth/register | Registers a new user.          | Not Required   |
| GET    | /api/books         | Retrieves a list of all books. | Not Required   |
| GET    | /api/books/{id}    | Fetches book details by ID.    | Not Required   |
| POST   | /api/books         | Adds a new book.               | Required       |
| PUT    | /api/books/{id}    | Updates existing book info.    | Required       |
| DELETE | /api/books/{id}    | Removes a book.                | Required       |

---

## 📃 License

This project is licensed under the **MIT License**.
