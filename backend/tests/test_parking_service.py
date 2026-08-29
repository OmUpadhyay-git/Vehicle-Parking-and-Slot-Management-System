import sys
import os
sys.path.insert(0, os.path.join(os.path.dirname(__file__), ".."))

import pytest
from unittest.mock import MagicMock, patch
from datetime import datetime, timedelta
from services.parking_service import park_vehicle, exit_vehicle, get_dashboard_stats
from services.fee_service import calculate_fee


def make_vehicle(vehicle_id=1, vehicle_number="KA01AB1234", vehicle_type="CAR", owner_name="Test", owner_phone="1234567890"):
    v = MagicMock()
    v.vehicle_id = vehicle_id
    v.vehicle_number = vehicle_number
    v.vehicle_type = vehicle_type
    v.owner_name = owner_name
    v.owner_phone = owner_phone
    return v


def make_slot(slot_id=1, slot_number="A1", vehicle_type="CAR", status="AVAILABLE"):
    s = MagicMock()
    s.slot_id = slot_id
    s.slot_number = slot_number
    s.vehicle_type = vehicle_type
    s.status = status
    return s


def make_record(record_id=1, vehicle_id=1, slot_id=1, entry_time=None, exit_time=None, duration=None, fee=None, status="PARKED"):
    r = MagicMock()
    r.record_id = record_id
    r.vehicle_id = vehicle_id
    r.slot_id = slot_id
    r.entry_time = entry_time or datetime.now()
    r.exit_time = exit_time
    r.duration = duration
    r.fee = fee
    r.status = status
    return r


class TestParkVehicle:
    def test_park_vehicle_success(self):
        db = MagicMock()
        vehicle = make_vehicle()
        slot = make_slot()

        db.query.return_value.filter.return_value.first.side_effect = [vehicle, None, slot]

        result = park_vehicle(db, "KA01AB1234", 1)

        assert result["success"] is True
        assert result["data"]["vehicle_number"] == "KA01AB1234"
        assert result["data"]["slot_number"] == "A1"
        db.commit.assert_called_once()

    def test_park_vehicle_not_found(self):
        db = MagicMock()
        db.query.return_value.filter.return_value.first.return_value = None

        result = park_vehicle(db, "NONEXIST", 1)

        assert result["success"] is False
        assert "not found" in result["message"].lower()

    def test_park_vehicle_already_parked(self):
        db = MagicMock()
        vehicle = make_vehicle()
        active_record = make_record()

        db.query.return_value.filter.return_value.first.side_effect = [vehicle, active_record]

        result = park_vehicle(db, "KA01AB1234", 1)

        assert result["success"] is False
        assert "already parked" in result["message"].lower()

    def test_park_vehicle_slot_occupied(self):
        db = MagicMock()
        vehicle = make_vehicle()
        slot = make_slot(status="OCCUPIED")

        db.query.return_value.filter.return_value.first.side_effect = [vehicle, None, slot]

        result = park_vehicle(db, "KA01AB1234", 1)

        assert result["success"] is False
        assert "occupied" in result["message"].lower()

    def test_park_vehicle_type_mismatch(self):
        db = MagicMock()
        vehicle = make_vehicle(vehicle_type="BIKE")
        slot = make_slot(vehicle_type="CAR")

        db.query.return_value.filter.return_value.first.side_effect = [vehicle, None, slot]

        result = park_vehicle(db, "KA01AB1234", 1)

        assert result["success"] is False
        assert "type" in result["message"].lower()

    def test_park_vehicle_case_insensitive_number(self):
        db = MagicMock()
        vehicle = make_vehicle(vehicle_number="KA01AB1234")
        slot = make_slot()

        db.query.return_value.filter.return_value.first.side_effect = [vehicle, None, slot]

        result = park_vehicle(db, "ka01ab1234", 1)

        assert result["success"] is True


class TestExitVehicle:
    def test_exit_vehicle_success(self):
        db = MagicMock()
        vehicle = make_vehicle()
        slot = make_slot()
        record = make_record(entry_time=datetime.now() - timedelta(hours=2))

        db.query.return_value.filter.return_value.first.side_effect = [record, vehicle, slot, None]

        result = exit_vehicle(db, 1, "CASH")

        assert result["success"] is True
        assert result["data"]["fee"] > 0
        assert result["data"]["duration_minutes"] > 0
        db.commit.assert_called_once()

    def test_exit_record_not_found(self):
        db = MagicMock()
        db.query.return_value.filter.return_value.first.return_value = None

        result = exit_vehicle(db, 999, "CASH")

        assert result["success"] is False
        assert "not found" in result["message"].lower()

    def test_exit_already_completed(self):
        db = MagicMock()
        record = make_record(status="COMPLETED")

        db.query.return_value.filter.return_value.first.return_value = record

        result = exit_vehicle(db, 1, "CASH")

        assert result["success"] is False
        assert "already completed" in result["message"].lower()

    def test_exit_creates_payment(self):
        db = MagicMock()
        vehicle = make_vehicle()
        slot = make_slot()
        record = make_record(entry_time=datetime.now() - timedelta(hours=1))

        db.query.return_value.filter.return_value.first.side_effect = [record, vehicle, slot, None]

        result = exit_vehicle(db, 1, "UPI")

        assert result["data"]["payment_method"] == "UPI"
        db.add.assert_called_once()
        db.commit.assert_called()


class TestDashboardStats:
    def test_dashboard_returns_stats(self):
        db = MagicMock()
        db.query.return_value.count.return_value = 20
        db.query.return_value.filter.return_value.count.return_value = 15
        db.query.return_value.filter.return_value.all.return_value = []

        stats = get_dashboard_stats(db)

        assert "total_slots" in stats
        assert "available_slots" in stats
        assert "occupied_slots" in stats
        assert "active_vehicles" in stats
        assert "today_revenue" in stats
