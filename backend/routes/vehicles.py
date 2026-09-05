import logging
from fastapi import APIRouter, Depends
from sqlalchemy.orm import Session

from database import get_db
from models.vehicle import Vehicle
from schemas.vehicle import VehicleCreate, VehicleResponse, VehicleListResponse

logger = logging.getLogger(__name__)
router = APIRouter()


@router.post("/vehicles", response_model=VehicleListResponse)
def create_vehicle(vehicle: VehicleCreate, db: Session = Depends(get_db)):
    try:
        vehicle_number = vehicle.vehicle_number.upper().strip()

        existing = db.query(Vehicle).filter(Vehicle.vehicle_number == vehicle_number).first()
        if existing:
            return VehicleListResponse(success=False, message="Vehicle number already exists")

        new_vehicle = Vehicle(
            vehicle_number=vehicle_number,
            vehicle_type=vehicle.vehicle_type.upper(),
            owner_name=vehicle.owner_name,
            owner_phone=vehicle.owner_phone,
        )
        db.add(new_vehicle)
        db.commit()
        db.refresh(new_vehicle)

        return VehicleListResponse(
            success=True,
            message="Vehicle added successfully",
            data=[VehicleResponse.model_validate(new_vehicle)],
        )
    except Exception as e:
        logger.error("Create vehicle error: %s", str(e))
        return VehicleListResponse(success=False, message="An error occurred while adding the vehicle")


@router.get("/vehicles", response_model=VehicleListResponse)
def get_vehicles(db: Session = Depends(get_db)):
    try:
        vehicles = db.query(Vehicle).all()
        return VehicleListResponse(
            success=True,
            message="Vehicles retrieved successfully",
            data=[VehicleResponse.model_validate(v) for v in vehicles],
        )
    except Exception as e:
        logger.error("Get vehicles error: %s", str(e))
        return VehicleListResponse(success=False, message="An error occurred while retrieving vehicles")


@router.get("/vehicles/{vehicle_id}", response_model=VehicleListResponse)
def get_vehicle(vehicle_id: int, db: Session = Depends(get_db)):
    try:
        vehicle = db.query(Vehicle).filter(Vehicle.vehicle_id == vehicle_id).first()
        if not vehicle:
            return VehicleListResponse(success=False, message="Vehicle not found")

        return VehicleListResponse(
            success=True,
            message="Vehicle retrieved successfully",
            data=[VehicleResponse.model_validate(vehicle)],
        )
    except Exception as e:
        logger.error("Get vehicle error: %s", str(e))
        return VehicleListResponse(success=False, message="An error occurred while retrieving the vehicle")


@router.get("/vehicles/search/{vehicle_number}", response_model=VehicleListResponse)
def search_vehicle(vehicle_number: str, db: Session = Depends(get_db)):
    try:
        vehicle_number = vehicle_number.upper().strip()
        vehicles = db.query(Vehicle).filter(Vehicle.vehicle_number.contains(vehicle_number)).all()

        if not vehicles:
            return VehicleListResponse(success=False, message="No vehicles found")

        return VehicleListResponse(
            success=True,
            message="Vehicles found",
            data=[VehicleResponse.model_validate(v) for v in vehicles],
        )
    except Exception as e:
        logger.error("Search vehicle error: %s", str(e))
        return VehicleListResponse(success=False, message="An error occurred while searching for vehicles")


@router.delete("/vehicles/{vehicle_id}", response_model=VehicleListResponse)
def delete_vehicle(vehicle_id: int, db: Session = Depends(get_db)):
    try:
        vehicle = db.query(Vehicle).filter(Vehicle.vehicle_id == vehicle_id).first()
        if not vehicle:
            return VehicleListResponse(success=False, message="Vehicle not found")

        from models.parking import ParkingRecord
        active = db.query(ParkingRecord).filter(
            ParkingRecord.vehicle_id == vehicle_id,
            ParkingRecord.status == "PARKED"
        ).first()
        if active:
            return VehicleListResponse(success=False, message="Cannot delete vehicle with active parking record")

        db.query(ParkingRecord).filter(ParkingRecord.vehicle_id == vehicle_id).delete()
        db.delete(vehicle)
        db.commit()
        return VehicleListResponse(success=True, message="Vehicle deleted successfully")
    except Exception as e:
        logger.error("Delete vehicle error: %s", str(e))
        return VehicleListResponse(success=False, message="An error occurred while deleting the vehicle")
