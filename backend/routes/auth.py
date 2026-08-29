import logging
from fastapi import APIRouter, Depends
from sqlalchemy.orm import Session

from database import get_db
from models.user import User
from schemas.user import UserLogin, UserResponse, LoginResponse
from services.auth_service import verify_password

logger = logging.getLogger(__name__)
router = APIRouter()


@router.post("/login", response_model=LoginResponse)
def login(credentials: UserLogin, db: Session = Depends(get_db)):
    try:
        user = db.query(User).filter(User.username == credentials.username).first()

        if not user or not verify_password(credentials.password, user.password):
            return LoginResponse(success=False, message="Invalid username or password")

        return LoginResponse(
            success=True,
            message="Login successful",
            data=UserResponse(
                user_id=user.user_id,
                name=user.name,
                username=user.username,
                role=user.role,
            ),
        )
    except Exception as e:
        logger.error("Login error: %s", str(e))
        return LoginResponse(success=False, message="An error occurred during login")
