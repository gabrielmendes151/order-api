# Order API

This project is an order management API built with Spring Boot. It consumes messages from a Kafka topic, processes orders, and stores them in a PostgreSQL database. The application uses the `read_committed` isolation level, performs a pre-save operation for orders, and leverages PostgreSQL as the relational database.

---

## Table of Contents

- [Technologies Used](#technologies-used)
- [Application Overview](#application-overview)
- [Why Kafka?](#why-kafka)
- [Isolation Level `read_committed`](#isolation-level-read_committed)
- [Pre-Save Process](#pre-save-process)
- [Why PostgreSQL?](#why-postgresql)
- [Running the project](#running-the-project)
- [Available Endpoints](#available-endpoints)
- [Final Remarks](#final-remarks)
- [Key Highlights](#key-highlights)
- [Available Endpoints](#available-endpoints)
- [Consumer Examples Messages](#consumer-examples-messages)

---

## Technologies Used

- **Java 21**
- **Spring Boot**
- **Apache Kafka**
- **PostgreSQL**
- **Spring Data JPA**
- **Docker and Docker Compose**
- **Lombok**
- **Swagger OpenAPI**

---

## Running the project
After cloning the project, it is necessary to use Docker Compose to upload a local database. Use the following command
```
sudo docker compose up
```

## Swagger docs
http://localhost:8080/swagger-ui/index.html
---

## Application Overview

The application consists of the following main components:

- **OrderController**: Exposes REST endpoints for querying orders.
- **OrderService**: Contains the business logic for processing and persisting orders.
- **ConsumerService**: Responsible for consuming messages from the Kafka topic and initiating order processing.

---

## Why Kafka?

Apache Kafka is a distributed streaming platform used for real-time data processing. It is employed in this project for the following reasons:

- **Performance and Scalability**: Kafka handles high-throughput data efficiently, crucial for systems processing large numbers of orders concurrently.
- **Decoupling Systems**: It enables producers and consumers to operate independently, facilitating maintainability and scalability.
- **Resilience and Fault Tolerance**: Kafka is designed for high availability and ensures no data is lost.
- **Asynchronous Processing**: Kafka allows asynchronous order processing, improving system efficiency.

---

## Isolation Level `read_committed`

The `read_committed` isolation level is configured for the Kafka consumer. This ensures that only committed messages are read. The reasons for using this isolation level are:

- **Data Consistency**: Prevents reading uncommitted or rolled-back messages, ensuring only valid data is processed.
- **Integrity in Processing**: Guarantees that consumers do not process partially written or transient messages.
- **Prevents Dirty Reads**: Ensures the system avoids inconsistencies caused by reading uncommitted data.

---

## Pre-Save Process

The `preSave` method in the `OrderService` performs a pre-save operation for orders before full processing. This process is crucial for the following reasons:

- **Duplicate Order Prevention**: Checks for existing orders with the same `externalOrderId`, avoiding duplicate entries in the system.
- **Initial State Logging**: Saves the order with the status `RECEIVED`, indicating it has been received and is pending processing.
- **Error Tracking**: Facilitates monitoring from the moment the order is received, aiding auditing and debugging.
- **Error Handling**: If an error occurs during processing, the order is already recorded in the database, allowing its status to be updated to `ERROR`.

---

## Why PostgreSQL?

PostgreSQL was selected as the relational database for the following reasons:

- **Reliability and Stability**: It is a proven, robust database suitable for critical applications.
- **Advanced Features**: Supports a wide range of data types and extensions, offering flexibility for future needs.
- **ACID Compliance**: Ensures atomic, consistent, isolated, and durable transactions, crucial for data integrity.
- **Active Community**: Provides constant updates and a vibrant support network.

---

## Final Remarks

This project demonstrates a robust architecture for order processing using modern technologies and best practices. Kafka integration ensures scalability and resilience, while PostgreSQL provides reliable data storage.

## Key Highlights

- **Kafka as the Messaging System**: Enables asynchronous processing and decoupling between producers and consumers.
- **Isolation Level `read_committed`**: Ensures consistency and integrity of consumed data.
- **Order Pre-Save**: Prevents duplicates and simplifies error management.
- **PostgreSQL as the Database**: Delivers a reliable and robust environment for data storage.

The implementation follows **SOLID principles** and uses Spring Boot features to simplify development and maintenance. Additionally, transactional annotations (`@Transactional`) ensure consistency during database operations.


## Available Endpoints

### GET `/orders`

Fetches a paginated list of orders.

**Query Parameters:**

- `page` (optional): Page number (default: 0).
- `size` (optional): Page size (default: 10).

**Example:**

```
GET http://localhost:8080/orders?page=0&size=5
```

## Consumer Examples Messages
```
{
  "externalOrderId": "123e4567-e89b-12d3-a456-426614174000",
   "products": [
      {
        "description": "Product A",
        "value": 100.50
      },
      {
        "description": "Product B",
        "value": 200.75
      }
   ]
}
```
