package com.voly_saina.exception;

public class ReservationException extends RuntimeException {

    public static final String RESERVATION_NON_FACTUREE = "La réservation n'est pas encore facturée";

    public ReservationException(String message) {
        super(message);
    }

    public ReservationException(String message, Throwable cause) {
        super(message, cause);
    }
}