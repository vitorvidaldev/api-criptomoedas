package com.vitorvidal.criptomoedas.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.vitorvidal.criptomoedas.exception.UnsupportedCryptocurrencyException;
import java.util.Locale;

public enum Cryptocurrency {
    BTC("BTCBRL"),
    ETH("ETHBRL"),
    SOL("SOLBRL");

    private final String binanceSymbol;

    Cryptocurrency(String binanceSymbol) {
        this.binanceSymbol = binanceSymbol;
    }

    public String getBinanceSymbol() {
        return binanceSymbol;
    }

    @JsonCreator
    public static Cryptocurrency fromJson(String value) {
        if (value == null || value.isBlank()) {
            throw new UnsupportedCryptocurrencyException("Cryptocurrency is required");
        }

        try {
            return Cryptocurrency.valueOf(value.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            throw new UnsupportedCryptocurrencyException("Unsupported cryptocurrency: " + value);
        }
    }

    @JsonValue
    public String toJson() {
        return name();
    }
}
