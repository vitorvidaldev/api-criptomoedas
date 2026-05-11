package com.vitorvidal.criptomoedas;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.vitorvidal.criptomoedas.adapter.out.persistence.jpa.repository.SpringDataClientBalanceRepository;
import com.vitorvidal.criptomoedas.adapter.out.persistence.jpa.repository.SpringDataCryptoTransactionRepository;
import com.vitorvidal.criptomoedas.application.port.out.PricePort;
import com.vitorvidal.criptomoedas.domain.Cryptocurrency;
import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TransactionControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

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

        when(pricePort.getCurrentPrice(Cryptocurrency.BTC)).thenReturn(new BigDecimal("200000.00"));
        when(pricePort.getCurrentPrice(Cryptocurrency.ETH)).thenReturn(new BigDecimal("10000.00"));
        when(pricePort.getCurrentPrice(Cryptocurrency.SOL)).thenReturn(new BigDecimal("500.00"));
    }

    @Test
    void postBuyAndGetBalances() throws Exception {
        UUID clientId = UUID.randomUUID();

        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "clientId": "%s",
                                  "type": "buy",
                                  "cryptocurrency": "BTC",
                                  "amountBRL": 1000.00
                                }
                                """.formatted(clientId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.clientId").value(clientId.toString()))
                .andExpect(jsonPath("$.type").value("buy"))
                .andExpect(jsonPath("$.cryptocurrency").value("BTC"))
                .andExpect(jsonPath("$.amountBRL").value(1000.00))
                .andExpect(jsonPath("$.amountCrypto").value(0.005));

        mockMvc.perform(get("/clients/{clientId}/balances", clientId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.clientId").value(clientId.toString()))
                .andExpect(jsonPath("$.balances.BTC.quantity").value(0.005))
                .andExpect(jsonPath("$.balances.BTC.valueBRL").value(1000.00))
                .andExpect(jsonPath("$.balances.ETH.quantity").value(0.0))
                .andExpect(jsonPath("$.balances.SOL.quantity").value(0.0))
                .andExpect(jsonPath("$.totalValueBRL").value(1000.00));
    }

    @Test
    void sellWithoutBalanceReturnsUnprocessableEntity() throws Exception {
        UUID clientId = UUID.randomUUID();

        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "clientId": "%s",
                                  "type": "sell",
                                  "cryptocurrency": "ETH",
                                  "amountCrypto": 0.01000000
                                }
                                """.formatted(clientId)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.type").value("https://criptomoedas.local/problems/insufficient-balance"));
    }

    @Test
    void unsupportedCryptocurrencyReturnsBadRequest() throws Exception {
        UUID clientId = UUID.randomUUID();

        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "clientId": "%s",
                                  "type": "buy",
                                  "cryptocurrency": "DOGE",
                                  "amountBRL": 1000.00
                                }
                                """.formatted(clientId)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("https://criptomoedas.local/problems/unsupported-cryptocurrency"));
    }
}
