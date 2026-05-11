package com.vitorvidal.criptomoedas.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class DecimalSupport {
    public static final int BRL_SCALE = 2;
    public static final int CRYPTO_SCALE = 8;
    public static final int STORAGE_SCALE = 18;

    private DecimalSupport() {
    }

    public static BigDecimal toMoney(BigDecimal value) {
        return value.setScale(BRL_SCALE, RoundingMode.HALF_UP);
    }

    public static BigDecimal toCrypto(BigDecimal value) {
        return value.setScale(CRYPTO_SCALE, RoundingMode.DOWN);
    }

    public static BigDecimal toStorage(BigDecimal value) {
        return value.setScale(STORAGE_SCALE, RoundingMode.HALF_UP);
    }

    public static BigDecimal zeroCrypto() {
        return BigDecimal.ZERO.setScale(CRYPTO_SCALE);
    }

    public static BigDecimal cryptoFromMoney(BigDecimal amountBRL, BigDecimal exchangeRate) {
        return amountBRL.divide(exchangeRate, CRYPTO_SCALE, RoundingMode.DOWN);
    }

    public static BigDecimal moneyFromCrypto(BigDecimal amountCrypto, BigDecimal exchangeRate) {
        return toMoney(amountCrypto.multiply(exchangeRate));
    }

    public static boolean hasAtMostScale(BigDecimal value, int scale) {
        return value == null || Math.max(0, value.stripTrailingZeros().scale()) <= scale;
    }
}
