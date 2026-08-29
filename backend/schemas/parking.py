from pydantic import BaseModel, Field, field_validator
from typing import Optional
from datetime import datetime
import re


class ParkingEntry(BaseModel):
    vehicle_number: str = Field(..., min_length=5, max_length=20)
    slot_id: int = Field(..., gt=0)

    @field_validator("vehicle_number")
    @classmethod
    def validate_vehicle_number(cls, v):
        v = v.strip().upper()
        if not re.match(r"^[A-Z0-9]+$", v):
            raise ValueError("Vehicle number must contain only alphanumeric characters")
        return v


class ParkingExit(BaseModel):
    record_id: int = Field(..., gt=0)
    payment_method: str = Field(..., pattern=r"^(CASH|UPI|CARD)$")


class ParkingRecordResponse(BaseModel):
    record_id: int
    vehicle_id: int
    slot_id: int
    entry_time: datetime
    exit_time: Optional[datetime] = None
    duration: Optional[int] = None
    fee: Optional[float] = None
    status: str
    vehicle_number: Optional[str] = None
    slot_number: Optional[str] = None

    class Config:
        from_attributes = True


class ParkingListResponse(BaseModel):
    success: bool
    message: str
    data: list[ParkingRecordResponse] = []


class ParkingEntryResponse(BaseModel):
    success: bool
    message: str
    data: Optional[dict] = None


class ParkingExitResponse(BaseModel):
    success: bool
    message: str
    data: Optional[dict] = None
