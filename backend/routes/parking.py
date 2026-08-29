import logging
from fastapi import APIRouter, Depends
from sqlalchemy.orm import Session

from database import get_db
from schemas.parking import (
    ParkingEntry,
    ParkingExit,
    ParkingListResponse,
    ParkingEntryResponse,
    ParkingExitResponse,
    ParkingRecordResponse,
)
from services.parking_service import (
    park_vehicle,
    exit_vehicle,
    get_active_parking_records,
    get_parking_history,
)

logger = logging.getLogger(__name__)
router = APIRouter()


@router.post("/parking/entry", response_model=ParkingEntryResponse)
def park_vehicle_endpoint(entry: ParkingEntry, db: Session = Depends(get_db)):
    try:
        result = park_vehicle(db, entry.vehicle_number, entry.slot_id)
        if not result["success"]:
            return ParkingEntryResponse(success=False, message=result["message"])

        return ParkingEntryResponse(
            success=True,
            message=result["message"],
            data=result.get("data"),
        )
    except Exception as e:
        logger.error("Park vehicle error: %s", str(e))
        return ParkingEntryResponse(success=False, message="An error occurred while parking the vehicle")


@router.post("/parking/exit", response_model=ParkingExitResponse)
def exit_vehicle_endpoint(exit_req: ParkingExit, db: Session = Depends(get_db)):
    try:
        result = exit_vehicle(db, exit_req.record_id, exit_req.payment_method)
        if not result["success"]:
            return ParkingExitResponse(success=False, message=result["message"])

        return ParkingExitResponse(
            success=True,
            message=result["message"],
            data=result.get("data"),
        )
    except Exception as e:
        logger.error("Exit vehicle error: %s", str(e))
        return ParkingExitResponse(success=False, message="An error occurred while processing vehicle exit")


@router.get("/parking/active", response_model=ParkingListResponse)
def get_active_parking(db: Session = Depends(get_db)):
    try:
        records = get_active_parking_records(db)
        return ParkingListResponse(
            success=True,
            message="Active parking records retrieved successfully",
            data=[ParkingRecordResponse(**r) for r in records],
        )
    except Exception as e:
        logger.error("Get active parking error: %s", str(e))
        return ParkingListResponse(success=False, message="An error occurred while retrieving active parking records")


@router.get("/parking/history", response_model=ParkingListResponse)
def get_history(db: Session = Depends(get_db)):
    try:
        records = get_parking_history(db)
        return ParkingListResponse(
            success=True,
            message="Parking history retrieved successfully",
            data=[ParkingRecordResponse(**r) for r in records],
        )
    except Exception as e:
        logger.error("Get parking history error: %s", str(e))
        return ParkingListResponse(success=False, message="An error occurred while retrieving parking history")
