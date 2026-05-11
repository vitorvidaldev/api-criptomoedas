package com.vitorvidal.criptomoedas.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    OpenAPI cryptoTransactionsOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API de Transacoes de Criptomoedas")
                        .version("v1")
                        .description("Gerencia compras, vendas e saldos de BTC, ETH e SOL com cotacao Binance."));
    }
}
