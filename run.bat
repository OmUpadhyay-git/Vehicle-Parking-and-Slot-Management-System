@echo off
title Vehicle Parking System Launcher
color 0A
setlocal enabledelayedexpansion

echo ============================================
echo   Vehicle Parking ^& Slot Management System
echo ============================================
echo.

:: Get project root directory
set "PROJECT_ROOT=%~dp0"
set "BACKEND_DIR=%PROJECT_ROOT%backend"
set "FRONTEND_DIR=%PROJECT_ROOT%frontend"
set "JAVA_SRC=%FRONTEND_DIR%\src\main\java"
set "JAVA_BUILD=%FRONTEND_DIR%\build"
set "BACKEND_PORT=8000"

:: ============================================
:: STEP 1: Check Python
:: ============================================
echo [1/7] Checking Python...
python --version >nul 2>&1
if errorlevel 1 (
    echo [ERROR] Python is not installed or not in PATH.
    echo Please install Python 3.8+ from https://www.python.org
    echo.
    pause
    exit /b 1
)
for /f "tokens=*" %%i in ('python --version 2^>^&1') do echo        Found: %%i
echo.

:: ============================================
:: STEP 2: Check Java
:: ============================================
echo [2/7] Checking Java...
java -version >nul 2>&1
if errorlevel 1 (
    echo [ERROR] Java is not installed or not in PATH.
    echo Please install JDK 11+ from https://adoptium.net
    echo.
    pause
    exit /b 1
)
for /f "tokens=*" %%i in ('java -version 2^>^&1') do (
    if not defined JAVA_VER set "JAVA_VER=%%i"
)
echo        Found: %JAVA_VER%
echo.

:: ============================================
:: STEP 3: Check MySQL
:: ============================================
echo [3/7] Checking MySQL...
mysql --version >nul 2>&1
if errorlevel 1 (
    echo [WARNING] MySQL client not found in PATH.
    echo          Make sure MySQL Server is running on localhost:3306
    echo          Database: parking_db ^| User: root
    echo.
) else (
    for /f "tokens=*" %%i in ('mysql --version 2^>^&1') do echo        Found: %%i
    echo.
)

:: ============================================
:: STEP 4: Install Python Dependencies
:: ============================================
echo [4/7] Installing Python dependencies...
cd /d "%BACKEND_DIR%"
pip install -r requirements.txt --quiet 2>nul
if errorlevel 1 (
    echo [WARNING] Some dependencies may not have installed correctly.
    echo          Trying to continue anyway...
)
echo        Done.
echo.

:: ============================================
:: STEP 5: Compile Java Files
:: ============================================
echo [5/7] Compiling Java frontend...
if not exist "%JAVA_BUILD%" mkdir "%JAVA_BUILD%"

:: Build list of all Java files
set "JAVA_FILES="
for /r "%JAVA_SRC%" %%f in (*.java) do (
    set "JAVA_FILES=!JAVA_FILES! "%%f""
)

echo        Compiling...
javac -d "%JAVA_BUILD%" -sourcepath "%JAVA_SRC%" !JAVA_FILES! 2>nul
if errorlevel 1 (
    echo [ERROR] Java compilation failed.
    echo          Check for compilation errors above.
    pause
    exit /b 1
)

echo        Compiled successfully.
echo.

:: ============================================
:: STEP 6: Start FastAPI Backend
:: ============================================
echo [6/7] Starting FastAPI backend on port %BACKEND_PORT%...
cd /d "%BACKEND_DIR%"
start "Parking System Backend" cmd /c "python -m uvicorn main:app --host 0.0.0.0 --port %BACKEND_PORT% --reload"

:: Wait for backend to start
echo        Waiting for backend to be ready...
set /a "WAIT_COUNT=0"
set /a "MAX_WAIT=30"

:WAIT_LOOP
if !WAIT_COUNT! geq !MAX_WAIT! (
    echo [WARNING] Backend may not be ready yet. Continuing anyway...
    goto :SKIP_WAIT
)

timeout /t 1 /nobreak >nul
set /a "WAIT_COUNT+=1"

:: Check if backend is responding
curl -s http://localhost:%BACKEND_PORT%/ >nul 2>&1
if errorlevel 1 (
    echo        Still waiting... !WAIT_COUNT!/!MAX_WAIT!
    goto :WAIT_LOOP
) else (
    echo        Backend is ready!
)

:SKIP_WAIT
echo.

:: ============================================
:: STEP 7: Start Java Frontend
:: ============================================
echo [7/7] Starting Java frontend...
cd /d "%FRONTEND_DIR%"
start "Parking System Frontend" cmd /c "java -cp "%JAVA_BUILD%" Main"

echo.
echo ============================================
echo   Application Started Successfully!
echo ============================================
echo.
echo   Backend:  http://localhost:%BACKEND_PORT%
echo   Frontend: Java Swing Application
echo.
echo   Default Login Credentials:
echo     Admin: admin / admin123
echo     Staff: staff / staff123
echo.
echo   To stop: Close the backend and frontend windows
echo ============================================
echo.
pause
