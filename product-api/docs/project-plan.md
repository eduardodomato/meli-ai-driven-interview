# **📦 Product Management API \- Project Plan**

This document outlines the initial feature backlog for the product-api project, a RESTful service built with Java 21 and Spring Boot 3\. The project adopts a simple Layered Architecture (Controller-Service-Repository) and uses a static JSON file for persistence.

## **🎯 Project Goals**

* Establish a Java 21/Spring Boot 3 application using Maven.  
* Implement basic CRUD operations for a Product resource.  
* Provide filtering capabilities on product properties.  
* Ensure the API is documented using Swagger/OpenAPI.

## **I. 🛠️ Foundation and Configuration**

This section focuses on setting up the necessary technologies and domain objects.

### **Feature 1: Project Setup and Dependencies**

| Item | Description |
| :---- | :---- |
| **User Story** | As a **Developer**, I want to set up the **Java 21/Spring Boot 3/Maven** project structure, including key dependencies (**Web, Lombok, Springdoc-OpenAPI UI**), so that I have a runnable project foundation ready for development. |
| **Dependencies** | Spring Web, Springdoc-OpenAPI UI, Lombok. |

### **Feature 2: Product Data Model Definition**

| Item | Description |
| :---- | :---- |
| **User Story** | As a **Domain Expert**, I want to define the Product data model as a **Lombok-annotated record** with fields (id, name, imageUrl, description, price, rating, and specifications), so that the application has a strongly typed and immutable domain object. |
| **Model** | com.example.productapi.model.Product (Record) |

### **Feature 3: Initial Data Source Creation**

| Item | Description |
| :---- | :---- |
| **User Story** | As a **Developer**, I want to create a **products.json file** with at least three valid mock products in the classpath, so that the application has initial data to work with without needing a database setup. |
| **File** | src/main/resources/products.json |

## **II. 🏛️ Core Architecture (C-S-R)**

This section implements the layered structure, utilizing in-memory JSON data.

### **Feature 4: Repository Interface and JSON Loading Implementation**

| Item | Description |
| :---- | :---- |
| **User Story** | As a **Persistence Layer**, I want a **ProductRepository** implementation that loads and manages product data from the products.json file **in-memory** on startup, so that the Service layer can perform data operations (CRUD). |
| **Implementation** | ProductRepositoryImpl must use a ConcurrentHashMap and implement findAll(), findById(), save(), update(), and delete(). |

### **Feature 5: Service Layer (Business Logic) Implementation**

| Item | Description |
| :---- | :---- |
| **User Story** | As a **Business Logic Layer**, I want a **Product Service** to handle product creation, retrieval, updates, and deletion, so that the Controller layer remains clean and focused solely on HTTP handling. |
| **Logic** | Delegate CRUD calls to the Repository and include basic ID generation logic for new products. |

## **III. 🌐 API Functionality**

This section focuses on exposing the business logic via REST endpoints.

### **Feature 6: Standard CRUD Endpoints**

| Item | Description |
| :---- | :---- |
| **User Story** | As an **API User**, I want REST endpoints for **POST, GET, PUT, and DELETE** on the /api/v1/products path, so that I can fully manage the product lifecycle (CRUD) via standard HTTP verbs. |
| **Controller** | ProductController |
| **Status Codes** | Must return appropriate codes (200, 201, 204, 404). |

### **Feature 7: Product Filtering Endpoint**

| Item | Description |
| :---- | :---- |
| **User Story** | As an **API User**, I want a **GET /api/v1/products/search** endpoint that accepts optional query parameters (name, description, minPrice, minRating), so that I can efficiently filter and find specific products based on multiple criteria. |
| **Filtering Logic** | The implementation must gracefully handle missing (null) request parameters. |

## **IV. 📝 Deliverables and Documentation**

This final section ensures the project is deployable and understandable.

### **Feature 8: API Documentation (Swagger)**

| Item | Description |
| :---- | :---- |
| **User Story** | As an **API Consumer**, I want the service to expose an OpenAPI interface via **Swagger UI**, so that I can easily discover all available endpoints, required schemas, and test the API directly from the browser. |
| **Access** | Should be accessible at http://localhost:8080/swagger-ui.html (default configuration). |

### **Feature 9: Project Documentation Files**

| Item | Description |
| :---- | :---- |
| **User Story** | As a **New Developer/Operator**, I want comprehensive **README.md and run.md** files, so that I can quickly understand the API's architecture and execute the project with detailed, step-by-step instructions. |
| **Files** | README.md (Architecture, Endpoints), run.md (Build and Run commands). |

