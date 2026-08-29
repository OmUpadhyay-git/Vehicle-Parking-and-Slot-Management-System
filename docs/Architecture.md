# System Architecture

# Vehicle Parking and Slot Management System

## 1. Architecture Overview

The Vehicle Parking and Slot Management System follows a simple **three-layer architecture**.

```text
┌─────────────────────────────────────────┐
│           PRESENTATION LAYER            │
│                                         │
│              JAVA SWING                 │
│                                         │
│  • Login                                │
│  • Dashboard                            │
│  • Vehicle Management                   │
│  • Slot Management                      │
│  • Vehicle Entry                        │
│  • Vehicle Exit                         │
│  • Payment                              │
└───────────────────┬─────────────────────┘
                    │
                    │ HTTP / REST API
                    ▼
┌─────────────────────────────────────────┐
│            APPLICATION LAYER            │
│                                         │
│           PYTHON + FASTAPI              │
│                                         │
│  • Authentication                       │
│  • Vehicle Management                   │
│  • Slot Management                      │
│  • Parking Logic                        │
│  • Fee Calculation                      │
│  • Payment Management                   │
│  • Dashboard Statistics                 │
└───────────────────┬─────────────────────┘
                    │
                    │ SQLAlchemy ORM
                    ▼
┌─────────────────────────────────────────┐
│               DATA LAYER                │
│                                         │
│                 MYSQL                   │
│                                         │
│  • Users                                │
│  • Vehicles                             │
│  • Parking Slots                        │
│  • Parking Records                      │
│  • Payments                             │
└─────────────────────────────────────────┘
```

The architecture separates the user interface, business logic, and database.

---

# 2. High-Level System Architecture

```text
                    USER
                      │
                      ▼
             ┌────────────────┐
             │   JAVA SWING   │
             │   APPLICATION  │
             └───────┬────────┘
                     │
                     │ REST API
                     │ JSON
                     ▼
             ┌────────────────┐
             │    FASTAPI     │
             │     BACKEND    │
             └───────┬────────┘
                     │
                     │ SQLAlchemy
                     ▼
             ┌────────────────┐
             │     MYSQL      │
             │    DATABASE    │
             └────────────────┘
```

### Communication Flow

```text
User
  ↓
Java Swing Interface
  ↓
HTTP Request
  ↓
FastAPI Backend
  ↓
Business Logic
  ↓
SQLAlchemy
  ↓
MySQL Database
  ↓
JSON Response
  ↓
Java Swing Interface
  ↓
User
```

---

# 3. Presentation Layer

The presentation layer is responsible for user interaction.

Technology:

```text
Java
Java Swing
```

The Java application will not contain parking business logic or directly access the database.

Its main responsibility is:

* Display screens.
* Take user input.
* Validate basic input.
* Send API requests.
* Receive API responses.
* Display results to the user.

## Main Screens

```text
Java Application
│
├── Login Screen
│
├── Dashboard
│
├── Vehicle Management
│
├── Parking Slot Management
│
├── Vehicle Entry
│
├── Vehicle Exit
│
├── Payment
│
└── Parking History
```

---

# 4. Java Frontend Architecture

```text
java-frontend/
│
├── Main.java
│
├── ui/
│   ├── LoginFrame.java
│   ├── DashboardFrame.java
│   ├── VehiclePanel.java
│   ├── SlotPanel.java
│   ├── ParkingEntryPanel.java
│   ├── ParkingExitPanel.java
│   ├── PaymentPanel.java
│   └── HistoryPanel.java
│
├── model/
│   ├── User.java
│   ├── Vehicle.java
│   ├── ParkingSlot.java
│   ├── ParkingRecord.java
│   └── Payment.java
│
└── service/
    └── ApiService.java
```

## Component Responsibilities

### `Main.java`

Starts the Java application.

### `ui/`

Contains all user interface components.

### `model/`

Contains Java objects representing backend data.

For example:

```text
Vehicle
ParkingSlot
ParkingRecord
Payment
```

### `ApiService.java`

Responsible for communicating with the FastAPI backend.

Example flow:

```text
Vehicle Entry Screen
        │
        ▼
   ApiService
        │
        ▼
POST /parking/entry
        │
        ▼
FastAPI Backend
```

---

# 5. Backend Architecture

The backend is responsible for:

* Authentication.
* Business rules.
* Vehicle management.
* Parking slot management.
* Parking entry.
* Parking exit.
* Parking fee calculation.
* Payment recording.
* Dashboard statistics.
* Database operations.

Technology:

```text
Python
FastAPI
SQLAlchemy
MySQL
```

---

# 6. Backend Folder Structure

```text
backend/
│
├── main.py
├── database.py
│
├── models/
│   ├── user.py
│   ├── vehicle.py
│   ├── slot.py
│   ├── parking.py
│   └── payment.py
│
├── schemas/
│   ├── user.py
│   ├── vehicle.py
│   ├── slot.py
│   ├── parking.py
│   └── payment.py
│
├── routes/
│   ├── auth.py
│   ├── vehicles.py
│   ├── slots.py
│   ├── parking.py
│   ├── payments.py
│   └── dashboard.py
│
└── services/
    ├── parking_service.py
    └── fee_service.py
```

This structure is intentionally small.

Do not create unnecessary folders such as:

```text
microservices/
repositories/
factories/
event_handlers/
message_brokers/
cache/
```

They are unnecessary for this project.

---

# 7. Backend Request Flow

Every request should follow this flow:

```text
Java UI
   │
   │ HTTP Request
   ▼
FastAPI Route
   │
   ▼
Service / Business Logic
   │
   ▼
SQLAlchemy Model
   │
   ▼
MySQL Database
   │
   ▼
Response
   │
   ▼
Java UI
```

Example:

```text
User clicks "Park Vehicle"
          │
          ▼
Java sends POST request
          │
          ▼
POST /parking/entry
          │
          ▼
FastAPI validates request
          │
          ▼
Parking Service checks:
• Vehicle exists
• Vehicle is not already parked
• Slot is available
• Slot supports vehicle type
          │
          ▼
Create Parking Record
          │
          ▼
Update Slot Status
          │
          ▼
Return JSON Response
          │
          ▼
Java displays confirmation
```

---

# 8. API Architecture

The system will use REST APIs.

## Authentication

```text
POST /login
```

Flow:

```text
Java Login Screen
        │
        ▼
POST /login
        │
        ▼
Validate Username and Password
        │
        ▼
Return User Information
```

---

## Vehicle APIs

```text
POST /vehicles
GET /vehicles
GET /vehicles/{id}
GET /vehicles/search/{vehicle_number}
```

Architecture:

```text
Vehicle UI
    ↓
Vehicle API
    ↓
Vehicle Service / Logic
    ↓
Vehicles Table
```

---

## Slot APIs

```text
POST /slots
GET /slots
GET /slots/available
GET /slots/occupied
PUT /slots/{id}
```

Architecture:

```text
Slot UI
   ↓
Slot API
   ↓
Slot Logic
   ↓
Parking Slots Table
```

---

## Parking APIs

```text
POST /parking/entry
POST /parking/exit
GET /parking/active
GET /parking/history
```

Architecture:

```text
Parking UI
     ↓
Parking API
     ↓
Parking Service
     ↓
┌───────────────────┐
│ Check Vehicle     │
│ Check Slot        │
│ Create Record     │
│ Update Slot       │
│ Calculate Fee     │
└───────────────────┘
     ↓
MySQL
```

---

## Payment APIs

```text
POST /payments
GET /payments
GET /payments/{id}
```

Architecture:

```text
Payment UI
    ↓
Payment API
    ↓
Payment Logic
    ↓
Payments Table
```

---

# 9. Database Architecture

The MySQL database contains five main tables.

```text
                ┌──────────────┐
                │    USERS     │
                └──────────────┘


┌──────────────┐
│   VEHICLES   │
└──────┬───────┘
       │
       │
       ▼
┌────────────────────┐
│  PARKING_RECORDS   │
└───────┬────────┬───┘
        │        │
        │        │
        ▼        ▼
┌─────────────┐ ┌──────────────┐
│ PARKING     │ │   PAYMENTS   │
│ SLOTS       │ └──────────────┘
└─────────────┘
```

## Relationship Summary

```text
Vehicle 1 ─────── N Parking Records

Parking Slot 1 ── N Parking Records

Parking Record 1 ─ 1 Payment
```

---

# 10. Database Access Architecture

The Java application must not directly connect to MySQL.

Correct architecture:

```text
Java
  │
  ▼
FastAPI
  │
  ▼
SQLAlchemy
  │
  ▼
MySQL
```

Incorrect architecture:

```text
Java ───────────► MySQL
 │
 └──────────────► FastAPI
```

The incorrect architecture creates duplicated database logic and makes the application harder to maintain.

---

# 11. Vehicle Entry Architecture

Vehicle entry is one of the main workflows.

```text
┌────────────────────┐
│ Vehicle Arrives    │
└─────────┬──────────┘
          ▼
┌────────────────────┐
│ Enter Vehicle      │
│ Number             │
└─────────┬──────────┘
          ▼
┌────────────────────┐
│ Find Vehicle       │
└─────────┬──────────┘
          │
     ┌────┴────┐
     │ Exists? │
     └────┬────┘
       Yes │ No
           │
           ▼
     Add Vehicle
           │
           ▼
┌────────────────────┐
│ Find Available     │
│ Compatible Slot    │
└─────────┬──────────┘
          ▼
┌────────────────────┐
│ Create Parking     │
│ Record             │
└─────────┬──────────┘
          ▼
┌────────────────────┐
│ Update Slot        │
│ → OCCUPIED         │
└─────────┬──────────┘
          ▼
┌────────────────────┐
│ Vehicle Parked     │
└────────────────────┘
```

---

# 12. Vehicle Exit Architecture

```text
┌────────────────────┐
│ Vehicle Exit       │
│ Request            │
└─────────┬──────────┘
          ▼
┌────────────────────┐
│ Find Active        │
│ Parking Record     │
└─────────┬──────────┘
          ▼
┌────────────────────┐
│ Record Exit Time   │
└─────────┬──────────┘
          ▼
┌────────────────────┐
│ Calculate Duration │
└─────────┬──────────┘
          ▼
┌────────────────────┐
│ Calculate Fee      │
└─────────┬──────────┘
          ▼
┌────────────────────┐
│ Record Payment     │
└─────────┬──────────┘
          ▼
┌────────────────────┐
│ Update Parking     │
│ Record             │
└─────────┬──────────┘
          ▼
┌────────────────────┐
│ Update Slot        │
│ → AVAILABLE        │
└─────────┬──────────┘
          ▼
┌────────────────────┐
│ Parking Completed  │
└────────────────────┘
```

---

# 13. Parking Fee Architecture

The parking fee calculation should remain inside the backend.

```text
Entry Time
     │
     ▼
Exit Time
     │
     ▼
Calculate Duration
     │
     ▼
Check Vehicle Type
     │
     ├───────────────┐
     ▼               ▼
   CAR              BIKE
     │               │
     ▼               ▼
Calculate Fee    Calculate Fee
     │               │
     └───────┬───────┘
             ▼
        Total Fee
```

Example pricing:

```text
Bike
First Hour = ₹20
Additional Hour = ₹10

Car
First Hour = ₹40
Additional Hour = ₹20
```

---

# 14. Slot Status Management

A parking slot can have the following states:

```text
AVAILABLE
OCCUPIED
```

State flow:

```text
AVAILABLE
    │
    │ Vehicle Entry
    ▼
OCCUPIED
    │
    │ Vehicle Exit
    ▼
AVAILABLE
```

The backend is responsible for changing the slot status.

The Java frontend should only request the action and display the result.

---

# 15. Authentication Architecture

The login process will follow this flow:

```text
User
  │
  ▼
Enter Username + Password
  │
  ▼
Java Login Screen
  │
  ▼
POST /login
  │
  ▼
FastAPI
  │
  ▼
Check Users Table
  │
  ├── Invalid → Return Error
  │
  └── Valid
         │
         ▼
    Return User Role
         │
         ▼
    Open Dashboard
```

User roles:

```text
ADMIN
STAFF
```

---

# 16. Error Handling Architecture

The backend should return simple and consistent responses.

Example success response:

```json
{
  "success": true,
  "message": "Vehicle parked successfully",
  "data": {
    "record_id": 101,
    "slot_number": "A5"
  }
}
```

Example error response:

```json
{
  "success": false,
  "message": "No compatible parking slot available"
}
```

Common errors:

* Invalid login.
* Vehicle not found.
* Vehicle already parked.
* Slot not available.
* Incompatible vehicle type.
* Active parking record not found.
* Payment failure.

---

# 17. Deployment Architecture

For the first version, deployment should remain simple.

```text
Local Computer
│
├── Java Swing Application
│
├── FastAPI Server
│   └── http://localhost:8000
│
└── MySQL Server
```

Development flow:

```text
Java Swing
    │
    │ localhost API
    ▼
FastAPI
    │
    ▼
MySQL
```

There is no need for cloud deployment, Docker, Kubernetes, or microservices in the MVP.

---

# 18. Final Architecture

The final architecture of the project is:

```text
                         USER
                           │
                           ▼
              ┌────────────────────────┐
              │     JAVA SWING UI      │
              │                        │
              │ Login                  │
              │ Dashboard              │
              │ Vehicles               │
              │ Slots                  │
              │ Parking Entry          │
              │ Parking Exit           │
              │ Payment                │
              │ History                │
              └───────────┬────────────┘
                          │
                          │ REST API / JSON
                          ▼
              ┌────────────────────────┐
              │    PYTHON BACKEND      │
              │       FASTAPI          │
              │                        │
              │ Authentication         │
              │ Vehicle Logic          │
              │ Slot Logic             │
              │ Parking Logic          │
              │ Fee Calculation        │
              │ Payment Logic          │
              │ Dashboard Statistics   │
              └───────────┬────────────┘
                          │
                          │ SQLAlchemy
                          ▼
              ┌────────────────────────┐
              │         MYSQL          │
              │                        │
              │ Users                  │
              │ Vehicles               │
              │ Parking Slots          │
              │ Parking Records        │
              │ Payments               │
              └────────────────────────┘
```

---

# 19. Architecture Principles

This project follows these principles:

1. **Keep the system simple.**
2. **Java handles the user interface.**
3. **Python handles business logic.**
4. **FastAPI exposes REST APIs.**
5. **MySQL stores persistent data.**
6. **Java does not directly access the database.**
7. **Business logic stays in the backend.**
8. **The frontend only handles presentation and user interaction.**
9. **Avoid unnecessary technologies and complexity.**

---

# 20. MVP Architecture Summary

The entire system can be summarized as:

```text
User
 ↓
Java Swing Application
 ↓
REST API
 ↓
Python FastAPI
 ↓
Business Logic
 ↓
SQLAlchemy
 ↓
MySQL
```

The most important business flow is:

```text
Vehicle
   ↓
Check Available Slot
   ↓
Assign Slot
   ↓
Create Parking Record
   ↓
Vehicle Exit
   ↓
Calculate Fee
   ↓
Payment
   ↓
Free Slot
```

This architecture is sufficient for the complete MVP. Do not add microservices, cloud infrastructure, AI, IoT, Docker, or other advanced technologies until the core system is fully working.
