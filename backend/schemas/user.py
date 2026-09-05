from pydantic import BaseModel, Field, field_validator
from typing import Optional
from datetime import datetime


class UserLogin(BaseModel):
    username: str = Field(..., min_length=2, max_length=50, pattern=r"^[a-zA-Z0-9_]+$")
    password: str = Field(..., min_length=2, max_length=100)

    @field_validator("username")
    @classmethod
    def strip_username(cls, v):
        return v.strip()

    @field_validator("password")
    @classmethod
    def strip_password(cls, v):
        return v.strip()


class UserResponse(BaseModel):
    user_id: int
    name: str
    username: str
    role: str

    class Config:
        from_attributes = True


class LoginResponse(BaseModel):
    success: bool
    message: str
    data: Optional[UserResponse] = None


class UserListItem(BaseModel):
    user_id: int
    name: str
    username: str
    role: str
    last_login: Optional[datetime] = None

    class Config:
        from_attributes = True


class UserListResponse(BaseModel):
    success: bool
    message: str
    data: list[UserListItem] = []


class PasswordChange(BaseModel):
    new_password: str = Field(..., min_length=2, max_length=100)


class PasswordChangeResponse(BaseModel):
    success: bool
    message: str


class UserCreate(BaseModel):
    name: str = Field(..., min_length=2, max_length=100)
    username: str = Field(..., min_length=2, max_length=50, pattern=r"^[a-zA-Z0-9_]+$")
    password: str = Field(..., min_length=2, max_length=100)
    role: str = Field(..., pattern=r"^(ADMIN|STAFF)$")

    @field_validator("name")
    @classmethod
    def strip_name(cls, v):
        return v.strip()

    @field_validator("username")
    @classmethod
    def strip_username(cls, v):
        return v.strip()


class UserCreateResponse(BaseModel):
    success: bool
    message: str


class UserDeleteResponse(BaseModel):
    success: bool
    message: str
