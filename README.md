# StockPilot - Warehouse & Order Management System

## 📌 Project Overview
StockPilot is a robust, console-based (CLI) Java Standard Edition (SE) application designed for managing warehouse inventory, customers, and order processing.

## 🚀 Technologies Used
* **Language:** Java 21
* **Build Tool:** Apache Maven
* **Database:** H2 Database
* **Testing:** JUnit 5 (Jupiter)
* **Core Concepts:** Stream API, File I/O, Multithreading (concepts), JDBC Transactions, Regex Validation.

## 🏗️ Architecture
The system strictly adheres to a clean Layered Architecture:
* **`model/`**: Contains Entity classes (`Product`, `Customer`, `Order`, `OrderItem`) with strict Encapsulation and Regex validation.
* **`repository/`**: The Data Access Layer. This is the *only* layer permitted to interact with the `java.sql.*` package (PreparedStatement, ResultSet) to prevent SQL injection.
* **`service/`**: Contains core business logic (discount calculations, analytics) decoupled from database operations.
* **`io/`**: Handles File I/O operations such as CSV imports.
* **`Main.java`**: The entry point providing the CLI menu loop.

## ✨ Key Features Completed
* **F1 & F2: Product & Customer Management:** Full CRUD operations using Generic Repository Interfaces. Includes strict Regex validation for SKUs, Emails, and Phone numbers.
* **F3: Order Processing (Checkout):** Implements JDBC Transactions. The process of saving the order, inserting order items, and deducting inventory stock is wrapped in an atomic transaction (Commit/Rollback) to prevent data inconsistency or overselling. Applies Polymorphism for discount policies.
* **F4: Analytics & Reporting:** Utilizes Java 8 **Stream API** (`filter`, `map`, `reduce`, `groupingBy`) to efficiently calculate total revenue and detect low-stock items without relying on traditional manual loops.
* **F5: CSV Data Import:** Reads and processes initial product catalogs from `.csv` files using `BufferedReader` and `try-with-resources` for safe memory management.
* **Unit Testing:** Includes 8 comprehensive test cases using JUnit 5, testing both positive outcomes and expected exceptions (`assertThrows`) in the Service layer.

## 🛠️ How to Build and Run
**Prerequisites:** Ensure you have JDK 21+ and Maven installed on your machine.

1. **Clone the repository and navigate to the project directory.**
2. **Build the executable JAR file:**
   ```bash
   mvn clean package