# The seed from Chat GPT
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

#### 📝 Eduardo's note: 
Here had an issue whe executing mkdir, from sigle command had to do one per directory.
Also decided to change .property extension to .yml due more readability.

---

## 2. Product Model and Data Source

Define the data structure and create the initial data file.

| Step | Prompt |
| :--- | :--- |
| **2.1 Product Model** | `In the **com.example.productapi.model** package, create a Java class named **Product**. It must be a **Lombok-annotated record** with the following fields: **String id**, **String name**, **String imageUrl**, **String description**, **double price**, **int rating**, and **Map<String, String> specifications**.` |
| **2.2 Data File** | `Create a file named **products.json** in the **src/main/resources** directory. Populate it with an array containing at least **three example Product objects** that match the Java model, including unique IDs.` |

#### 📝 Eduardo's note: 
I actually used only this:

| Step | Prompt |
| :--- | :--- |
| **2.1 Product Model** | `In the **com.example.productapi.model** package, modify the Java class named **Product**. Remove the following fileds: stock and active. And add those fields: **String imageUrl**, **Integer rating**, and **Map<String, String> specifications**. Cascade all the modifications needed, like modifiying the products.json, the controller and so.` |

**During the PR review to merge all those changes into develop branch, the Cursor's bugBot detected a NullPointer risk in a Service logic. So that proceedwith the following prompt:**

`The **searchProducts** method's name filter can throw a **NullPointerException**. This occurs when a product has a null name, as the filter attempts to call **.toLowerCase()** on the null **product.getName()** value. As I was required to build the exception handling logic, do you reccomend to include a new class annotated with ControllerAdvice to handle it?`


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
| **3.3 Service Layer** | `In the **com.example.productapi.service** package, create a class named **ProductService**. It should be injected with **ProductRepository** and contain the following methods: **findAll()**, **findById(String id)**, **save(Product product)**, **update(String id, Product product)**, and **delete(String id)**. Implement the basic CRUD logic, delegating data access to the repository and handling basic business concerns like ID generation for new products.` |


#### 📝 Eduardo's note: 
I actually used only this:
| Step | Prompt |
| :--- | :--- |
| **3.1 Repository Interface and Service** | `Want to reduce coupling between the service and the JSON file that is acting as DB, so create an interface **ProductRepository** in **com.example.productapi.repository** with all the needed methods, then create a class **ProductRepositoryImpl** that implements **ProductRepository**. This implementation must **read the data from the products.json** file using **Jackson** so that all the data access logic is moved from the Service to the repository, and only business logic remains in the Service. Add the proper test classes.` |


---

## 4. Controller and Filtering Endpoint

Create the REST interface and implement the filtering capability.

### Controller Layer (REST Endpoints)

| Step | Prompt |
| :--- | :--- |
| **4.1 REST Controller** | `In the **com.example.productapi.controller** package, create a **ProductController** class. Use the **@RestController** and **@RequestMapping("/api/v1/products")** annotations. Inject **ProductService** and create REST endpoints for the full **CRUD** operations: **POST** (create), **GET /** (findAll), **GET /{id}** (findById), **PUT /{id}** (update), and **DELETE /{id}** (delete). Ensure proper HTTP status codes and exception handling (e.g., for not found).` |

### Filtering Implementation

| Step | Prompt |
| :--- | :--- |
| **4.2 Repository Filtering** | `Add a method to **ProductRepositoryImpl** named **findByProperties(String name, String description, Double minPrice, Integer minRating)**. Implement the logic to filter the in-memory product map based on the provided parameters (if a parameter is null or empty, it should be ignored).` |
| **4.3 Service Filtering** | `Add a corresponding method to **ProductService** named **findByProperties(String name, String description, Double minPrice, Integer minRating)** that calls the repository method.` |
| **4.4 Controller Filtering Endpoint** | `Add a **GET** endpoint to **ProductController** at **"/search"** that accepts **@RequestParam** for **name**, **description**, **minPrice**, and **minRating**, and calls the new service method to return the filtered list of products.` |

---

## 5. Documentation Files

Generate the two required documentation files.

| Step | Prompt |
| :--- | :--- |
| **5.1 README.md** | `Generate a **README.md** file for the 'product-api' project. It must include sections for: **API Overview**, **Endpoints** (list the main paths and methods), **Architectural Details** (mentioning the simple **Controller-Service-Repository** layers and JSON persistence), and **Setup Instructions** (detailing dependencies, Java version, and build steps).` |
| **5.2 run.md** | `Generate a separate file named **run.md**. This file must provide **detailed, step-by-step instructions** on how to **build** and **run** the Spring Boot application locally using **Maven** commands and accessing the Swagger UI at the default port (8080).` |

---

## 6. Spring Boot Actuator setup

Configure Actuator endpoints to expose health, info, metrics, and environment details, enhancing observability and monitoring capabilities.

| Step | Prompt |
| :--- | :--- |
| **6.1 Configure Actuator** | `Learn from all the previous failures, then make a plan divided in phases to implementing the actuator feature, like: fase 1 analize project structure, dependency tree, dependency compatibilities to choose the exact versions, fase 2 implement the built in actuator features only, no custom metrics at all, fase 3 implement only simple counting metrics in the service, no timmer, no JSON metrics. Moving from one phase to the next requires explicit approval from me, because I want to test each phase from my end.` |