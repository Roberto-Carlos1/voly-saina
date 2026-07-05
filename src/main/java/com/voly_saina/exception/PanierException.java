package com.voly_saina.exception;

public class PanierException extends RuntimeException {

    public PanierException(String message) {
        super(message);
    }

    public PanierException(String message, Throwable cause) {
        super(message, cause);
    }
}