package com.vitorvidal.criptomoedas.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Locale;

public enum TransactionType {
    BUY,
    SELL;

    @JsonCreator
    public static TransactionType fromJson(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Transaction type is required");
        }

        try {
            return TransactionType.valueOf(value.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Unsupported transaction type: " + value);
        }
    }

    @JsonValue
    public String toJson() {
        return name().toLowerCase(Locale.ROOT);
    }
}
