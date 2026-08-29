from datetime import datetime
from sqlalchemy.orm import Session

from models.vehicle import Vehicle
from models.slot import ParkingSlot
from models.parking import ParkingRecord
from models.payment import Payment
from services.fee_service import calculate_fee


def park_vehicle(db: Session, vehicle_number: str, slot_id: int):
    vehicle_number = vehicle_number.upper().strip()

    vehicle = db.query(Vehicle).filter(Vehicle.vehicle_number == vehicle_number).first()
    if not vehicle:
        return {"success": False, "message": "Vehicle not found"}

    active_record = (
        db.query(ParkingRecord)
        .filter(ParkingRecord.vehicle_id == vehicle.vehicle_id, ParkingRecord.status == "PARKED")
        .first()
    )
    if active_record:
        return {"success": False, "message": "Vehicle is already parked"}

    slot = db.query(ParkingSlot).filter(ParkingSlot.slot_id == slot_id).first()
    if not slot:
        return {"success": False, "message": "Parking slot not found"}

    if slot.status == "OCCUPIED":
        return {"success": False, "message": "Slot is already occupied"}

    if slot.vehicle_type != vehicle.vehicle_type:
        return {"success": False, "message": "Vehicle type does not match slot type"}

    entry_time = datetime.now()
    record = ParkingRecord(
        vehicle_id=vehicle.vehicle_id,
        slot_id=slot.slot_id,
        entry_time=entry_time,
        status="PARKED",
    )
    slot.status = "OCCUPIED"

    db.add(record)
    db.commit()
    db.refresh(record)

    return {
        "success": True,
        "message": "Vehicle parked successfully",
        "data": {
            "record_id": record.record_id,
            "vehicle_number": vehicle.vehicle_number,
            "slot_number": slot.slot_number,
            "entry_time": entry_time.isoformat(),
        },
    }


def exit_vehicle(db: Session, record_id: int, payment_method: str):
    record = db.query(ParkingRecord).filter(ParkingRecord.record_id == record_id).first()
    if not record:
        return {"success": False, "message": "Parking record not found"}

    if record.status == "COMPLETED":
        return {"success": False, "message": "Parking session already completed"}

    vehicle = db.query(Vehicle).filter(Vehicle.vehicle_id == record.vehicle_id).first()
    slot = db.query(ParkingSlot).filter(ParkingSlot.slot_id == record.slot_id).first()

    exit_time = datetime.now()
    duration_minutes = int((exit_time - record.entry_time).total_seconds() / 60)

    if duration_minutes < 1:
        duration_minutes = 1

    fee = calculate_fee(vehicle.vehicle_type, duration_minutes)

    record.exit_time = exit_time
    record.duration = duration_minutes
    record.fee = fee
    record.status = "COMPLETED"
    slot.status = "AVAILABLE"

    payment = Payment(
        record_id=record.record_id,
        amount=fee,
        payment_method=payment_method.upper(),
        payment_time=exit_time,
        status="PAID",
    )
    db.add(payment)
    db.commit()

    return {
        "success": True,
        "message": "Vehicle exit processed successfully",
        "data": {
            "record_id": record.record_id,
            "vehicle_number": vehicle.vehicle_number,
            "slot_number": slot.slot_number,
            "entry_time": record.entry_time.isoformat(),
            "exit_time": exit_time.isoformat(),
            "duration_minutes": duration_minutes,
            "fee": fee,
            "payment_method": payment_method,
        },
    }


def get_active_parking_records(db: Session):
    records = (
        db.query(ParkingRecord)
        .filter(ParkingRecord.status == "PARKED")
        .all()
    )

    result = []
    for record in records:
        vehicle = db.query(Vehicle).filter(Vehicle.vehicle_id == record.vehicle_id).first()
        slot = db.query(ParkingSlot).filter(ParkingSlot.slot_id == record.slot_id).first()
        result.append({
            "record_id": record.record_id,
            "vehicle_id": record.vehicle_id,
            "vehicle_number": vehicle.vehicle_number if vehicle else "",
            "vehicle_type": vehicle.vehicle_type if vehicle else "",
            "slot_id": record.slot_id,
            "slot_number": slot.slot_number if slot else "",
            "entry_time": record.entry_time.isoformat(),
            "status": record.status,
        })

    return result


def get_parking_history(db: Session):
    records = (
        db.query(ParkingRecord)
        .filter(ParkingRecord.status == "COMPLETED")
        .order_by(ParkingRecord.record_id.desc())
        .all()
    )

    result = []
    for record in records:
        vehicle = db.query(Vehicle).filter(Vehicle.vehicle_id == record.vehicle_id).first()
        slot = db.query(ParkingSlot).filter(ParkingSlot.slot_id == record.slot_id).first()
        payment = db.query(Payment).filter(Payment.record_id == record.record_id).first()
        result.append({
            "record_id": record.record_id,
            "vehicle_number": vehicle.vehicle_number if vehicle else "",
            "vehicle_type": vehicle.vehicle_type if vehicle else "",
            "slot_number": slot.slot_number if slot else "",
            "entry_time": record.entry_time.isoformat(),
            "exit_time": record.exit_time.isoformat() if record.exit_time else "",
            "duration_minutes": record.duration or 0,
            "fee": record.fee or 0.0,
            "payment_status": payment.status if payment else "PENDING",
            "payment_method": payment.payment_method if payment else "",
        })

    return result


def get_dashboard_stats(db: Session):
    total_slots = db.query(ParkingSlot).count()
    available_slots = db.query(ParkingSlot).filter(ParkingSlot.status == "AVAILABLE").count()
    occupied_slots = db.query(ParkingSlot).filter(ParkingSlot.status == "OCCUPIED").count()
    active_vehicles = db.query(ParkingRecord).filter(ParkingRecord.status == "PARKED").count()

    today_start = datetime.now().replace(hour=0, minute=0, second=0, microsecond=0)
    today_revenue = (
        db.query(Payment)
        .filter(Payment.status == "PAID", Payment.payment_time >= today_start)
        .count()
    )

    today_revenue_amount = 0.0
    today_payments = (
        db.query(Payment)
        .filter(Payment.status == "PAID", Payment.payment_time >= today_start)
        .all()
    )
    for p in today_payments:
        today_revenue_amount += p.amount

    return {
        "total_slots": total_slots,
        "available_slots": available_slots,
        "occupied_slots": occupied_slots,
        "active_vehicles": active_vehicles,
        "today_revenue": today_revenue_amount,
    }
