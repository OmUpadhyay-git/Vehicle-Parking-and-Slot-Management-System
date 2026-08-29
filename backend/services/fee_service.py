import math

RATES = {
    "CAR": {
        "first_hour": 40,
        "additional_hour": 20,
    },
    "BIKE": {
        "first_hour": 20,
        "additional_hour": 10,
    },
}


def calculate_fee(vehicle_type: str, duration_minutes: int) -> float:
    vehicle_type = vehicle_type.upper()

    if vehicle_type not in RATES:
        raise ValueError(f"Invalid vehicle type: {vehicle_type}")

    if duration_minutes <= 0:
        return 0.0

    charged_hours = math.ceil(duration_minutes / 60)
    rates = RATES[vehicle_type]

    if charged_hours <= 1:
        return float(rates["first_hour"])

    additional_hours = charged_hours - 1
    total_fee = rates["first_hour"] + (additional_hours * rates["additional_hour"])

    return float(total_fee)
