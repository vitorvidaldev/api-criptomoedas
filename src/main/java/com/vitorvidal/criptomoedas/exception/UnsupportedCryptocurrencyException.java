package com.vitorvidal.criptomoedas.exception;

public class UnsupportedCryptocurrencyException extends RuntimeException {
    public UnsupportedCryptocurrencyException(String message) {
        super(message);
    }
}
