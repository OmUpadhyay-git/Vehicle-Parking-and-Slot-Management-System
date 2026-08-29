from sqlalchemy import Column, Integer, DateTime, Float, String, ForeignKey
from sqlalchemy.orm import relationship
from database import Base


class Payment(Base):

    __tablename__ = "payments"

    payment_id = Column(Integer, primary_key=True, index=True)
    record_id = Column(Integer, ForeignKey("parking_records.record_id"), unique=True, nullable=False)
    amount = Column(Float, nullable=False)
    payment_method = Column(String(10), nullable=False)
    payment_time = Column(DateTime, nullable=False)
    status = Column(String(10), nullable=False, default="PAID")

    parking_record = relationship("ParkingRecord", back_populates="payment")
