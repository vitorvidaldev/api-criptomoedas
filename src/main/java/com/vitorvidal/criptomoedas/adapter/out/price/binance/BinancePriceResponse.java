package com.vitorvidal.criptomoedas.adapter.out.price.binance;

public record BinancePriceResponse(
        String symbol,
        String price
) {
}
