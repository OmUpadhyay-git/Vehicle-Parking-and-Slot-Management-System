import sys
import os
sys.path.insert(0, os.path.join(os.path.dirname(__file__), ".."))

import pytest
from services.fee_service import calculate_fee


class TestCalculateFeeCAR:
    def test_first_hour_exactly(self):
        assert calculate_fee("CAR", 60) == 40

    def test_less_than_one_hour(self):
        assert calculate_fee("CAR", 30) == 40

    def test_one_minute(self):
        assert calculate_fee("CAR", 1) == 40

    def test_two_hours(self):
        assert calculate_fee("CAR", 120) == 60

    def test_three_hours(self):
        assert calculate_fee("CAR", 180) == 80

    def test_ten_hours(self):
        assert calculate_fee("CAR", 600) == 220

    def test_partial_hour_rounds_up(self):
        assert calculate_fee("CAR", 90) == 60

    def test_61_minutes(self):
        assert calculate_fee("CAR", 61) == 60


class TestCalculateFeeBIKE:
    def test_first_hour_exactly(self):
        assert calculate_fee("BIKE", 60) == 20

    def test_less_than_one_hour(self):
        assert calculate_fee("BIKE", 30) == 20

    def test_one_minute(self):
        assert calculate_fee("BIKE", 1) == 20

    def test_two_hours(self):
        assert calculate_fee("BIKE", 120) == 30

    def test_three_hours(self):
        assert calculate_fee("BIKE", 180) == 40

    def test_ten_hours(self):
        assert calculate_fee("BIKE", 600) == 110

    def test_partial_hour_rounds_up(self):
        assert calculate_fee("BIKE", 90) == 30


class TestCalculateFeeEdgeCases:
    def test_zero_minutes(self):
        assert calculate_fee("CAR", 0) == 0.0

    def test_negative_minutes(self):
        assert calculate_fee("CAR", -5) == 0.0

    def test_invalid_vehicle_type(self):
        with pytest.raises(ValueError, match="Invalid vehicle type"):
            calculate_fee("TRUCK", 60)

    def test_case_insensitive_type(self):
        assert calculate_fee("car", 60) == 40
        assert calculate_fee("bike", 60) == 20
