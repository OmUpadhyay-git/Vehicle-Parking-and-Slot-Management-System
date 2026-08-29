# Project Memory

## Project Overview

Vehicle Parking and Slot Management System — a desktop application for managing vehicle entry, parking slot allocation, vehicle exit, parking fees, and payment records.

## Technology Stack

- Frontend: Java + Java Swing
- Backend: Python + FastAPI
- Database: MySQL
- ORM: SQLAlchemy
- Communication: REST API / JSON

## Architecture Decisions

- Three-layer architecture: Presentation (Java Swing) → Application (FastAPI) → Data (MySQL)
- Java never connects directly to MySQL
- All business logic lives in Python backend
- Backend returns consistent JSON responses with `success`, `message`, `data` fields

## Database Status

- MySQL 8.0 running on localhost:3306
- Database `parking_db` created
- Credentials: root / Password
- **Tables auto-created via SQLAlchemy** + **auto-seeded on startup** (users + 20 slots)
- Models defined in `backend/models/` — SQLAlchemy models with relationships
- `database.py` loads from `.env`, connection verified OK
- Relationships: Vehicle 1→N ParkingRecords, ParkingSlot 1→N ParkingRecords, ParkingRecord 1→1 Payment

## Backend Status

- `main.py` — FastAPI app with CORS middleware, all 6 routers registered, auto-create tables, **auto-seed data on startup**
- `database.py` — SQLAlchemy setup, loads DATABASE_URL from `.env`, connection verified OK
- `.env` — created with correct credentials
- **Models**: all 5 models with full relationships (back_populates, ForeignKey)
- **Schemas**: all 5 schema files with request/response Pydantic models
- **Routes**: all 6 route files fully implemented
  - auth.py: POST /login (validate credentials, return user info)
  - vehicles.py: POST/GET/GET/{id}/GET/search/{number}
  - slots.py: POST/GET/GET/available/GET/occupied/PUT/{id}
  - parking.py: POST entry/POST exit/GET active/GET history
  - payments.py: POST/GET/GET/{id}
  - dashboard.py: GET /dashboard (total/available/occupied slots, active vehicles, today revenue)
- **Services**: fully implemented
  - fee_service.py: calculate_fee() with CAR (40/20) and BIKE (20/10) rates, partial hour rounding
  - parking_service.py: park_vehicle(), exit_vehicle(), get_active_parking_records(), get_parking_history(), get_dashboard_stats()
- Routes registered in `main.py` with CORS middleware

## Frontend Status

### Completed Today

- **Model classes** — All 5 models with full getters/setters/constructors:
  - `User.java` (userId, name, username, password, role)
  - `Vehicle.java` (vehicleId, vehicleNumber, vehicleType, ownerName, ownerPhone)
  - `ParkingSlot.java` (slotId, slotNumber, vehicleType, status)
  - `ParkingRecord.java` (recordId, vehicleId, slotId, entryTime, exitTime, duration, fee, status)
  - `Payment.java` (paymentId, recordId, amount, paymentMethod, paymentTime, status)

- **ApiService.java** — HTTP helper with method stubs for all endpoints:
  - Auth: login()
  - Vehicles: createVehicle(), getVehicles(), getVehicle(), searchVehicle()
  - Slots: createSlot(), getSlots(), getAvailableSlots(), getOccupiedSlots(), updateSlot()
  - Parking: parkVehicle(), exitVehicle(), getActiveParking(), getParkingHistory()
  - Payments: createPayment(), getPayments(), getPayment()
  - Dashboard: getDashboard()
  - HTTP helper: sendRequest() for GET/POST/PUT

- **Main.java** — Launches LoginFrame with system look and feel

- **LoginFrame.java** — Professional split-panel login UI:
  - Left branding panel (dark, parking icon, title, feature text, version)
  - Right form panel (Welcome Back, USERNAME/PASSWORD labels, fields, SIGN IN button)
  - Placeholder text within inputs, rounded borders, styled blue button
  - Basic validation (empty fields)
  - Dynamic screen-responsive sizing (60% width, 70% height, clamped)
  - GridBagLayout centers form; resizable with 800×520 minimum
  - Connected to backend POST /login API
  - Clears fields on success, disposes frame

- **DashboardFrame.java** — Main application frame:
  - Sidebar navigation with role-based menu (Admin/Staff)
  - CardLayout for panel switching
  - Logout with confirmation dialog
  - Sidebar button highlighting on selection
  - Mouse hover effects

- **DashboardPanel.java** — Dashboard cards:
  - 5 stat cards: Total Slots, Available, Occupied, Active Vehicles, Revenue
  - Color-coded cards (blue, green, red, purple, yellow)
  - updateStats() method ready for API data

- **VehiclePanel.java** — Vehicle management:
  - Search field + Search button
  - Add Vehicle button
  - Vehicle table (Vehicle No, Type, Owner Name, Phone)
  - Add Vehicle dialog with form validation
  - Placeholder data loaded

- **SlotPanel.java** — Parking slot management:
  - Add Slot button
  - Filter dropdown (All, Available, Occupied, Car, Bike)
  - Split pane: table + grid visualization
  - Color-coded status cells (green=AVAILABLE, red=OCCUPIED)
  - Grid visualization with slot boxes colored by status
  - Add Slot dialog

- **ParkingEntryPanel.java** — Vehicle entry:
  - Vehicle number input + Search button
  - Vehicle type display
  - Available slot dropdown
  - Entry time display
  - Park Vehicle button
  - Confirmation dialog
  - Status feedback

- **ParkingExitPanel.java** — Vehicle exit:
  - Vehicle number search
  - Vehicle details display (type, slot, entry time)
  - Exit time, duration, fee display
  - Payment method selection
  - Complete Exit button
  - Confirmation dialog

- **PaymentPanel.java** — Payment display:
  - Vehicle number, slot, fee display
  - Payment method selection (Cash, UPI, Card)
  - Pay button
  - Confirmation dialog
  - Success message

- **HistoryPanel.java** — Parking history:
  - Search field
  - History table (Vehicle, Slot, Entry, Exit, Duration, Fee, Payment)
  - Color-coded payment status (green=PAID, red=PENDING)
  - Placeholder data

### Files Changed

- `frontend/src/main/java/Main.java`
- `frontend/src/main/java/model/User.java`
- `frontend/src/main/java/model/Vehicle.java`
- `frontend/src/main/java/model/ParkingSlot.java`
- `frontend/src/main/java/model/ParkingRecord.java`
- `frontend/src/main/java/model/Payment.java`
- `frontend/src/main/java/service/ApiService.java`
- `frontend/src/main/java/ui/LoginFrame.java`
- `frontend/src/main/java/ui/DashboardFrame.java`
- `frontend/src/main/java/ui/DashboardPanel.java` (NEW)
- `frontend/src/main/java/ui/VehiclePanel.java`
- `frontend/src/main/java/ui/SlotPanel.java`
- `frontend/src/main/java/ui/ParkingEntryPanel.java`
- `frontend/src/main/java/ui/ParkingExitPanel.java`
- `frontend/src/main/java/ui/PaymentPanel.java`
- `frontend/src/main/java/ui/HistoryPanel.java`

### Verified

- All 16 Java files compile successfully (37 .class files)
- Application launches and shows login frame
- No compilation errors

## Business Rules

- Vehicle types: CAR, BIKE
- Slot statuses: AVAILABLE, OCCUPIED
- Parking statuses: PARKED, COMPLETED
- Payment methods: CASH, UPI, CARD
- Payment statuses: PAID, PENDING
- Fee rates: CAR (Rs.40 first hr, Rs.20 additional), BIKE (Rs.20 first hr, Rs.10 additional)
- Partial hours rounded up for fee calculation
- One vehicle cannot have multiple active sessions
- Vehicle type must match slot type

## Current Development Phase

Phase 4 — Integration & Testing (COMPLETE)

## Completed Work

- Project structure created
- Documentation copied to `docs/`
- MySQL `parking_db` database created
- `.env` file created with correct credentials
- `database.py` updated to load from `.env`
- Python dependencies installed
- MySQL connection verified OK
- **ALL Java frontend UI panels implemented and compiled**
- **MySQL schema created (5 tables)**
- **Backend models with SQLAlchemy relationships**
- **Backend schemas with Pydantic request/response models**
- **Backend services (fee calculation, parking logic, dashboard stats)**
- **Backend routes (all 13 API endpoints implemented)**
- **CORS middleware and route registration in main.py**
- **Frontend-Backend integration completed**
- **All placeholder data replaced with real API calls**
- **Password hashing (PBKDF2-SHA256) implemented**
- **Input validation on all schemas**
- **Error handling on all routes**
- **Dependency audit completed (no vulnerabilities)**
- **run.bat launcher created (starts backend + frontend)**
- **Auto-seeding in main.py (users + slots on startup)**
- **User om/om created for general access**
- **Login UI default credentials hint removed**

## Current Work

All phases completed. Project is ready for testing.

## Pending Work

None - all phases complete.

## Frontend Integration Status

All UI panels now use real API calls instead of placeholder data.

| Screen | API Endpoint | Integration Status |
|--------|-------------|-------------------|
| Login | POST /login | Integrated |
| Dashboard | GET /dashboard | Integrated |
| Vehicles | GET /vehicles, POST /vehicles, GET /vehicles/search/{number} | Integrated |
| Slots | GET /slots, POST /slots, GET /slots/available | Integrated |
| Entry | POST /parking/entry, GET /slots/available, GET /vehicles/search/{number} | Integrated |
| Exit | POST /parking/exit, GET /parking/active, GET /vehicles/search/{number} | Integrated |
| Payments | POST /payments | Integrated |
| History | GET /parking/history | Integrated |

## Important Decisions

- Using Architecture.md structures over PRD.md where conflicts exist (more complete)
- Backend uses `schemas/` folder for Pydantic models (standard FastAPI practice)
- Frontend uses placeholder data until backend APIs are ready
- ParkingExitPanel handles both exit and payment in one flow (per Design.md)

## Login UI Fix (2026-08-28)

- **Issue 1:** Form fields and button extended beyond visible window area, components clipped
- **Issue 2:** Fixed frame size did not adapt to different screen resolutions
- **Fix 1:** Dynamic frame sizing based on screen (60% width, 70% height, clamped 900–1200 × 550–750)
- **Fix 2:** GridBagLayout with CENTER anchor + weight constraints centers form in right panel
- **Fix 3:** Form panel has fixed readable width (300–340px) so it never stretches
- **Layout:** GridLayout(1,2) horizontal split; GridBagLayout centers form; BoxLayout vertical flow
- **Fields:** Username, password, button share same width; max width = Integer.MAX_VALUE, fixed height
- **Resizable:** Frame is now resizable with 800×520 minimum size
- **Verified:** Compiled and launched — no clipping, all components visible

## Known Issues

- Deprecation warning in ApiService.java (non-critical, Java HttpURLConnection)

## Launcher (run.bat)

Windows batch file to start the complete project:

| Step | Action |
|------|--------|
| 1 | Check Python is installed |
| 2 | Check Java is installed |
| 3 | Check MySQL (warning if not found) |
| 4 | Install Python dependencies |
| 5 | Compile all Java files |
| 6 | Start FastAPI backend on port 8000 |
| 7 | Start Java Swing frontend |

**Usage:** Double-click `run.bat` in project root

**Features:**
- Auto-installs Python dependencies
- Auto-compiles Java source files
- Waits for backend to be ready before starting frontend
- Shows login credentials and stop instructions

## Resume Instructions

1. Read docs/PRD.md, docs/Architecture.md, docs/Rules.md, docs/Design.md
2. Read docs/memory.md
3. All 4 phases complete
4. **Easiest way to run:** Double-click `run.bat` in project root
5. Manual way:
   - Start MySQL
   - Backend: `cd backend && python -m uvicorn main:app --host 0.0.0.0 --port 8000 --reload`
   - Frontend: `cd frontend && javac -d build -sourcepath src/main/java src/main/java/**/*.java && java -cp build Main`
6. Login:
   - Admin: admin / admin123
   - Staff: staff / staff123
   - General: om / om

## Input Validation

All schemas use strict Pydantic v2 validation:

| Schema | Field | Validation |
|--------|-------|------------|
| UserLogin | username | min_length=2, max_length=50, pattern=`^[a-zA-Z0-9_]+$` |
| UserLogin | password | min_length=2, max_length=100 |
| VehicleCreate | vehicle_number | min_length=5, max_length=20, alphanumeric only |
| VehicleCreate | vehicle_type | pattern=`^(CAR\|BIKE)$` |
| VehicleCreate | owner_name | min_length=2, max_length=100, letters+spaces only |
| VehicleCreate | owner_phone | min_length=10, max_length=15, digits only |
| SlotCreate | slot_number | min_length=1, max_length=10, alphanumeric only |
| SlotCreate | vehicle_type | pattern=`^(CAR\|BIKE)$` |
| SlotCreate | status | pattern=`^(AVAILABLE\|OCCUPIED)$` |
| ParkingEntry | vehicle_number | min_length=5, max_length=20, alphanumeric only |
| ParkingEntry | slot_id | gt=0 (positive integer) |
| ParkingExit | record_id | gt=0 |
| ParkingExit | payment_method | pattern=`^(CASH\|UPI\|CARD)$` |
| PaymentCreate | record_id | gt=0 |
| PaymentCreate | amount | gt=0, le=100000, max 2 decimal places |
| PaymentCreate | payment_method | pattern=`^(CASH\|UPI\|CARD)$` |

Rejected inputs return 422 Unprocessable Entity with detailed error messages.

## Changes This Session (2026-08-29)

### Login UI
- Removed default credentials hint text from LoginFrame.java
- Recompiled Java (37 .class files)

### Database Auto-Seeding
- **Problem:** Tables were empty because `schema.sql` was never executed
- **Fix:** Added `seed_data()` to `main.py` that auto-creates users and slots on startup if empty
- **Users seeded:** admin/admin123, staff/staff123, om/om
- **Slots seeded:** 20 slots (10 CAR A1-A10, 10 BIKE B1-B10)

### New User
- Created user `om` with password `om` and role `STAFF`

### Validation Fix
- Relaxed `UserLogin` schema: username min 3→2, password min 4→2
- Reason: short credentials like `om/om` were rejected with 422 error

### Security
- pip-audit: No known vulnerabilities found
- `database.py`: Removed hardcoded fallback password, now requires .env
- `schema.sql`: Replaced plaintext passwords with PBKDF2-SHA256 hashed passwords
- `.env` already in `.gitignore` (verified)
- Created `services/auth_service.py` with PBKDF2-SHA256 hashing (100k iterations + salt)
- Auth route now verifies hashed passwords instead of plaintext comparison

### Error Handling
- All 6 route files wrapped in try/except blocks
- Errors logged server-side with `logging` module
- Users see generic messages: "An error occurred while..."
- No stack traces, file paths, or database errors exposed to frontend

## Last Updated

2026-08-29 — Phase 4 completed. Frontend-backend integration done. run.bat launcher created. Auto-seeding added to main.py. User om/om created. Relaxed login validation (min 2 chars).
