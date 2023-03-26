package com.jolyn.meetingroomreservationsystem.exception;

public class OverlappingReservationException extends Exception {
    public OverlappingReservationException(String message) {
        super(message);
    }
}
