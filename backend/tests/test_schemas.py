import sys
import os
sys.path.insert(0, os.path.join(os.path.dirname(__file__), ".."))

import pytest
from pydantic import ValidationError
from schemas.vehicle import VehicleCreate
from schemas.slot import SlotCreate
from schemas.parking import ParkingEntry, ParkingExit
from schemas.payment import PaymentCreate


class TestVehicleCreateSchema:
    def test_valid_car(self):
        v = VehicleCreate(vehicle_number="KA01AB1234", vehicle_type="CAR", owner_name="Raj Kumar", owner_phone="9876543210")
        assert v.vehicle_number == "KA01AB1234"

    def test_valid_bike(self):
        v = VehicleCreate(vehicle_number="KA01CD5678", vehicle_type="BIKE", owner_name="Priya Singh", owner_phone="9876543211")
        assert v.vehicle_type == "BIKE"

    def test_invalid_vehicle_type(self):
        with pytest.raises(ValidationError):
            VehicleCreate(vehicle_number="KA01AB1234", vehicle_type="TRUCK", owner_name="Test", owner_phone="1234567890")

    def test_short_vehicle_number(self):
        with pytest.raises(ValidationError):
            VehicleCreate(vehicle_number="AB", vehicle_type="CAR", owner_name="Test", owner_phone="1234567890")

    def test_invalid_vehicle_number_chars(self):
        with pytest.raises(ValidationError):
            VehicleCreate(vehicle_number="KA-01-AB", vehicle_type="CAR", owner_name="Test", owner_phone="1234567890")

    def test_short_owner_name(self):
        with pytest.raises(ValidationError):
            VehicleCreate(vehicle_number="KA01AB1234", vehicle_type="CAR", owner_name="A", owner_phone="1234567890")

    def test_invalid_owner_name_chars(self):
        with pytest.raises(ValidationError):
            VehicleCreate(vehicle_number="KA01AB1234", vehicle_type="CAR", owner_name="Raj123", owner_phone="1234567890")

    def test_short_phone(self):
        with pytest.raises(ValidationError):
            VehicleCreate(vehicle_number="KA01AB1234", vehicle_type="CAR", owner_name="Test User", owner_phone="12345")

    def test_invalid_phone_chars(self):
        with pytest.raises(ValidationError):
            VehicleCreate(vehicle_number="KA01AB1234", vehicle_type="CAR", owner_name="Test User", owner_phone="12345abcde")

    def test_vehicle_number_stripped_uppered(self):
        v = VehicleCreate(vehicle_number="  ka01ab1234  ", vehicle_type="CAR", owner_name="Test User", owner_phone="1234567890")
        assert v.vehicle_number == "KA01AB1234"


class TestSlotCreateSchema:
    def test_valid_car_slot(self):
        s = SlotCreate(slot_number="A1", vehicle_type="CAR")
        assert s.slot_number == "A1"
        assert s.status == "AVAILABLE"

    def test_valid_bike_slot(self):
        s = SlotCreate(slot_number="B1", vehicle_type="BIKE")
        assert s.vehicle_type == "BIKE"

    def test_invalid_vehicle_type(self):
        with pytest.raises(ValidationError):
            SlotCreate(slot_number="C1", vehicle_type="TRUCK")

    def test_empty_slot_number(self):
        with pytest.raises(ValidationError):
            SlotCreate(slot_number="", vehicle_type="CAR")


class TestParkingEntrySchema:
    def test_valid_entry(self):
        e = ParkingEntry(vehicle_number="KA01AB1234", slot_id=1)
        assert e.slot_id == 1

    def test_invalid_slot_id(self):
        with pytest.raises(ValidationError):
            ParkingEntry(vehicle_number="KA01AB1234", slot_id=0)

    def test_invalid_vehicle_number(self):
        with pytest.raises(ValidationError):
            ParkingEntry(vehicle_number="AB", slot_id=1)


class TestParkingExitSchema:
    def test_valid_exit(self):
        e = ParkingExit(record_id=1, payment_method="CASH")
        assert e.payment_method == "CASH"

    def test_valid_upi(self):
        e = ParkingExit(record_id=1, payment_method="UPI")
        assert e.payment_method == "UPI"

    def test_valid_card(self):
        e = ParkingExit(record_id=1, payment_method="CARD")
        assert e.payment_method == "CARD"

    def test_invalid_payment_method(self):
        with pytest.raises(ValidationError):
            ParkingExit(record_id=1, payment_method="NETBANKING")

    def test_invalid_record_id(self):
        with pytest.raises(ValidationError):
            ParkingExit(record_id=0, payment_method="CASH")


class TestPaymentCreateSchema:
    def test_valid_payment(self):
        p = PaymentCreate(record_id=1, amount=40.0, payment_method="CASH")
        assert p.amount == 40.0

    def test_zero_amount(self):
        with pytest.raises(ValidationError):
            PaymentCreate(record_id=1, amount=0, payment_method="CASH")

    def test_negative_amount(self):
        with pytest.raises(ValidationError):
            PaymentCreate(record_id=1, amount=-10, payment_method="CASH")

    def test_amount_too_many_decimals_rejected(self):
        with pytest.raises(ValidationError):
            PaymentCreate(record_id=1, amount=40.123, payment_method="CASH")

    def test_amount_two_decimals_accepted(self):
        p = PaymentCreate(record_id=1, amount=40.12, payment_method="CASH")
        assert p.amount == 40.12

    def test_invalid_payment_method(self):
        with pytest.raises(ValidationError):
            PaymentCreate(record_id=1, amount=40, payment_method="CHECK")
