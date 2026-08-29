import logging
from fastapi import APIRouter, Depends
from sqlalchemy.orm import Session

from database import get_db
from services.parking_service import get_dashboard_stats

logger = logging.getLogger(__name__)
router = APIRouter()


@router.get("/dashboard")
def get_dashboard(db: Session = Depends(get_db)):
    try:
        stats = get_dashboard_stats(db)
        return {
            "success": True,
            "message": "Dashboard data retrieved successfully",
            "data": stats,
        }
    except Exception as e:
        logger.error("Dashboard error: %s", str(e))
        return {
            "success": False,
            "message": "An error occurred while retrieving dashboard data",
        }
