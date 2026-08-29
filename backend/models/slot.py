from sqlalchemy import Column, Integer, String
from sqlalchemy.orm import relationship
from database import Base


class ParkingSlot(Base):

    __tablename__ = "parking_slots"

    slot_id = Column(Integer, primary_key=True, index=True)
    slot_number = Column(String(10), unique=True, nullable=False)
    vehicle_type = Column(String(10), nullable=False)
    status = Column(String(10), nullable=False, default="AVAILABLE")

    parking_records = relationship("ParkingRecord", back_populates="slot")
