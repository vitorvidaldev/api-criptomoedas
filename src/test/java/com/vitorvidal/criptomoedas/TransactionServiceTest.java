package com.vitorvidal.criptomoedas;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.vitorvidal.criptomoedas.adapter.out.persistence.jpa.entity.ClientBalanceJpaEntity;
import com.vitorvidal.criptomoedas.adapter.out.persistence.jpa.repository.SpringDataClientBalanceRepository;
import com.vitorvidal.criptomoedas.adapter.out.persistence.jpa.repository.SpringDataCryptoTransactionRepository;
import com.vitorvidal.criptomoedas.application.command.RegisterTransactionCommand;
import com.vitorvidal.criptomoedas.application.port.in.RegisterTransactionUseCase;
import com.vitorvidal.criptomoedas.application.port.out.PricePort;
import com.vitorvidal.criptomoedas.application.result.TransactionResult;
import com.vitorvidal.criptomoedas.domain.Cryptocurrency;
import com.vitorvidal.criptomoedas.exception.InsufficientBalanceException;
import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class TransactionServiceTest {

    @Autowired
    private RegisterTransactionUseCase registerTransactionUseCase;

    @Autowired
    private SpringDataClientBalanceRepository balanceRepository;

    @Autowired
    private SpringDataCryptoTransactionRepository transactionRepository;

    @MockBean
    private PricePort pricePort;

    @BeforeEach
    void setUp() {
        transactionRepository.deleteAll();
        balanceRepository.deleteAll();
    }

    @Test
    void buyTransactionCalculatesCryptoAndUpdatesBalance() {
        UUID clientId = UUID.randomUUID();
        when(pricePort.getCurrentPrice(Cryptocurrency.BTC)).thenReturn(new BigDecimal("200000.00"));

        TransactionResult response = registerTransactionUseCase.register(new RegisterTransactionCommand(
                clientId,
                com.vitorvidal.criptomoedas.domain.TransactionType.BUY,
                Cryptocurrency.BTC,
                new BigDecimal("1000.00"),
                null
        ));

        assertThat(response.amountBRL()).isEqualByComparingTo("1000.00");
        assertThat(response.amountCrypto()).isEqualByComparingTo("0.00500000");
        assertThat(response.exchangeRate()).isEqualByComparingTo("200000.00");

        ClientBalanceJpaEntity balance = balanceRepository.findByClientId(clientId).getFirst();
        assertThat(balance.getCryptocurrency()).isEqualTo(Cryptocurrency.BTC);
        assertThat(balance.getTotalQuantity()).isEqualByComparingTo("0.00500000");
        assertThat(transactionRepository.count()).isEqualTo(1);
    }

    @Test
    void sellTransactionRequiresEnoughBalance() {
        UUID clientId = UUID.randomUUID();
        when(pricePort.getCurrentPrice(Cryptocurrency.ETH)).thenReturn(new BigDecimal("10000.00"));

        assertThatThrownBy(() -> registerTransactionUseCase.register(new RegisterTransactionCommand(
                clientId,
                com.vitorvidal.criptomoedas.domain.TransactionType.SELL,
                Cryptocurrency.ETH,
                null,
                new BigDecimal("0.10000000")
        )))
                .isInstanceOf(InsufficientBalanceException.class)
                .hasMessageContaining("Insufficient ETH balance");

        assertThat(transactionRepository.count()).isZero();
        assertThat(balanceRepository.findByClientId(clientId)).isEmpty();
    }

    @Test
    void sellTransactionCalculatesBrlAndDecreasesBalance() {
        UUID clientId = UUID.randomUUID();
        when(pricePort.getCurrentPrice(Cryptocurrency.SOL)).thenReturn(new BigDecimal("500.00"));

        registerTransactionUseCase.register(new RegisterTransactionCommand(
                clientId,
                com.vitorvidal.criptomoedas.domain.TransactionType.BUY,
                Cryptocurrency.SOL,
                new BigDecimal("1000.00"),
                null
        ));

        TransactionResult sell = registerTransactionUseCase.register(new RegisterTransactionCommand(
                clientId,
                com.vitorvidal.criptomoedas.domain.TransactionType.SELL,
                Cryptocurrency.SOL,
                null,
                new BigDecimal("0.50000000")
        ));

        assertThat(sell.amountBRL()).isEqualByComparingTo("250.00");
        assertThat(sell.amountCrypto()).isEqualByComparingTo("0.50000000");

        ClientBalanceJpaEntity balance = balanceRepository.findByClientId(clientId).getFirst();
        assertThat(balance.getTotalQuantity()).isEqualByComparingTo("1.50000000");
        assertThat(transactionRepository.count()).isEqualTo(2);
    }
}
