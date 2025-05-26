##  Project: eCommerce Platform

This project represents the backend system of a modular and scalable online shop. The system is designed and implemented from scratch by myself, simulating a real-world scenario where a software company (me, as a developer) is contracted to deliver an eCommerce platform for a small shop that sells non-fragile, non-perishable items.

---

##  System Overview

The platform allows clients to browse products (called "things"), add them to a shopping basket, and place orders. These orders are fulfilled by a smart delivery system using one or more storage units (warehouses). The design follows Domain-Driven Design (DDD) patterns, ensuring business logic is cleanly separated and encapsulated.

---

###  Domain Structure

#### `Thing` – Products

Each product has a name, description, size, purchase price, and sales price. A product can exist in multiple storage units, each tracking its stock level.

#### `Client`

A client has a name, email, and home address (street, city, zip code). The email uniquely identifies the client—no username system is used.

#### `ShoppingBasket`

When a client wants to buy something, they add it to a shopping basket. This reserves the stock and prevents others from buying it. Each shopping basket contains parts (things and their quantities). There is no time limit on reservations. Clients can remove items or check out at any time.

#### `Order`

Upon checkout, a shopping basket becomes an order. Orders contain parts (product references and quantity) and are fulfilled via delivery packages.

#### `StorageUnit`

A warehouse that holds products. Initially, the system had a single storage unit. Later, I extended it to support multiple distributed units across Germany. Each unit tracks its own inventory of products (stock levels).

#### `DeliveryPackage`

When an order is placed, the system analyzes which storage units can fulfill the order. The system creates one or more delivery packages:

- Each delivery package comes from a single storage unit.
- The algorithm attempts to minimize the number of packages.
- If multiple units qualify, the closest (based on zip code) is selected.

---

## Database Schema

1. **Thing**:
    - ThingID (Primary Key)
    - Name
    - Description
    - Size
    - PurchasePrice
    - SalesPrice
    - StockQuantity

2. **Clients**:
    - ClientID (Primary Key)
    - Name
    - Email (Unique)
    - Address (Street, City, Zip Code)

3. **Orders**:
    - OrderID (Primary Key)
    - ClientID (Foreign Key)
    - Date
    - Status (e.g., pending, completed)

4. **OrderParts**:
    - OrderPartID (Primary Key)
    - OrderID (Foreign Key)
    - ProductID (Foreign Key)
    - Quantity

5. **StorageUnits**:
    - StorageUnitID (Primary Key)
    - Name
    - Address (Street, City, Zip Code)

6. **DeliveryPackages**:
    - DeliveryPackageID (Primary Key)
    - OrderID (Foreign Key)
    - StorageUnitID (Foreign Key)
    -
7. **DeliveryPackagePart**:
    - DeliveryPackagePartID (Primary Key)
    - OrderID (Foreign Key)
    - ThingID (Foreign Key)



###  Business Rules

- **Stock availability**: Products can only be purchased if their stock > 0.
- **Reservation**: Items in shopping baskets are reserved and hidden from others.
- **No returns**: Returns are handled outside the system (via email).
- **Manual stock control**: Admins can add/remove/update products and their stock manually.
- **Delivery logic**: Delivery packages must be optimized for cost and proximity.

---

##  System Architecture

- ![System Architeture](images/uml_structure.bmp)

###  Example Flow

1. A client registers with name, email, and address.
2. They search for products and add items to their shopping basket.
3. The stock for selected items is reserved.
4. When they check out, an order is created.
5. The system determines the best warehouses to fulfill the order and creates delivery packages.
6. The order and package details are stored and retrievable via the REST API.

---

###  Development Principles

-  Domain-Driven Design (DDD)
-  Clean Code practices
-  SOLID principles (SRP, OCP, DIP)
-  Layered architecture (domain, application, infrastructure, interface)
-  Full unit and integration testing
-  RESTful API design

---

##  REST API Overview

###  Shopping System

| Endpoint                                        | Method | Description                          |
| ----------------------------------------------- | ------ | ------------------------------------ |
| `/clients?email=...`                            | GET    | Fetch client by email                |
| `/shoppingBaskets?clientId=...`                 | GET    | Get shopping basket for a client     |
| `/shoppingBaskets/{basket-id}/parts`            | POST   | Add a thing to shopping basket       |
| `/shoppingBaskets/{basket-id}/parts/{thing-id}` | DELETE | Remove thing from basket             |
| `/shoppingBaskets/{basket-id}/checkout`         | POST   | Checkout the basket and create order |
| `/orders?clientId=...`                          | GET    | Get all orders of client             |
| `/orders?clientId=...&filter=latest`            | GET    | Get latest order of client           |
| `/deliveryPackages?orderId=...`                 | GET    | Get delivery packages for an order   |

###  Catalog & Stock Management

| Endpoint                                         | Method | Description                                          |
| ------------------------------------------------ | ------ | ---------------------------------------------------- |
| `/things?name=...`                               | GET    | Search things by name                                |
| `/things/{thing-id}`                             | GET    | Get thing by ID                                      |
| `/things/{thing-id}`                             | PATCH  | Change sales price                                   |
| `/storageUnits/{unit-id}/stockLevels/{thing-id}` | PATCH  | Change stock level for a thing                       |
| `/stockLevels?thingId=...`                       | GET    | Get stock levels of a thing across all storage units |
| `/storageUnits/{unit-id}`                        | PATCH  | Change storage unit name                             |

###  Client Management

| Endpoint               | Method | Description           |
| ---------------------- | ------ | --------------------- |
| `/clients/{client-id}` | PATCH  | Update client address |
| `/clients/{client-id}` | DELETE | Delete client data    |

---

##  Tech Stack

- Java 21 • Spring Boot • JPA (Hibernate)
- REST (JSON over HTTP)
- Gradle
- JUnit 5 • Mockito
- H2
- Postman • IntelliJ

---

##  Entity Relationships

- `Client` → owns → `ShoppingBasket`
- `ShoppingBasket` → contains → `ShoppingBasketParts`
- `ShoppingBasket` → becomes → `Order`
- `Order` → fulfilled by → `DeliveryPackages`
- `DeliveryPackage` → sent from → `StorageUnit`
- `StorageUnit` → contains → `StockLevels`
- `StockLevel` → tracks → `Thing`
- `OrderPart`, `DeliveryPackagePart` → reference → `Thing`

---

##  Scalability & Extensibility

This project is highly modular and can be extended with:

- User roles (admin, customer)
- Payment systems
- Return processing
- Notification systems

It is also ready for containerization using Docker and Kubernetes.

---

##  Contact

- ✉ Email: abheidari99@gmail.com

---

© 2025 – All rights reserved by Abolfazl Heidari
