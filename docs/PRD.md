# Product Requirements Document (PRD)

# Vehicle Parking and Slot Management System

## 1. Project Overview

### Project Name

**Vehicle Parking and Slot Management System**

### Project Type

Desktop-based parking management application with a Java frontend, Python backend, and MySQL database.

### Purpose

The Vehicle Parking and Slot Management System is designed to manage vehicle entry, parking slot allocation, vehicle exit, parking fees, and payment records.

The system will replace manual parking management with a digital system that allows staff or administrators to easily track available and occupied parking slots.

---

# 2. Problem Statement

Manual parking management creates several problems:

* Difficulty tracking available parking slots.
* Manual errors while assigning parking spaces.
* Difficulty maintaining vehicle entry and exit records.
* Incorrect parking fee calculations.
* Difficulty tracking parking history and revenue.

This system will provide a centralized solution to manage these operations.

---

# 3. Project Goal

The main goal is to develop a simple and functional parking management system where users can:

1. Manage vehicles.
2. Manage parking slots.
3. Assign available slots to vehicles.
4. Record vehicle entry and exit.
5. Calculate parking fees.
6. Record payments.
7. View basic parking statistics .

---

# 4. Target Users

The system will have two types of users.

## 4.1 Admin

The admin can:

* Login to the system.
* Manage parking slots.
* Add and manage vehicles.
* View active parking records.
* View parking history.
* View payment records.
* View dashboard statistics.

## 4.2 Staff

The staff can:

* Login to the system.
* Register vehicle entry.
* Assign parking slots.
* Process vehicle exit.
* Generate parking fees.
* Record payments.

---

# 5. Technology Stack

| Layer             | Technology     |
| ----------------- | -------------- |
| Frontend          | Java           |
| UI Framework      | Java Swing     |
| Backend           | Python         |
| Backend Framework | FastAPI        |
| Database          | MySQL          |
| ORM               | SQLAlchemy     |
| API Communication | REST API       |
| API Testing       | Postman        |
| Version Control   | Git and GitHub |

---

# 6. System Architecture

The application will follow a simple three-layer architecture.

```text
┌──────────────────────────────┐
│          JAVA UI             │
│                              │
│  Login                       │
│  Dashboard                   │
│  Vehicle Management          │
│  Slot Management             │
│  Parking Entry and Exit      │
│  Payment                     │
└───────────────┬──────────────┘
                │
                │ HTTP / REST API
                ▼
┌──────────────────────────────┐
│       PYTHON BACKEND         │
│          FastAPI             │
│                              │
│  Authentication              │
│  Vehicle Management          │
│  Slot Management             │
│  Parking Management          │
│  Fee Calculation             │
│  Payment Management          │
└───────────────┬──────────────┘
                │
                │ SQLAlchemy
                ▼
┌──────────────────────────────┐
│            MYSQL             │
│                              │
│  Users                       │
│  Vehicles                    │
│  Parking Slots               │
│  Parking Records             │
│  Payments                    │
└──────────────────────────────┘
```

The Java application will communicate with the Python backend through REST APIs.

The Python backend will handle the business logic and database operations.

---

# 7. Core Features

## 7.1 Authentication

Users must log in before accessing the system.

Features:

* Username and password login.
* Admin and Staff roles.
* Basic role-based access.

---

## 7.2 Dashboard

The dashboard will display:

* Total parking slots.
* Available slots.
* Occupied slots.
* Number of currently parked vehicles.
* Today's total revenue.

Example:

```text
Total Slots:       50
Available Slots:   20
Occupied Slots:    30
Active Vehicles:   30
Today's Revenue:   ₹2,500
```

---

## 7.3 Vehicle Management

The system should allow users to:

* Add a vehicle.
* Search for a vehicle.
* View vehicle details.

Vehicle information:

* Vehicle number.
* Vehicle type.
* Owner name.
* Owner phone number.

Supported vehicle types:

* Car.
* Bike.

---

## 7.4 Parking Slot Management

The system should allow the admin to:

* Add parking slots.
* View all slots.
* View available slots.
* View occupied slots.

Each slot will contain:

* Slot number.
* Vehicle type.
* Slot status.

Slot statuses:

```text
AVAILABLE
OCCUPIED
```

Example:

```text
A1 - Car - AVAILABLE
A2 - Car - OCCUPIED
B1 - Bike - AVAILABLE
```

---

## 7.5 Vehicle Entry

When a vehicle enters the parking area:

1. Enter the vehicle number.
2. Check whether the vehicle already exists.
3. Select or assign an available compatible slot.
4. Create a parking record.
5. Save the entry time.
6. Change the slot status to `OCCUPIED`.

The system should not allow an already occupied slot to be assigned.

---

## 7.6 Vehicle Exit

When a vehicle exits:

1. Search for the active parking record.
2. Record the exit time.
3. Calculate the parking duration.
4. Calculate the parking fee.
5. Process payment.
6. Mark the parking record as completed.
7. Change the parking slot status to `AVAILABLE`.

---

## 7.7 Parking Fee Calculation

The initial parking rates will be simple.

### Bike

```text
First Hour: ₹20
Additional Hour: ₹10
```

### Car

```text
First Hour: ₹40
Additional Hour: ₹20
```

The fee is calculated based on the parking duration.

```text
Duration = Exit Time - Entry Time

Total Fee =
Base Fee + Additional Hour Charges
```

The exact pricing can be changed later if required.

---

## 7.8 Payment Management

After calculating the parking fee, the system should record the payment.

Supported payment methods:

* Cash.
* UPI.
* Card.

Payment information:

* Payment amount.
* Payment method.
* Payment time.
* Payment status.

Payment statuses:

```text
PAID
PENDING
```

---

## 7.9 Parking History

The system should maintain records of completed parking sessions.

Users should be able to view:

* Vehicle number.
* Slot number.
* Entry time.
* Exit time.
* Parking duration.
* Parking fee.
* Payment status.

---

# 8. Database Design

The system will use five main tables.

---

## 8.1 Users Table

Table name:

```text
users
```

| Column   | Type    | Description     |
| -------- | ------- | --------------- |
| user_id  | INT     | Primary Key     |
| name     | VARCHAR | User name       |
| username | VARCHAR | Unique username |
| password | VARCHAR | User password   |
| role     | VARCHAR | ADMIN or STAFF  |

---

## 8.2 Vehicles Table

Table name:

```text
vehicles
```

| Column         | Type    | Description                |
| -------------- | ------- | -------------------------- |
| vehicle_id     | INT     | Primary Key                |
| vehicle_number | VARCHAR | Unique registration number |
| vehicle_type   | VARCHAR | Car or Bike                |
| owner_name     | VARCHAR | Vehicle owner name         |
| owner_phone    | VARCHAR | Owner phone number         |

---

## 8.3 Parking Slots Table

Table name:

```text
parking_slots
```

| Column       | Type    | Description           |
| ------------ | ------- | --------------------- |
| slot_id      | INT     | Primary Key           |
| slot_number  | VARCHAR | Unique slot number    |
| vehicle_type | VARCHAR | Car or Bike           |
| status       | VARCHAR | AVAILABLE or OCCUPIED |

---

## 8.4 Parking Records Table

Table name:

```text
parking_records
```

| Column     | Type     | Description         |
| ---------- | -------- | ------------------- |
| record_id  | INT      | Primary Key         |
| vehicle_id | INT      | Foreign Key         |
| slot_id    | INT      | Foreign Key         |
| entry_time | DATETIME | Vehicle entry time  |
| exit_time  | DATETIME | Vehicle exit time   |
| duration   | INT      | Duration in minutes |
| fee        | DECIMAL  | Total parking fee   |
| status     | VARCHAR  | PARKED or COMPLETED |

---

## 8.5 Payments Table

Table name:

```text
payments
```

| Column         | Type     | Description           |
| -------------- | -------- | --------------------- |
| payment_id     | INT      | Primary Key           |
| record_id      | INT      | Foreign Key           |
| amount         | DECIMAL  | Payment amount        |
| payment_method | VARCHAR  | Cash, UPI, or Card    |
| payment_time   | DATETIME | Payment date and time |
| status         | VARCHAR  | PAID or PENDING       |

---

# 9. Database Relationships

```text
VEHICLES
    │
    │ 1
    │
    │ N
PARKING_RECORDS
    │
    ├───────────────┐
    │               │
    │ N             │ 1
    │               │
PARKING_SLOTS    PAYMENTS
```

Relationship details:

* One vehicle can have multiple parking records over time.
* One parking slot can be used in multiple parking records over time.
* One parking record can have one payment.

---

# 10. API Requirements

## Authentication

```text
POST /login
```

---

## Vehicle APIs

```text
POST /vehicles
GET /vehicles
GET /vehicles/{id}
GET /vehicles/search/{vehicle_number}
```

---

## Parking Slot APIs

```text
POST /slots
GET /slots
GET /slots/available
GET /slots/occupied
PUT /slots/{id}
```

---

## Parking APIs

```text
POST /parking/entry
POST /parking/exit
GET /parking/active
GET /parking/history
```

---

## Payment APIs

```text
POST /payments
GET /payments
GET /payments/{id}
```

---

## Dashboard API

```text
GET /dashboard
```

Example response:

```json
{
  "total_slots": 50,
  "available_slots": 20,
  "occupied_slots": 30,
  "active_vehicles": 30,
  "today_revenue": 2500
}
```

---

# 11. Backend Structure

The Python backend should use the following simple structure.

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
├── routes/
│   ├── auth.py
│   ├── vehicles.py
│   ├── slots.py
│   ├── parking.py
│   ├── payments.py
│   └── dashboard.py
│
└── services/
    └── parking_service.py
```

The backend structure should remain simple and easy to maintain.

---

# 12. Java Frontend Structure

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
│   ├── ParkingPanel.java
│   └── PaymentPanel.java
│
├── model/
│   ├── Vehicle.java
│   ├── ParkingSlot.java
│   ├── ParkingRecord.java
│   └── Payment.java
│
└── service/
    └── ApiService.java
```

The `ApiService` will send HTTP requests to the FastAPI backend.

The Java application will not directly communicate with MySQL.

```text
Java Application
        ↓
REST API
        ↓
FastAPI Backend
        ↓
MySQL Database
```

---

# 13. Main User Flow

## Vehicle Entry Flow

```text
Vehicle Arrives
       ↓
Enter Vehicle Number
       ↓
Check Vehicle Information
       ↓
Find Available Compatible Slot
       ↓
Assign Parking Slot
       ↓
Create Parking Record
       ↓
Save Entry Time
       ↓
Change Slot Status to OCCUPIED
       ↓
Vehicle Parked
```

---

## Vehicle Exit Flow

```text
Vehicle Exit Request
       ↓
Search Active Parking Record
       ↓
Record Exit Time
       ↓
Calculate Duration
       ↓
Calculate Parking Fee
       ↓
Record Payment
       ↓
Update Parking Record
       ↓
Change Slot Status to AVAILABLE
       ↓
Parking Session Completed
```

---

# 14. Business Rules

The following rules must be enforced:

1. A vehicle cannot be assigned to an occupied slot.
2. A car can only be assigned to a car parking slot.
3. A bike can only be assigned to a bike parking slot.
4. A vehicle can only have one active parking session at a time.
5. A parking record must exist before a vehicle can exit.
6. A slot becomes `OCCUPIED` after vehicle entry.
7. A slot becomes `AVAILABLE` after vehicle exit.
8. Parking fees are calculated based on the parking duration.
9. A completed parking session cannot be modified without admin access.

---

# 15. Non-Functional Requirements

The system should be:

### Simple

The application should have a clean and easy-to-understand interface.

### Reliable

The database should maintain correct parking and payment records.

### Responsive

Basic operations such as vehicle entry and slot search should complete quickly.

### Maintainable

The code should be organized into frontend, backend, and database layers.

---

# 16. Project Scope

## Included

* User login.
* Admin and staff roles.
* Vehicle management.
* Parking slot management.
* Vehicle entry.
* Vehicle exit.
* Automatic parking fee calculation.
* Payment recording.
* Parking history.
* Basic dashboard.

## Not Included

The following features are intentionally excluded to keep the project manageable:

* Online payment gateway integration.
* GPS tracking.
* Vehicle number plate recognition.
* AI-based parking detection.
* SMS notifications.
* Email notifications.
* Multi-location parking.
* Microservices architecture.
* Real-time IoT sensors.
* Advanced analytics.
* Cloud deployment.
* Docker or Kubernetes.

These features may be considered as future improvements but are not required for the first version.

---

# 17. Development Phases

## Phase 1: Database Setup

Tasks:

* Create MySQL database.
* Create five main tables.
* Define primary and foreign key relationships.
* Insert sample parking slots.

---

## Phase 2: Python Backend

Tasks:

* Setup FastAPI project.
* Connect FastAPI with MySQL.
* Create database models.
* Develop authentication API.
* Develop vehicle APIs.
* Develop slot APIs.
* Implement parking entry logic.
* Implement parking exit logic.
* Implement fee calculation.
* Develop payment APIs.
* Develop dashboard API.

---

## Phase 3: Java Frontend

Tasks:

* Create login screen.
* Create dashboard.
* Create vehicle management screen.
* Create parking slot screen.
* Create vehicle entry screen.
* Create vehicle exit screen.
* Create payment screen.
* Connect Java application with REST APIs.

---

## Phase 4: Integration and Testing

Tasks:

* Connect Java frontend with FastAPI.
* Test all API endpoints.
* Test vehicle entry and exit flow.
* Test parking slot status updates.
* Test parking fee calculations.
* Test payment records.
* Fix errors.

---

# 18. Success Criteria

The project will be considered complete when:

* Users can log in successfully.
* Vehicles can be registered.
* Available slots can be viewed.
* Vehicles can be assigned to compatible parking slots.
* Occupied slots cannot be assigned again.
* Vehicle entry and exit times are correctly stored.
* Parking duration is correctly calculated.
* Parking fees are correctly calculated.
* Payments are recorded.
* Parking history can be viewed.
* Dashboard statistics are correctly displayed.

---

# 19. Future Enhancements

Possible future improvements:

* QR code-based parking tickets.
* Online payment integration.
* Vehicle number plate recognition.
* Parking reservation system.
* Real-time parking availability.
* Mobile application.
* Multiple parking locations.
* Advanced reports and analytics.

---

# 20. Final MVP Definition

The minimum working version of the project must successfully perform the following workflow:

```text
Login
  ↓
Add / Find Vehicle
  ↓
Check Available Slot
  ↓
Assign Slot
  ↓
Record Vehicle Entry
  ↓
Vehicle Parks
  ↓
Record Vehicle Exit
  ↓
Calculate Fee
  ↓
Record Payment
  ↓
Free Parking Slot
  ↓
Store Parking History
```

The core of the system is:

```text
Vehicle → Parking Slot → Parking Record → Payment
```

Any feature outside this flow is optional and should not be developed until the core system works correctly.
