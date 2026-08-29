from pydantic import BaseModel, Field, field_validator
from typing import Optional
import re


class SlotCreate(BaseModel):
    slot_number: str = Field(..., min_length=1, max_length=10, pattern=r"^[A-Z0-9]+$")
    vehicle_type: str = Field(..., pattern=r"^(CAR|BIKE)$")
    status: str = Field(default="AVAILABLE", pattern=r"^(AVAILABLE|OCCUPIED)$")

    @field_validator("slot_number")
    @classmethod
    def validate_slot_number(cls, v):
        v = v.strip().upper()
        if not re.match(r"^[A-Z0-9]+$", v):
            raise ValueError("Slot number must contain only alphanumeric characters")
        return v


class SlotResponse(BaseModel):
    slot_id: int
    slot_number: str
    vehicle_type: str
    status: str

    class Config:
        from_attributes = True


class SlotUpdate(BaseModel):
    status: Optional[str] = Field(None, pattern=r"^(AVAILABLE|OCCUPIED)$")


class SlotListResponse(BaseModel):
    success: bool
    message: str
    data: list[SlotResponse] = []
