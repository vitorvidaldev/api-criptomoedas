package com.vitorvidal.criptomoedas.adapter.in.web.dto;

import com.vitorvidal.criptomoedas.application.result.CryptoBalanceResult;
import java.math.BigDecimal;

public record CryptoBalanceResponse(
        BigDecimal quantity,
        BigDecimal valueBRL
) {
    public static CryptoBalanceResponse from(CryptoBalanceResult result) {
        return new CryptoBalanceResponse(result.quantity(), result.valueBRL());
    }
}
