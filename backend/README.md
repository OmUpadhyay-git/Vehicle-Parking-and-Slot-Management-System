# Backend - Vehicle Parking System

Python FastAPI backend for the Vehicle Parking and Slot Management System.

## Structure

```
backend/
├── main.py
├── database.py
├── requirements.txt
├── .env.example
├── models/
│   ├── __init__.py
│   ├── user.py
│   ├── vehicle.py
│   ├── slot.py
│   ├── parking.py
│   └── payment.py
├── schemas/
│   ├── __init__.py
│   ├── user.py
│   ├── vehicle.py
│   ├── slot.py
│   ├── parking.py
│   └── payment.py
├── routes/
│   ├── __init__.py
│   ├── auth.py
│   ├── vehicles.py
│   ├── slots.py
│   ├── parking.py
│   ├── payments.py
│   └── dashboard.py
└── services/
    ├── __init__.py
    ├── parking_service.py
    └── fee_service.py
```

## Technology

- Language: Python
- Framework: FastAPI
- ORM: SQLAlchemy
- Database: MySQL

## Status

Project structure created. Application features not yet implemented.
