import logging
from fastapi import APIRouter, Depends
from sqlalchemy.orm import Session

from database import get_db
from models.user import User
from schemas.user import (
    UserListResponse, UserListItem, PasswordChange, PasswordChangeResponse,
    UserCreate, UserCreateResponse, UserDeleteResponse,
)
from services.auth_service import hash_password

logger = logging.getLogger(__name__)
router = APIRouter()


@router.get("/users", response_model=UserListResponse)
def get_users(db: Session = Depends(get_db)):
    try:
        users = db.query(User).all()
        items = []
        for user in users:
            items.append(UserListItem(
                user_id=user.user_id,
                name=user.name,
                username=user.username,
                role=user.role,
                last_login=user.last_login,
            ))
        return UserListResponse(
            success=True,
            message="Users retrieved successfully",
            data=items,
        )
    except Exception as e:
        logger.error("Get users error: %s", str(e))
        return UserListResponse(success=False, message="An error occurred while retrieving users")


@router.post("/users", response_model=UserCreateResponse)
def create_user(body: UserCreate, db: Session = Depends(get_db)):
    try:
        existing = db.query(User).filter(User.username == body.username).first()
        if existing:
            return UserCreateResponse(success=False, message="Username already exists")

        user = User(
            name=body.name,
            username=body.username,
            password=hash_password(body.password),
            role=body.role,
        )
        db.add(user)
        db.commit()

        return UserCreateResponse(success=True, message="User created successfully")
    except Exception as e:
        logger.error("Create user error: %s", str(e))
        return UserCreateResponse(success=False, message="An error occurred while creating user")


@router.put("/users/{user_id}/password", response_model=PasswordChangeResponse)
def change_password(user_id: int, body: PasswordChange, db: Session = Depends(get_db)):
    try:
        user = db.query(User).filter(User.user_id == user_id).first()
        if not user:
            return PasswordChangeResponse(success=False, message="User not found")

        user.password = hash_password(body.new_password)
        db.commit()

        return PasswordChangeResponse(
            success=True,
            message="Password updated successfully",
        )
    except Exception as e:
        logger.error("Change password error: %s", str(e))
        return PasswordChangeResponse(success=False, message="An error occurred while updating password")


@router.delete("/users/{user_id}", response_model=UserDeleteResponse)
def delete_user(user_id: int, db: Session = Depends(get_db)):
    try:
        user = db.query(User).filter(User.user_id == user_id).first()
        if not user:
            return UserDeleteResponse(success=False, message="User not found")

        if user.role == "ADMIN":
            admin_count = db.query(User).filter(User.role == "ADMIN").count()
            if admin_count <= 1:
                return UserDeleteResponse(success=False, message="Cannot delete the last admin user")

        db.delete(user)
        db.commit()

        return UserDeleteResponse(success=True, message="User deleted successfully")
    except Exception as e:
        logger.error("Delete user error: %s", str(e))
        return UserDeleteResponse(success=False, message="An error occurred while deleting user")
