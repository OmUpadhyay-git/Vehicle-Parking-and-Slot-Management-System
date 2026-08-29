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
- Shared JSON parsing utilities in `service/JsonHelper.java` (no external JSON library)

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
  - `ParkingRecordResponse` includes optional fields: `vehicle_type`, `duration_minutes`, `payment_status`, `payment_method` (for history responses)
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
  - auth_service.py: PBKDF2-SHA256 password hashing and verification
- Routes registered in `main.py` with CORS middleware

## Frontend Status

### Model Classes (5 files)

- `User.java` — userId, name, username, password, role
- `Vehicle.java` — vehicleId, vehicleNumber, vehicleType, ownerName, ownerPhone
- `ParkingSlot.java` — slotId, slotNumber, vehicleType, status
- `ParkingRecord.java` — recordId, vehicleId, slotId, entryTime, exitTime, duration, fee, status
- `Payment.java` — paymentId, recordId, amount, paymentMethod, paymentTime, status

### Service Classes (2 files)

- **ApiService.java** — HTTP helper with methods for all 13 endpoints:
  - Auth: login()
  - Vehicles: createVehicle(), getVehicles(), getVehicle(), searchVehicle()
  - Slots: createSlot(), getSlots(), getAvailableSlots(), getOccupiedSlots(), updateSlot()
  - Parking: parkVehicle(), exitVehicle(), getActiveParking(), getParkingHistory()
  - Payments: createPayment(), getPayments(), getPayment()
  - Dashboard: getDashboard()
  - HTTP helper: sendRequest() for GET/POST/PUT with 5s timeouts

- **JsonHelper.java** (NEW) — Shared static utility methods for JSON parsing:
  - `extractField(json, key)` — Extract string value from JSON
  - `extractJsonInt(json, key)` — Extract integer value from JSON
  - `extractJsonDouble(json, key)` — Extract double value from JSON
  - `extractMessage(json, key)` — Extract message field from API response
  - `formatDateTime(isoDateTime)` — Convert ISO timestamp to "YYYY-MM-DD HH:MM"
  - `formatDuration(minutes)` — Convert minutes to "Xh Ym" format
  - Used by: VehiclePanel, SlotPanel, ParkingEntryPanel, ParkingExitPanel, HistoryPanel, DashboardPanel, PaymentPanel

### UI Classes (9 files)

- **Main.java** — Entry point, launches LoginFrame with system look and feel

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
  - CardLayout for panel switching (DASHBOARD, VEHICLES, SLOTS, ENTRY, EXIT, HISTORY)
  - Logout with confirmation dialog
  - Sidebar button highlighting on selection
  - Mouse hover effects
  - "Parking Slots" menu visible only for ADMIN role

- **DashboardPanel.java** — Dashboard cards:
  - 5 stat cards: Total Slots (blue), Available (green), Occupied (red), Active Vehicles (purple), Revenue (orange)
  - Fetches data from GET /dashboard via ApiService
  - Uses JsonHelper for JSON parsing
  - refreshData() method for manual refresh

- **VehiclePanel.java** — Vehicle management:
  - Search field + Search button + ADD VEHICLE button
  - Vehicle table (Vehicle No, Type, Owner Name, Phone)
  - Add Vehicle dialog with form validation
  - Loads vehicles from GET /vehicles via ApiService
  - Searches via GET /vehicles/search/{number}
  - Creates via POST /vehicles

- **SlotPanel.java** — Parking slot management:
  - Add Slot button
  - Filter dropdown (All, Available, Occupied, Car, Bike)
  - Split pane: table + grid visualization
  - Color-coded status cells (green=AVAILABLE, red=OCCUPIED)
  - Grid visualization with slot boxes colored by status
  - Add Slot dialog
  - Loads from GET /slots, creates via POST /slots

- **ParkingEntryPanel.java** — Vehicle entry:
  - Vehicle number input + Search button
  - Vehicle type display (auto-populated from search)
  - Available slot dropdown (filtered by vehicle type)
  - Entry time display (auto-generated)
  - Park Vehicle button with confirmation dialog
  - Loads available slots from GET /slots/available
  - Searches vehicle via GET /vehicles/search/{number}
  - Parks via POST /parking/entry

- **ParkingExitPanel.java** — Vehicle exit:
  - Vehicle number search
  - Vehicle details display (type, slot, entry time)
  - Exit time, duration, fee display (calculated client-side for preview)
  - Payment method selection (CASH, UPI, CARD)
  - Complete Exit button with confirmation dialog
  - Searches active records via GET /parking/active
  - Exits via POST /parking/exit (which also records payment)

- **PaymentPanel.java** — Payment display (standalone, not in DashboardFrame navigation):
  - Vehicle number, slot, fee display
  - Payment method selection (Cash, UPI, Card)
  - Pay button with confirmation dialog
  - Calls POST /payments via ApiService.createPayment()
  - Success/error feedback
  - Note: ParkingExitPanel handles payment inline; this panel is available for future use

- **HistoryPanel.java** — Parking history:
  - Search field (client-side filter by vehicle number)
  - History table (Vehicle, Slot, Entry, Exit, Duration, Fee, Payment)
  - Color-coded payment status (green=PAID, red=PENDING)
  - Loads from GET /parking/history via ApiService
  - Uses JsonHelper for formatting dates and durations

### Files List (17 Java files)

- `frontend/src/main/java/Main.java`
- `frontend/src/main/java/model/User.java`
- `frontend/src/main/java/model/Vehicle.java`
- `frontend/src/main/java/model/ParkingSlot.java`
- `frontend/src/main/java/model/ParkingRecord.java`
- `frontend/src/main/java/model/Payment.java`
- `frontend/src/main/java/service/ApiService.java`
- `frontend/src/main/java/service/JsonHelper.java`
- `frontend/src/main/java/ui/LoginFrame.java`
- `frontend/src/main/java/ui/DashboardFrame.java`
- `frontend/src/main/java/ui/DashboardPanel.java`
- `frontend/src/main/java/ui/VehiclePanel.java`
- `frontend/src/main/java/ui/SlotPanel.java`
- `frontend/src/main/java/ui/ParkingEntryPanel.java`
- `frontend/src/main/java/ui/ParkingExitPanel.java`
- `frontend/src/main/java/ui/PaymentPanel.java`
- `frontend/src/main/java/ui/HistoryPanel.java`

### Verified

- All 17 Java files compile successfully (38 .class files)
- Application launches and shows login frame
- No compilation errors
- All Python imports pass
- ParkingRecordResponse schema validates with new fields
- All 27 API endpoint tests pass
- All 58 unit tests pass

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

Phase 5 — Final QA, End-to-End Testing & Project Completion (COMPLETE)

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
- **Dashboard revenue bug fixed (removed dead count variable)**
- **Schema mismatch fixed (added history fields to ParkingRecordResponse)**
- **PaymentPanel wired to POST /payments API**
- **Duplicate JSON helpers extracted to shared JsonHelper.java**
- **Phase 5: All 27 API endpoint tests passed (login, vehicles, slots, parking entry/exit, payments, dashboard, history)**
- **Phase 5: 58 unit tests added and passing (fee_service, parking_service, schema validation)**
- **Phase 5: Java frontend compiles successfully (38 .class files)**
- **Phase 5: run.bat quoting bug fixed (line 142 — nested double quotes broke with spaces in path)**

## Current Work

All 5 phases complete. Project is fully functional and tested.

## Pending Work

- PaymentPanel not wired into DashboardFrame CardLayout (by design — exit flow handles payment inline)

## Frontend Integration Status

All UI panels use real API calls instead of placeholder data.

| Screen | API Endpoint | Integration Status |
|--------|-------------|-------------------|
| Login | POST /login | Integrated |
| Dashboard | GET /dashboard | Integrated |
| Vehicles | GET /vehicles, POST /vehicles, GET /vehicles/search/{number} | Integrated |
| Slots | GET /slots, POST /slots, GET /slots/available | Integrated |
| Entry | POST /parking/entry, GET /slots/available, GET /vehicles/search/{number} | Integrated |
| Exit | POST /parking/exit, GET /parking/active, GET /vehicles/search/{number} | Integrated |
| Payments | POST /payments | Integrated (via PaymentPanel, not in nav) |
| History | GET /parking/history | Integrated |

## Important Decisions

- Using Architecture.md structures over PRD.md where conflicts exist (more complete)
- Backend uses `schemas/` folder for Pydantic models (standard FastAPI practice)
- ParkingExitPanel handles both exit and payment in one flow (per Design.md)
- PaymentPanel exists as standalone component but is not in DashboardFrame navigation
- JsonHelper.java provides shared JSON parsing — no external JSON library used
- History API response uses `duration_minutes`/`payment_status`/`payment_method` field names (matching service layer), schema updated to accept them

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

- Deprecation warning in ApiService.java (non-critical, Java HttpURLConnection uses deprecated API)

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

## Session History

### 2026-08-28 — Initial Setup & All Phases

- Project structure created
- Documentation copied to `docs/`
- MySQL `parking_db` database created
- `.env` file created with correct credentials
- `database.py` updated to load from `.env`
- Python dependencies installed
- MySQL connection verified OK
- All 5 SQLAlchemy models created with relationships
- All 5 Pydantic schemas created with validation
- All 6 route files implemented (auth, vehicles, slots, parking, payments, dashboard)
- All 3 service files implemented (auth_service, fee_service, parking_service)
- All 16 Java UI files implemented and compiled
- CORS middleware added to main.py
- Frontend-backend integration completed
- All placeholder data replaced with real API calls
- Password hashing (PBKDF2-SHA256) implemented
- Input validation on all schemas
- Error handling on all routes
- run.bat launcher created
- Auto-seeding in main.py (users + slots on startup)
- User om/om created for general access
- Login UI default credentials hint removed
- Login UI fix: dynamic frame sizing, GridBagLayout centering, resizable

### 2026-08-29 — Phase 4 Bug Fixes & Quality Improvements

**Bug Fixes:**
- **Dashboard revenue:** Removed dead `today_revenue` count variable in `parking_service.py` — was querying payment count (unused) alongside the actual amount sum
- **Schema mismatch:** Added `vehicle_type`, `duration_minutes`, `payment_status`, `payment_method` fields to `ParkingRecordResponse` schema — `get_parking_history()` was returning these fields but the schema rejected them

**PaymentPanel Integration:**
- **Problem:** `PaymentPanel.java` had a `TODO` comment — payment POST was never sent to backend
- **Fix:** Added `currentRecordId` field, wired `apiService.createPayment()` call, added loading state and error handling
- **Note:** PaymentPanel is not currently wired into DashboardFrame's CardLayout (exit flow handles payment inline via ParkingExitPanel)

**JSON Helper Refactoring:**
- **Problem:** `extractField()`, `extractJsonInt()`, `extractJsonDouble()`, `extractMessage()`, `formatDateTime()`, `formatDuration()` were copy-pasted across 6 panel classes
- **Fix:** Created `service/JsonHelper.java` with all shared static methods
- Updated all panels (VehiclePanel, SlotPanel, ParkingEntryPanel, ParkingExitPanel, HistoryPanel, DashboardPanel, PaymentPanel) to use `JsonHelper`
- Removed all local duplicate methods from each panel

**Compilation:**
- All 17 Java files compile successfully (38 .class files)
- All Python imports pass
- ParkingRecordResponse schema validates with new fields

### 2026-08-29 — Phase 5: Final QA, Testing & Project Completion

**API Endpoint Testing (27 tests):**
- Login: valid admin, valid staff, wrong password, nonexistent user — all correct
- Vehicles: create CAR, create BIKE, duplicate rejection, get all, search, search not found — all correct
- Slots: get all (20 seeded), get available, create new slot, duplicate slot rejection — all correct
- Parking: park vehicle, reject already parked, reject type mismatch, exit with CASH, reject double exit, reject nonexistent record — all correct
- Payments: get payment history — correct
- Dashboard: stats with total_slots, available_slots, occupied_slots, active_vehicles, today_revenue — all correct
- BIKE flow: park BIKE in B1, exit with UPI — correct

**Unit Tests (58 tests, all passing):**
- `test_fee_service.py` (19 tests): CAR/BIKE fee calculation, edge cases (0 min, negative, invalid type, case insensitive)
- `test_parking_service.py` (11 tests): park_vehicle success/already parked/occupied/type mismatch/not found, exit_vehicle success/already completed/not found/payment creation, dashboard stats
- `test_schemas.py` (28 tests): VehicleCreate, SlotCreate, ParkingEntry, ParkingExit, PaymentCreate — valid inputs, invalid types, boundary values, pattern matching

**Bug Fix:**
- **run.bat line 142:** Fixed nested double quotes in `start cmd /c "java -cp "%JAVA_BUILD%" Main"` — path with spaces broke the command. Changed to `java -cp "%JAVA_BUILD%" Main` (no outer quotes).

**Compilation:**
- All 17 Java files compile successfully (38 .class files)
- All 58 Python unit tests pass
- All API endpoint tests pass

## Last Updated

2026-08-29 — Phase 5 complete. All 27 API endpoint tests passed. 58 unit tests added and passing. Java frontend compiles (38 .class files). Fixed run.bat quoting bug. Project fully tested and complete.
