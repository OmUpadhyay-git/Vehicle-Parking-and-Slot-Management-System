from pydantic import BaseModel, Field, field_validator
from typing import Optional
from datetime import datetime


class PaymentCreate(BaseModel):
    record_id: int = Field(..., gt=0)
    amount: float = Field(..., gt=0, le=100000)
    payment_method: str = Field(..., pattern=r"^(CASH|UPI|CARD)$")

    @field_validator("amount")
    @classmethod
    def validate_amount(cls, v):
        if v <= 0:
            raise ValueError("Payment amount must be greater than zero")
        if round(v, 2) != v:
            raise ValueError("Payment amount must have at most 2 decimal places")
        return round(v, 2)


class PaymentResponse(BaseModel):
    payment_id: int
    record_id: int
    amount: float
    payment_method: str
    payment_time: datetime
    status: str

    class Config:
        from_attributes = True


class PaymentListResponse(BaseModel):
    success: bool
    message: str
    data: list[PaymentResponse] = []


class PaymentCreateResponse(BaseModel):
    success: bool
    message: str
    data: Optional[PaymentResponse] = None
