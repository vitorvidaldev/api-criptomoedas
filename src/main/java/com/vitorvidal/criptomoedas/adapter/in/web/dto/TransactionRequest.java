package com.vitorvidal.criptomoedas.adapter.in.web.dto;

import com.vitorvidal.criptomoedas.application.command.RegisterTransactionCommand;
import com.vitorvidal.criptomoedas.domain.Cryptocurrency;
import com.vitorvidal.criptomoedas.domain.TransactionType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.UUID;

@Schema(description = "Dados para registrar compra ou venda de criptomoeda")
public record TransactionRequest(
        @NotNull
        @Schema(example = "a3bc6a7d-8f0d-4d08-98c2-0b715cc19dd2")
        UUID clientId,

        @NotNull
        @Schema(example = "buy", allowableValues = {"buy", "sell"})
        TransactionType type,

        @NotNull
        @Schema(example = "BTC", allowableValues = {"BTC", "ETH", "SOL"})
        Cryptocurrency cryptocurrency,

        @Positive
        @Schema(example = "1000.00", description = "Obrigatorio para compras")
        BigDecimal amountBRL,

        @Positive
        @Schema(example = "0.02000000", description = "Obrigatorio para vendas")
        BigDecimal amountCrypto
) {
    public RegisterTransactionCommand toCommand() {
        return new RegisterTransactionCommand(clientId, type, cryptocurrency, amountBRL, amountCrypto);
    }
}
