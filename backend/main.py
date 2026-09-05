import logging
from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from sqlalchemy.orm import Session

from database import engine, Base, get_db
from routes import auth, vehicles, slots, parking, payments, dashboard, users
from models.user import User
from models.slot import ParkingSlot
from services.auth_service import hash_password

logging.basicConfig(
    level=logging.INFO,
    format="%(asctime)s - %(name)s - %(levelname)s - %(message)s",
)

Base.metadata.create_all(bind=engine)

logger = logging.getLogger(__name__)


def seed_data():
    db: Session = next(get_db())
    try:
        if db.query(User).count() == 0:
            logger.info("Seeding default users...")
            db.add(User(name="Administrator", username="admin", password=hash_password("admin123"), role="ADMIN"))
            db.add(User(name="Staff Member", username="staff", password=hash_password("staff123"), role="STAFF"))
            db.add(User(name="General User", username="om", password=hash_password("om"), role="STAFF"))
            db.commit()
            logger.info("Default users created: admin/admin123, staff/staff123, om/om")

        if db.query(ParkingSlot).count() == 0:
            logger.info("Seeding parking slots...")
            slots_list = []
            for i in range(1, 11):
                slots_list.append(ParkingSlot(slot_number=f"A{i}", vehicle_type="CAR", status="AVAILABLE"))
                slots_list.append(ParkingSlot(slot_number=f"B{i}", vehicle_type="BIKE", status="AVAILABLE"))
            db.add_all(slots_list)
            db.commit()
            logger.info("20 parking slots created (10 CAR, 10 BIKE)")
    except Exception as e:
        logger.error("Seed data error: %s", str(e))
        db.rollback()
    finally:
        db.close()


seed_data()

app = FastAPI(title="Vehicle Parking System API")

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

app.include_router(auth.router)
app.include_router(vehicles.router)
app.include_router(slots.router)
app.include_router(parking.router)
app.include_router(payments.router)
app.include_router(dashboard.router)
app.include_router(users.router)


@app.get("/")
def root():
    return {"message": "Vehicle Parking System API"}
