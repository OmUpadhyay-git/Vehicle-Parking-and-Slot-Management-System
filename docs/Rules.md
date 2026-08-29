# System Rules

# Vehicle Parking and Slot Management System

## 1. Purpose

This document defines the rules and constraints of the Vehicle Parking and Slot Management System.

These rules ensure that the system behaves correctly and prevents invalid operations such as assigning an occupied slot, parking the same vehicle twice, or completing an exit without an active parking record.

The rules should primarily be enforced by the **Python FastAPI backend**.

---

# 2. General System Rules

1. Every user must log in before accessing the system.
2. Every vehicle must have a unique vehicle number.
3. Every parking slot must have a unique slot number.
4. A parking record must always be connected to one vehicle and one parking slot.
5. A vehicle can have multiple parking records over time.
6. A parking slot can be used by multiple vehicles over time.
7. Historical parking records must not be deleted during normal operations.
8. The Java frontend must communicate with the system through the FastAPI backend.
9. The Java application must not directly access the MySQL database.
10. All important business validation must be performed on the backend.

---

# 3. User and Authentication Rules

## 3.1 Login Rules

1. A user must provide a valid username and password.
2. Invalid login credentials must return an error.
3. Users cannot access the main system without successful authentication.
4. The system must identify the user's role after login.

---

## 3.2 User Roles

The system supports two roles:

```text
ADMIN
STAFF
```

### Admin Permissions

An Admin can:

* Manage parking slots.
* View all vehicles.
* View parking history.
* View payment history.
* View dashboard statistics.
* Perform parking entry and exit operations.

### Staff Permissions

Staff can:

* Add or search vehicles.
* Perform vehicle entry.
* Perform vehicle exit.
* Record payments.
* View active parking records.

Staff should not manage core parking slot configuration.

---

# 4. Vehicle Rules

1. Every vehicle must have a unique vehicle number.
2. Vehicle numbers should not be empty.
3. A vehicle must have a valid vehicle type.
4. Supported vehicle types are:

```text
CAR
BIKE
```

5. A vehicle cannot have more than one active parking session at the same time.
6. If a vehicle is already parked, another parking entry must be rejected.
7. A vehicle can be parked again after its previous parking session is completed.
8. Vehicle information should be stored and reused for future parking sessions.
9. Vehicle number searches should be case-insensitive.

Example:

```text
dl01ab1234
DL01AB1234
Dl01Ab1234
```

All should be treated as the same vehicle number.

---

# 5. Parking Slot Rules

1. Every parking slot must have a unique slot number.
2. Every slot must support a defined vehicle type.
3. Supported slot vehicle types are:

```text
CAR
BIKE
```

4. A slot can only have the following statuses:

```text
AVAILABLE
OCCUPIED
```

5. A vehicle can only be assigned to a compatible parking slot.

Example:

```text
CAR  → CAR SLOT
BIKE → BIKE SLOT
```

6. A vehicle cannot be assigned to an `OCCUPIED` slot.
7. A slot must automatically change to `OCCUPIED` when a vehicle is successfully parked.
8. A slot must automatically change to `AVAILABLE` after the vehicle completes the exit process.
9. A slot should not be manually marked as `AVAILABLE` if an active parking record exists for that slot.

---

# 6. Vehicle Entry Rules

Before creating a parking record, the system must check:

1. The vehicle number is provided.
2. The vehicle type is valid.
3. The vehicle is not already parked.
4. A compatible parking slot is available.
5. The selected slot is currently `AVAILABLE`.

If all conditions are valid:

```text
Create Parking Record
        ↓
Save Entry Time
        ↓
Set Record Status = PARKED
        ↓
Set Slot Status = OCCUPIED
```

The parking record and slot status update should be treated as one operation.

If parking record creation fails, the slot status must not change.

---

# 7. Vehicle Exit Rules

Before processing vehicle exit, the system must check:

1. The vehicle has an active parking record.
2. The parking record status is `PARKED`.
3. The exit time must be later than the entry time.
4. The parking slot associated with the record must exist.

When the exit is processed:

```text
Record Exit Time
        ↓
Calculate Duration
        ↓
Calculate Parking Fee
        ↓
Process Payment
        ↓
Mark Record as COMPLETED
        ↓
Set Slot Status = AVAILABLE
```

A completed parking record cannot be processed again.

---

# 8. Parking Record Rules

A parking record must contain:

* Vehicle ID.
* Slot ID.
* Entry time.
* Parking status.

When completed, it must also contain:

* Exit time.
* Parking duration.
* Parking fee.

Parking record statuses:

```text
PARKED
COMPLETED
```

Rules:

1. A vehicle can have only one record with status `PARKED`.
2. A completed record cannot return to `PARKED`.
3. Entry time cannot be modified after the parking session starts.
4. Exit time cannot be earlier than entry time.
5. Duration cannot be negative.
6. A completed record must contain a calculated parking fee.

---

# 9. Parking Duration Rules

Parking duration is calculated using:

```text
Duration = Exit Time - Entry Time
```

The duration should be stored in minutes.

Example:

```text
Entry Time: 10:15 AM
Exit Time: 12:45 PM

Duration = 150 minutes
```

For fee calculation, partial hours should be rounded up.

Example:

```text
Duration: 1 hour 10 minutes

Charged Duration: 2 hours
```

This rule prevents ambiguity in parking fee calculation.

---

# 10. Parking Fee Rules

The fee depends on:

1. Vehicle type.
2. Parking duration.

Initial rates:

## Bike

```text
First Hour = ₹20
Each Additional Hour = ₹10
```

## Car

```text
First Hour = ₹40
Each Additional Hour = ₹20
```

### Fee Calculation Rule

```text
If charged hours <= 1:

Fee = First Hour Rate

If charged hours > 1:

Fee =
First Hour Rate
+
(Additional Hours × Additional Hour Rate)
```

Example for a Car:

```text
Parking Duration = 3 hours

First Hour = ₹40

Additional Hours = 2

Fee = ₹40 + (2 × ₹20)

Total Fee = ₹80
```

Example for a Bike:

```text
Parking Duration = 2 hours

First Hour = ₹20

Additional Hours = 1

Fee = ₹20 + (1 × ₹10)

Total Fee = ₹30
```

The fee calculation must be performed by the backend, not by the Java frontend.

---

# 11. Payment Rules

1. A payment must be linked to a parking record.
2. Payment amount must be greater than zero.
3. Payment amount should match the calculated parking fee.
4. Supported payment methods are:

```text
CASH
UPI
CARD
```

5. Payment statuses are:

```text
PAID
PENDING
```

6. A parking session should not be marked as fully completed until the required payment is successfully recorded.
7. A payment record must store the payment time.
8. A completed payment should not be duplicated for the same parking record.

For the MVP:

```text
One Parking Record → One Payment
```

---

# 12. Dashboard Rules

The dashboard must calculate and display:

* Total parking slots.
* Available parking slots.
* Occupied parking slots.
* Number of currently parked vehicles.
* Today's total revenue.

Rules:

```text
Total Slots
=
Available Slots + Occupied Slots
```

```text
Active Vehicles
=
Number of Parking Records
where status = PARKED
```

```text
Today's Revenue
=
Sum of PAID payments
recorded today
```

Only successful payments should be included in revenue.

---

# 13. Data Integrity Rules

The system must maintain correct relationships between tables.

## Vehicle and Parking Record

```text
One Vehicle
     │
     └── Can Have Many Parking Records
```

## Parking Slot and Parking Record

```text
One Parking Slot
     │
     └── Can Have Many Parking Records Over Time
```

## Parking Record and Payment

```text
One Parking Record
     │
     └── Has One Payment
```

Foreign key relationships must be maintained.

A parking record cannot reference a vehicle or slot that does not exist.

A payment cannot reference a parking record that does not exist.

---

# 14. API Rules

All APIs should:

1. Accept valid input.
2. Validate required fields.
3. Return appropriate success responses.
4. Return clear error messages.
5. Use JSON for request and response data.
6. Not expose database implementation details to the frontend.

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
  "message": "Vehicle is already parked"
}
```

---

# 15. Error Handling Rules

The backend should reject invalid operations.

Common errors include:

| Situation                    | Expected Result   |
| ---------------------------- | ----------------- |
| Invalid login                | Reject login      |
| Vehicle already parked       | Reject entry      |
| No available slot            | Reject parking    |
| Wrong vehicle type for slot  | Reject assignment |
| Occupied slot selected       | Reject assignment |
| Vehicle not currently parked | Reject exit       |
| Invalid payment amount       | Reject payment    |
| Duplicate payment            | Reject payment    |

Errors should return understandable messages.

---

# 16. Frontend Rules

The Java frontend is responsible for:

* Displaying screens.
* Collecting user input.
* Performing basic validation.
* Calling REST APIs.
* Displaying API responses and errors.

The frontend must not:

* Directly connect to MySQL.
* Contain core parking business logic.
* Independently calculate the final parking fee.
* Independently change parking slot status.

All critical decisions must come from the backend.

Correct flow:

```text
Java UI
   ↓
API Request
   ↓
FastAPI Validation and Logic
   ↓
MySQL
   ↓
API Response
   ↓
Java UI
```

---

# 17. Backend Rules

The Python backend is responsible for:

* Authentication validation.
* Vehicle validation.
* Slot validation.
* Vehicle-slot compatibility.
* Active parking session checks.
* Parking record creation.
* Parking duration calculation.
* Parking fee calculation.
* Payment validation.
* Slot status updates.
* Dashboard calculations.

The backend is the single source of truth for business rules.

---

# 18. Database Rules

1. Primary keys must uniquely identify each record.
2. Foreign keys must maintain table relationships.
3. Vehicle numbers must be unique.
4. Slot numbers must be unique.
5. A parking record must reference a valid vehicle.
6. A parking record must reference a valid slot.
7. A payment must reference a valid parking record.
8. Important records should not be deleted during normal operations.
9. Slot status must always match the actual parking state.

Example:

```text
Active Parking Record Exists
          ↓
Slot Status must be OCCUPIED
```

```text
No Active Parking Record
          ↓
Slot Status should be AVAILABLE
```

---

# 19. Development Rules

To keep the project manageable:

1. Build the database first.
2. Build and test the backend APIs before starting full frontend integration.
3. Test every API using Postman.
4. Keep the frontend and backend separated.
5. Do not add features outside the MVP until the core system works.
6. Keep classes and files focused on a single responsibility.
7. Avoid unnecessary frameworks and libraries.
8. Do not introduce microservices.
9. Do not introduce cloud infrastructure for the MVP.
10. Do not add AI, IoT, or vehicle recognition features.
11. Use Git for version control.
12. Commit working features regularly.

---

# 20. Final Core Rules

The entire project depends on these core rules:

```text
1. One vehicle cannot be parked twice at the same time.

2. One slot cannot contain more than one active vehicle.

3. Vehicle type must match slot type.

4. AVAILABLE slot
        ↓ Vehicle Entry
   OCCUPIED slot

5. OCCUPIED slot
        ↓ Vehicle Exit
   AVAILABLE slot

6. Every active parking session must have:
   Vehicle + Slot + Entry Time

7. Every completed parking session must have:
   Exit Time + Duration + Fee

8. Every payment must belong to one parking record.

9. Business logic belongs in the FastAPI backend.

10. Java is responsible only for the user interface and API communication.
```

---

# 21. Core System Flow

```text
Login
  ↓
Add / Find Vehicle
  ↓
Validate Vehicle
  ↓
Find Compatible Available Slot
  ↓
Create Parking Record
  ↓
Change Slot → OCCUPIED
  ↓
Vehicle Exit
  ↓
Calculate Duration
  ↓
Calculate Fee
  ↓
Record Payment
  ↓
Complete Parking Record
  ↓
Change Slot → AVAILABLE
  ↓
Store Parking History
```

These rules should be followed throughout development to ensure the system remains simple, consistent, and reliable.
