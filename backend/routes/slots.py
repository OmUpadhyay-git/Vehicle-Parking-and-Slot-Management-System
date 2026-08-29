import logging
from fastapi import APIRouter, Depends
from sqlalchemy.orm import Session

from database import get_db
from models.slot import ParkingSlot
from schemas.slot import SlotCreate, SlotResponse, SlotUpdate, SlotListResponse

logger = logging.getLogger(__name__)
router = APIRouter()


@router.post("/slots", response_model=SlotListResponse)
def create_slot(slot: SlotCreate, db: Session = Depends(get_db)):
    try:
        slot_number = slot.slot_number.upper().strip()

        existing = db.query(ParkingSlot).filter(ParkingSlot.slot_number == slot_number).first()
        if existing:
            return SlotListResponse(success=False, message="Slot number already exists")

        new_slot = ParkingSlot(
            slot_number=slot_number,
            vehicle_type=slot.vehicle_type.upper(),
            status="AVAILABLE",
        )
        db.add(new_slot)
        db.commit()
        db.refresh(new_slot)

        return SlotListResponse(
            success=True,
            message="Slot created successfully",
            data=[SlotResponse.model_validate(new_slot)],
        )
    except Exception as e:
        logger.error("Create slot error: %s", str(e))
        return SlotListResponse(success=False, message="An error occurred while creating the slot")


@router.get("/slots", response_model=SlotListResponse)
def get_slots(db: Session = Depends(get_db)):
    try:
        slots = db.query(ParkingSlot).all()
        return SlotListResponse(
            success=True,
            message="Slots retrieved successfully",
            data=[SlotResponse.model_validate(s) for s in slots],
        )
    except Exception as e:
        logger.error("Get slots error: %s", str(e))
        return SlotListResponse(success=False, message="An error occurred while retrieving slots")


@router.get("/slots/available", response_model=SlotListResponse)
def get_available_slots(db: Session = Depends(get_db)):
    try:
        slots = db.query(ParkingSlot).filter(ParkingSlot.status == "AVAILABLE").all()
        return SlotListResponse(
            success=True,
            message="Available slots retrieved successfully",
            data=[SlotResponse.model_validate(s) for s in slots],
        )
    except Exception as e:
        logger.error("Get available slots error: %s", str(e))
        return SlotListResponse(success=False, message="An error occurred while retrieving available slots")


@router.get("/slots/occupied", response_model=SlotListResponse)
def get_occupied_slots(db: Session = Depends(get_db)):
    try:
        slots = db.query(ParkingSlot).filter(ParkingSlot.status == "OCCUPIED").all()
        return SlotListResponse(
            success=True,
            message="Occupied slots retrieved successfully",
            data=[SlotResponse.model_validate(s) for s in slots],
        )
    except Exception as e:
        logger.error("Get occupied slots error: %s", str(e))
        return SlotListResponse(success=False, message="An error occurred while retrieving occupied slots")


@router.put("/slots/{slot_id}", response_model=SlotListResponse)
def update_slot(slot_id: int, update: SlotUpdate, db: Session = Depends(get_db)):
    try:
        slot = db.query(ParkingSlot).filter(ParkingSlot.slot_id == slot_id).first()
        if not slot:
            return SlotListResponse(success=False, message="Slot not found")

        if update.status:
            slot.status = update.status

        db.commit()
        db.refresh(slot)

        return SlotListResponse(
            success=True,
            message="Slot updated successfully",
            data=[SlotResponse.model_validate(slot)],
        )
    except Exception as e:
        logger.error("Update slot error: %s", str(e))
        return SlotListResponse(success=False, message="An error occurred while updating the slot")
