from sqlalchemy import Column, Integer, String
from sqlalchemy.orm import relationship
from database import Base


class Vehicle(Base):

    __tablename__ = "vehicles"

    vehicle_id = Column(Integer, primary_key=True, index=True)
    vehicle_number = Column(String(20), unique=True, nullable=False)
    vehicle_type = Column(String(10), nullable=False)
    owner_name = Column(String(100), nullable=False)
    owner_phone = Column(String(15), nullable=False)

    parking_records = relationship("ParkingRecord", back_populates="vehicle")
