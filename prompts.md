## The seed from Chat GPT
I'm starting a new project using Cursor in order to helping me on speeding up the development process, so that I'll need some useful prompts that you will provide. I need the very firsts steps to generate a Java project (version 21), using a compatible Spring Boot version and built with maven. It is a restFull api that exposes endpoints to handle Products (initial fields are name, image URL, description,  price, rating and specifications). Start with the minimum CRUD endpoints plus findAll, and a version to filtering by the given properties. Need to include the basic Swagger feature. As this will be locally run instead of using a database it will use a JSON file in the classpath to store the Products. To avoid complexity it is required to use a simple Layer architecture at the beginning. Also need to get a Readme.md file that explains the API details, endpoints, architectural details and the setup instructions. In a separate file called run.md give more detail instructions for running the program.

## Output:

# 🚀 Cursor Prompt Plan: Spring Boot 3 / Java 21 Product API

This guide provides a sequence of prompts to use within **Cursor** to generate a RESTful API project for managing `Product` data. The project uses **Java 21**, **Spring Boot 3.x**, **Maven**, in-memory JSON persistence, and includes **Swagger/OpenAPI** documentation.

---

## 1. Project Generation and Initial Setup

Start with a single comprehensive prompt to lay the foundation and configure the build system.

| Step | Prompt |
| :--- | :--- |
| **1.1 Initial Setup** | `Generate a Maven project structure for a **Java 21** Spring Boot 3.x application. Name the project 'product-api'. The application should be a **RESTful API** for managing 'Product' entities. Include the following dependencies: **Spring Web**, **Lombok**, and **Springdoc-OpenAPI UI** (for Swagger). The persistence layer will use a **local JSON file** (in the classpath) instead of a database for initial development. Set the application's package structure to use **com.example.productapi** as the base package.` |

---

## 2. Product Model and Data Source

Define the data structure and create the initial data file.

| Step | Prompt |
| :--- | :--- |
| **2.1 Product Model** | `In the **com.example.productapi.model** package, create a Java class named **Product**. It must be a **Lombok-annotated record** with the following fields: **String id**, **String name**, **String imageUrl**, **String description**, **double price**, **int rating**, and **Map<String, String> specifications**.` |
| **2.2 Data File** | `Create a file named **products.json** in the **src/main/resources** directory. Populate it with an array containing at least **three example Product objects** that match the Java model, including unique IDs.` |

---

## 3. Layered Architecture Implementation (Repository & Service)

Implement the data access and business logic layers.

### Repository Layer (Data Access)

| Step | Prompt |
| :--- | :--- |
| **3.1 Repository Interface** | `Create an interface **ProductRepository** in **com.example.productapi.repository** with methods for **findAll()**, **findById(String id)**, **save(Product product)**, **update(String id, Product product)**, and **delete(String id)**.` |
| **3.2 Repository Implementation** | `Create a class **ProductRepositoryImpl** that implements **ProductRepository**. This implementation must **read the data from the products.json** file (using **Jackson**) on initialization and store it in a local **ConcurrentHashMap<String, Product>**. Implement all required CRUD methods (`findAll`, `findById`, `save`, `update`, `delete`), operating on the in-memory map.` |

### Service Layer (Business Logic)

| Step | Prompt |
| :--- | :--- |
| **3.3 Service Layer** | `In the **com.example.productapi.service** package, create a class named **ProductService**. It should be injected with **ProductRepository** and contain the following methods: **findAll()**, **findById(String id)**, **save(Product product)**, **update(String id, Product product)**, and **delete(String id