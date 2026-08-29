import logging
from fastapi import APIRouter, Depends
from sqlalchemy.orm import Session
from datetime import datetime

from database import get_db
from models.payment import Payment
from models.parking import ParkingRecord
from schemas.payment import PaymentCreate, PaymentResponse, PaymentListResponse, PaymentCreateResponse

logger = logging.getLogger(__name__)
router = APIRouter()


@router.post("/payments", response_model=PaymentCreateResponse)
def create_payment(payment: PaymentCreate, db: Session = Depends(get_db)):
    try:
        record = db.query(ParkingRecord).filter(ParkingRecord.record_id == payment.record_id).first()
        if not record:
            return PaymentCreateResponse(success=False, message="Parking record not found")

        existing = db.query(Payment).filter(Payment.record_id == payment.record_id).first()
        if existing:
            return PaymentCreateResponse(success=False, message="Payment already exists for this record")

        new_payment = Payment(
            record_id=payment.record_id,
            amount=payment.amount,
            payment_method=payment.payment_method.upper(),
            payment_time=datetime.now(),
            status="PAID",
        )
        db.add(new_payment)
        db.commit()
        db.refresh(new_payment)

        return PaymentCreateResponse(
            success=True,
            message="Payment recorded successfully",
            data=PaymentResponse.model_validate(new_payment),
        )
    except Exception as e:
        logger.error("Create payment error: %s", str(e))
        return PaymentCreateResponse(success=False, message="An error occurred while recording the payment")


@router.get("/payments", response_model=PaymentListResponse)
def get_payments(db: Session = Depends(get_db)):
    try:
        payments = db.query(Payment).all()
        return PaymentListResponse(
            success=True,
            message="Payments retrieved successfully",
            data=[PaymentResponse.model_validate(p) for p in payments],
        )
    except Exception as e:
        logger.error("Get payments error: %s", str(e))
        return PaymentListResponse(success=False, message="An error occurred while retrieving payments")


@router.get("/payments/{payment_id}", response_model=PaymentListResponse)
def get_payment(payment_id: int, db: Session = Depends(get_db)):
    try:
        payment = db.query(Payment).filter(Payment.payment_id == payment_id).first()
        if not payment:
            return PaymentListResponse(success=False, message="Payment not found")

        return PaymentListResponse(
            success=True,
            message="Payment retrieved successfully",
            data=[PaymentResponse.model_validate(payment)],
        )
    except Exception as e:
        logger.error("Get payment error: %s", str(e))
        return PaymentListResponse(success=False, message="An error occurred while retrieving the payment")
