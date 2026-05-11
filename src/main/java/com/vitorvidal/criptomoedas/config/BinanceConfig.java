package com.vitorvidal.criptomoedas.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class BinanceConfig {

    @Bean
    RestClient binanceRestClient(RestClient.Builder builder, BinanceProperties properties) {
        return builder.baseUrl(properties.baseUrl()).build();
    }
}
