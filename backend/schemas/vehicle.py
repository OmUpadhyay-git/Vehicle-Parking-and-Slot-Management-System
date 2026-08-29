from pydantic import BaseModel, Field, field_validator
from typing import Optional
import re


class VehicleCreate(BaseModel):
    vehicle_number: str = Field(..., min_length=5, max_length=20)
    vehicle_type: str = Field(..., pattern=r"^(CAR|BIKE)$")
    owner_name: str = Field(..., min_length=2, max_length=100, pattern=r"^[a-zA-Z\s]+$")
    owner_phone: str = Field(..., min_length=10, max_length=15, pattern=r"^[0-9]+$")

    @field_validator("vehicle_number")
    @classmethod
    def validate_vehicle_number(cls, v):
        v = v.strip().upper()
        if not re.match(r"^[A-Z0-9]+$", v):
            raise ValueError("Vehicle number must contain only alphanumeric characters")
        return v

    @field_validator("owner_name")
    @classmethod
    def validate_owner_name(cls, v):
        v = v.strip()
        if not re.match(r"^[a-zA-Z\s]+$", v):
            raise ValueError("Owner name must contain only letters and spaces")
        return v

    @field_validator("owner_phone")
    @classmethod
    def validate_owner_phone(cls, v):
        v = v.strip()
        if not v.isdigit():
            raise ValueError("Phone number must contain only digits")
        return v


class VehicleResponse(BaseModel):
    vehicle_id: int
    vehicle_number: str
    vehicle_type: str
    owner_name: str
    owner_phone: str

    class Config:
        from_attributes = True


class VehicleListResponse(BaseModel):
    success: bool
    message: str
    data: list[VehicleResponse] = []
