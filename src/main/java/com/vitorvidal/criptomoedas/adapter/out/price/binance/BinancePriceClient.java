package com.vitorvidal.criptomoedas.adapter.out.price.binance;

import com.vitorvidal.criptomoedas.application.port.out.PricePort;
import com.vitorvidal.criptomoedas.domain.Cryptocurrency;
import com.vitorvidal.criptomoedas.exception.ExternalPriceException;
import java.math.BigDecimal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

@Component
public class BinancePriceClient implements PricePort {
    private static final Logger log = LoggerFactory.getLogger(BinancePriceClient.class);

    private final RestClient restClient;

    public BinancePriceClient(RestClient binanceRestClient) {
        this.restClient = binanceRestClient;
    }

    @Override
    public BigDecimal getCurrentPrice(Cryptocurrency cryptocurrency) {
        String symbol = cryptocurrency.getBinanceSymbol();
        log.info("Consulting Binance price for symbol={}", symbol);

        try {
            BinancePriceResponse response = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/api/v3/ticker/price")
                            .queryParam("symbol", symbol)
                            .build())
                    .retrieve()
                    .body(BinancePriceResponse.class);

            if (response == null || response.price() == null || response.price().isBlank()) {
                throw new ExternalPriceException("Binance returned an empty price for " + symbol);
            }

            return new BigDecimal(response.price());
        } catch (RestClientResponseException ex) {
            throw new ExternalPriceException(
                    "Binance price lookup failed for " + symbol + " with status " + ex.getStatusCode(),
                    ex
            );
        } catch (RestClientException | NumberFormatException ex) {
            throw new ExternalPriceException("Binance price lookup failed for " + symbol, ex);
        }
    }
}
