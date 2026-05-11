package com.vitorvidal.criptomoedas.config;

import com.vitorvidal.criptomoedas.application.port.out.ClientBalancePort;
import com.vitorvidal.criptomoedas.application.port.out.CryptoTransactionPort;
import com.vitorvidal.criptomoedas.application.port.out.PricePort;
import com.vitorvidal.criptomoedas.application.port.out.UnitOfWorkPort;
import com.vitorvidal.criptomoedas.application.service.TransactionApplicationService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationUseCaseConfig {

    @Bean
    TransactionApplicationService transactionApplicationService(
            CryptoTransactionPort transactionPort,
            ClientBalancePort balancePort,
            PricePort pricePort,
            UnitOfWorkPort unitOfWorkPort
    ) {
        return new TransactionApplicationService(transactionPort, balancePort, pricePort, unitOfWorkPort);
    }
}
