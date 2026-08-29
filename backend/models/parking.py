from sqlalchemy import Column, Integer, DateTime, Float, String, ForeignKey
from sqlalchemy.orm import relationship
from database import Base


class ParkingRecord(Base):

    __tablename__ = "parking_records"

    record_id = Column(Integer, primary_key=True, index=True)
    vehicle_id = Column(Integer, ForeignKey("vehicles.vehicle_id"), nullable=False)
    slot_id = Column(Integer, ForeignKey("parking_slots.slot_id"), nullable=False)
    entry_time = Column(DateTime, nullable=False)
    exit_time = Column(DateTime, nullable=True)
    duration = Column(Integer, nullable=True)
    fee = Column(Float, nullable=True)
    status = Column(String(10), nullable=False, default="PARKED")

    vehicle = relationship("Vehicle", back_populates="parking_records")
    slot = relationship("ParkingSlot", back_populates="parking_records")
    payment = relationship("Payment", back_populates="parking_record", uselist=False)
