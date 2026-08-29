# Design Document

# Vehicle Parking and Slot Management System

## 1. Purpose

This document defines the user interface and application design for the Vehicle Parking and Slot Management System.

The design should focus on:

* Simplicity.
* Easy navigation.
* Fast vehicle entry and exit.
* Clear parking slot status.
* Minimal screens.
* Minimal user actions.

This is a solo project, so the UI should be clean and functional rather than overly complex.

---

# 2. Design Principles

The application should follow these principles:

1. Keep the interface simple.
2. Show important information clearly.
3. Avoid unnecessary screens.
4. Reduce the number of clicks required.
5. Use consistent buttons and layouts.
6. Clearly separate available and occupied parking slots.
7. Show clear success and error messages.
8. Keep navigation consistent on every screen.
9. Design for desktop use.
10. Prioritize functionality over visual complexity.

---

# 3. Application Layout

The main application will use a simple dashboard layout.

```text
┌──────────────────────────────────────────────────────────────┐
│  VEHICLE PARKING & SLOT MANAGEMENT SYSTEM                    │
├───────────────┬──────────────────────────────────────────────┤
│               │                                              │
│   DASHBOARD   │                                              │
│               │                                              │
│   VEHICLES    │              MAIN CONTENT                    │
│               │                                              │
│   SLOTS       │                                              │
│               │                                              │
│   PARKING     │                                              │
│               │                                              │
│   HISTORY     │                                              │
│               │                                              │
│   LOGOUT      │                                              │
│               │                                              │
└───────────────┴──────────────────────────────────────────────┘
```

The application contains two main sections:

### Sidebar

Used for navigation.

### Main Content Area

Displays the selected screen.

---

# 4. Navigation Structure

The main navigation should contain:

```text
Dashboard
│
├── Vehicles
│
├── Parking Slots
│
├── Vehicle Entry
│
├── Vehicle Exit
│
├── Parking History
│
└── Logout
```

For Admin users:

```text
Dashboard
Vehicles
Parking Slots
Vehicle Entry
Vehicle Exit
Payments
Parking History
Logout
```

For Staff users:

```text
Dashboard
Vehicles
Vehicle Entry
Vehicle Exit
Parking History
Logout
```

Staff users should not see parking slot configuration options.

---

# 5. Login Screen Design

The login screen should be minimal.

```text
┌─────────────────────────────────────┐
│                                     │
│       VEHICLE PARKING SYSTEM        │
│                                     │
│         Username                    │
│       ┌─────────────────┐           │
│       │                 │           │
│       └─────────────────┘           │
│                                     │
│         Password                    │
│       ┌─────────────────┐           │
│       │ ************    │           │
│       └─────────────────┘           │
│                                     │
│          [ LOGIN ]                  │
│                                     │
└─────────────────────────────────────┘
```

### Required Components

* Application title.
* Username field.
* Password field.
* Login button.
* Error message area.

Example error:

```text
Invalid username or password.
```

After successful login:

```text
Login
  ↓
Check User Role
  ↓
Open Dashboard
```

---

# 6. Dashboard Design

The dashboard should provide a quick overview of the parking system.

```text
┌──────────────────────────────────────────────────────────┐
│ Dashboard                                                │
├──────────────────────────────────────────────────────────┤
│                                                          │
│  ┌──────────────┐  ┌──────────────┐                     │
│  │ Total Slots  │  │ Available    │                     │
│  │     50       │  │     20       │                     │
│  └──────────────┘  └──────────────┘                     │
│                                                          │
│  ┌──────────────┐  ┌──────────────┐                     │
│  │ Occupied     │  │ Active       │                     │
│  │     30       │  │ Vehicles 30  │                     │
│  └──────────────┘  └──────────────┘                     │
│                                                          │
│  ┌──────────────────────────────────────┐                │
│  │ Today's Revenue: ₹2,500              │                │
│  └──────────────────────────────────────┘                │
│                                                          │
└──────────────────────────────────────────────────────────┘
```

### Dashboard Information

The dashboard should display:

* Total Slots.
* Available Slots.
* Occupied Slots.
* Active Vehicles.
* Today's Revenue.

Do not add complicated charts in the MVP.

The dashboard should load its data from:

```text
GET /dashboard
```

---

# 7. Vehicle Management Screen

This screen allows users to manage and search vehicles.

```text
┌──────────────────────────────────────────────────────────┐
│ Vehicle Management                                       │
├──────────────────────────────────────────────────────────┤
│                                                          │
│ Search Vehicle: [________________] [ SEARCH ]            │
│                                                          │
│ [ + ADD VEHICLE ]                                        │
│                                                          │
├──────────────────────────────────────────────────────────┤
│ Vehicle No. │ Type │ Owner Name │ Phone                 │
├─────────────┼──────┼────────────┼───────────────────────│
│ DL01AB1234  │ Car  │ Rahul      │ 9876543210           │
│ DL02XY5678  │ Bike │ Amit       │ 9876543211           │
│                                                          │
└──────────────────────────────────────────────────────────┘
```

### Actions

* Add vehicle.
* Search vehicle.
* View vehicle details.

Vehicle input fields:

```text
Vehicle Number
Vehicle Type
Owner Name
Owner Phone
```

---

# 8. Add Vehicle Form

```text
┌───────────────────────────────────┐
│ Add Vehicle                       │
├───────────────────────────────────┤
│                                   │
│ Vehicle Number                    │
│ [________________________]        │
│                                   │
│ Vehicle Type                      │
│ [ Car / Bike ▼ ]                  │
│                                   │
│ Owner Name                        │
│ [________________________]        │
│                                   │
│ Owner Phone                       │
│ [________________________]        │
│                                   │
│ [ CANCEL ]        [ SAVE ]        │
│                                   │
└───────────────────────────────────┘
```

Validation errors should appear near the input field.

Example:

```text
Vehicle number is required.
```

---

# 9. Parking Slot Management Screen

This screen is mainly for Admin users.

```text
┌──────────────────────────────────────────────────────────┐
│ Parking Slot Management                                  │
├──────────────────────────────────────────────────────────┤
│                                                          │
│ [ + ADD SLOT ]                                           │
│                                                          │
│ Filter: [ All ▼ ]                                        │
│                                                          │
├──────────────────────────────────────────────────────────┤
│ Slot │ Vehicle Type │ Status                            │
├──────┼──────────────┼───────────────────────────────────│
│ A1   │ Car          │ AVAILABLE                         │
│ A2   │ Car          │ OCCUPIED                          │
│ B1   │ Bike         │ AVAILABLE                         │
│ B2   │ Bike         │ OCCUPIED                          │
│                                                          │
└──────────────────────────────────────────────────────────┘
```

### Filter Options

```text
All
Available
Occupied
Car
Bike
```

---

# 10. Parking Slot Visualization

A simple grid can be used to visually display parking slots.

```text
CAR PARKING

┌────┐ ┌────┐ ┌────┐ ┌────┐
│ A1 │ │ A2 │ │ A3 │ │ A4 │
│ AV │ │ OC │ │ AV │ │ OC │
└────┘ └────┘ └────┘ └────┘


BIKE PARKING

┌────┐ ┌────┐ ┌────┐ ┌────┐
│ B1 │ │ B2 │ │ B3 │ │ B4 │
│ AV │ │ OC │ │ AV │ │ AV │
└────┘ └────┘ └────┘ └────┘
```

Legend:

```text
AV = AVAILABLE
OC = OCCUPIED
```

The status should be visually distinguishable.

For example:

* Available → Green.
* Occupied → Red.

Do not use too many different colors.

---

# 11. Vehicle Entry Screen

The vehicle entry process should be fast.

```text
┌──────────────────────────────────────────┐
│ Vehicle Entry                            │
├──────────────────────────────────────────┤
│                                          │
│ Vehicle Number                           │
│ [____________________________] [ SEARCH ]│
│                                          │
│ Vehicle Type                             │
│ [ Car ▼ ]                                │
│                                          │
│ Available Slot                           │
│ [ A5 ▼ ]                                 │
│                                          │
│ Entry Time                               │
│ Automatically Generated                  │
│                                          │
│              [ PARK VEHICLE ]            │
│                                          │
└──────────────────────────────────────────┘
```

### Entry Flow

```text
Enter Vehicle Number
        ↓
Search Vehicle
        ↓
If Vehicle Exists
        ↓
Show Vehicle Details
        ↓
Show Compatible Available Slots
        ↓
Select Slot
        ↓
Click Park Vehicle
        ↓
Show Confirmation
```

If the vehicle does not exist:

```text
Vehicle Not Found
        ↓
Open Add Vehicle Form
        ↓
Save Vehicle
        ↓
Continue Parking Entry
```

---

# 12. Vehicle Exit Screen

The vehicle exit screen should automatically calculate parking information.

```text
┌──────────────────────────────────────────┐
│ Vehicle Exit                             │
├──────────────────────────────────────────┤
│                                          │
│ Vehicle Number                           │
│ [________________________] [ SEARCH ]    │
│                                          │
│ Vehicle Type: Car                        │
│ Parking Slot: A5                         │
│ Entry Time: 10:15 AM                     │
│                                          │
│ Exit Time: Automatically Generated       │
│                                          │
│ Duration: 2 Hours                        │
│ Parking Fee: ₹60                         │
│                                          │
│ Payment Method                           │
│ [ Cash ▼ ]                               │
│                                          │
│             [ COMPLETE EXIT ]            │
│                                          │
└──────────────────────────────────────────┘
```

The fee should come from the backend.

The Java frontend should only display the calculated result.

---

# 13. Payment Screen

The payment screen should be simple.

```text
┌──────────────────────────────────────────┐
│ Payment                                  │
├──────────────────────────────────────────┤
│                                          │
│ Vehicle Number: DL01AB1234               │
│ Slot: A5                                 │
│                                          │
│ Parking Fee: ₹60                         │
│                                          │
│ Payment Method                           │
│ [ Cash ▼ ]                               │
│                                          │
│                  [ PAY ]                 │
│                                          │
└──────────────────────────────────────────┘
```

Supported methods:

```text
Cash
UPI
Card
```

After successful payment:

```text
Payment Successful
Vehicle Exit Completed
Slot A5 is now Available
```

---

# 14. Parking History Screen

The parking history screen displays completed parking sessions.

```text
┌─────────────────────────────────────────────────────────────┐
│ Parking History                                             │
├─────────────────────────────────────────────────────────────┤
│ Search: [____________] [ SEARCH ]                           │
│                                                             │
├─────────────────────────────────────────────────────────────┤
│ Vehicle │ Slot │ Entry │ Exit │ Duration │ Fee │ Payment    │
├─────────┼──────┼───────┼──────┼──────────┼─────┼────────────│
│ DL01... │ A5   │ 10:00 │ 12:00│ 2 Hours  │ ₹60 │ PAID       │
│ DL02... │ B2   │ 09:00 │ 10:30│ 2 Hours  │ ₹30 │ PAID       │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

The MVP does not require complex reporting or analytics.

---

# 15. Navigation Flow

```text
                    LOGIN
                      │
                      ▼
                  DASHBOARD
                      │
        ┌─────────────┼─────────────┐
        ▼             ▼             ▼
    VEHICLES         SLOTS        PARKING
        │             │             │
        ▼             ▼        ┌────┴────┐
    Add/Search      Manage     ▼         ▼
                           VEHICLE     VEHICLE
                            ENTRY       EXIT
                                │         │
                                ▼         ▼
                              PARKING   PAYMENT
                                │         │
                                └────┬────┘
                                     ▼
                                   HISTORY
```

---

# 16. User Interaction Rules

The system should provide clear feedback for every important action.

## Success Message

Example:

```text
✓ Vehicle parked successfully.
Slot A5 has been assigned.
```

## Error Message

Example:

```text
✗ Vehicle is already parked.
```

## Warning Message

Example:

```text
No compatible parking slot is available.
```

Important actions should not silently fail.

---

# 17. Form Design Rules

All forms should follow a consistent structure.

```text
Screen Title

Label
[ Input Field ]

Label
[ Input Field ]

[ CANCEL ]   [ SAVE / SUBMIT ]
```

Rules:

1. Labels should be clear.
2. Required fields should be validated.
3. Buttons should have clear names.
4. Error messages should explain the problem.
5. Forms should not contain unnecessary fields.
6. Save buttons should only be used for valid operations.

---

# 18. Button Rules

Use simple and consistent button labels.

Examples:

```text
LOGIN
SAVE
CANCEL
SEARCH
ADD VEHICLE
ADD SLOT
PARK VEHICLE
COMPLETE EXIT
PAY
LOGOUT
```

Avoid vague buttons such as:

```text
SUBMIT DATA
PROCESS
CLICK HERE
ACTION
```

Buttons should clearly describe what they do.

---

# 19. Confirmation Dialogs

Confirmation dialogs should be used for important actions.

### Vehicle Entry

```text
Confirm Vehicle Parking

Vehicle: DL01AB1234
Slot: A5

[ CANCEL ] [ CONFIRM ]
```

### Vehicle Exit

```text
Confirm Vehicle Exit

Vehicle: DL01AB1234
Total Fee: ₹60

[ CANCEL ] [ CONFIRM ]
```

The confirmation step helps prevent accidental operations.

---

# 20. Loading and Error States

The application should handle API delays and errors.

### Loading

```text
Loading...
```

or:

```text
Processing parking request...
```

### API Error

```text
Unable to connect to the server.

Please try again.
```

The application should not freeze while waiting for API responses.

---

# 21. UI Data Flow

The frontend should follow this data flow:

```text
User Action
     ↓
Java UI
     ↓
ApiService
     ↓
REST API Request
     ↓
FastAPI Backend
     ↓
JSON Response
     ↓
ApiService
     ↓
Update UI
```

Example:

```text
User clicks "Park Vehicle"
        ↓
Java collects input
        ↓
ApiService sends request
        ↓
POST /parking/entry
        ↓
FastAPI processes request
        ↓
Returns JSON response
        ↓
Java displays success or error
```

---

# 22. Responsive Design

This is a desktop application.

The UI should be designed for:

```text
Minimum Resolution: 1024 × 768
Recommended Resolution: 1366 × 768 or higher
```

The application does not need mobile responsiveness.

The main content should resize reasonably when the application window is resized.

---

# 23. Design Consistency Rules

The entire application should maintain:

* Consistent sidebar navigation.
* Consistent spacing.
* Consistent button styles.
* Consistent form layouts.
* Consistent table design.
* Consistent error messages.
* Consistent success messages.

Avoid giving every screen a completely different design.

---

# 24. Screens Required for MVP

The MVP should contain only these main screens:

| Screen                  | Required |
| ----------------------- | -------- |
| Login                   | Yes      |
| Dashboard               | Yes      |
| Vehicle Management      | Yes      |
| Add Vehicle             | Yes      |
| Parking Slot Management | Yes      |
| Vehicle Entry           | Yes      |
| Vehicle Exit            | Yes      |
| Payment                 | Yes      |
| Parking History         | Yes      |

No additional screens should be created unless required by the core workflow.

---

# 25. Final UI Architecture

```text
JAVA SWING APPLICATION
│
├── Login
│
└── Main Application
    │
    ├── Sidebar Navigation
    │
    └── Main Content
        │
        ├── Dashboard
        │
        ├── Vehicles
        │   ├── Vehicle List
        │   └── Add Vehicle
        │
        ├── Parking Slots
        │
        ├── Parking
        │   ├── Vehicle Entry
        │   └── Vehicle Exit
        │
        ├── Payment
        │
        └── Parking History
```

---

# 26. Final Design Principle

The final design should follow this rule:

```text
Simple
  ↓
Clear
  ↓
Fast
  ↓
Functional
```

The user should be able to perform the main parking operation with minimal steps:

```text
Find Vehicle
     ↓
Select Available Slot
     ↓
Park Vehicle
```

And during exit:

```text
Find Vehicle
     ↓
View Parking Details
     ↓
Calculate Fee
     ↓
Record Payment
     ↓
Complete Exit
```

The design should remain focused on the core workflow:

```text
Vehicle → Slot → Parking → Payment → Exit
```

Any visual feature that does not improve this workflow should not be added to the MVP.
