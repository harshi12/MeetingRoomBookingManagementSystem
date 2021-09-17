package com.adobe.MiniProject.errorcodes;

public enum BookingError {
    EQUIPMENT_NOT_FOUND("Cannot find equipment title. Or check for discrepancy between equipment title and equipment id"),
    ROOM_NOT_FOUND("Cannot find room title"),
    LAYOUT_NOT_FOUND("Cannot find layout title"),
    FOOD_ITEM_NOT_FOUND("Cannot find food item"),
    BOOKING_JSON_HAS_BOOKING_ID("Cannot add a booking already with bookingID"),
    BOOKING_JSON_HAS_CLIENT_ID("Cannot add a booking already with clientID"),
    INVALID_UPDATE_BOOKING_JSON("Cannot find booking ID in the JSON"),
    EQUIPMENT_ID_NOT_FOUND("Equipment ID missing in Booking JSON"),
    FOOD_ID_NOT_FOUND("FOOD ID missing in Booking JSON"),
    CLIENT_ID_NOT_FOUND("Client ID missing in Booking JSON"),
    INVALID_BOOKING("No booking exists with the given bookingID"),
    INVALID_BOOKING_DATE("Booking date cannot be in past"),
    INVALID_END_DATE("Booking End Date cannot be before Booking Date");

    public String error;

    BookingError(String string) {
        this.error  = string;
    }

    public String value() {
        return this.error;
    }
}
