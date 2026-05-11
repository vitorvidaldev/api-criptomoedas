package com.vitorvidal.criptomoedas.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "binance.api")
public record BinanceProperties(
        String baseUrl
) {
}
