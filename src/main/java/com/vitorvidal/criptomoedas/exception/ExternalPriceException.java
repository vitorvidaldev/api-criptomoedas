package com.vitorvidal.criptomoedas.exception;

public class ExternalPriceException extends RuntimeException {
    public ExternalPriceException(String message) {
        super(message);
    }

    public ExternalPriceException(String message, Throwable cause) {
        super(message, cause);
    }
}
