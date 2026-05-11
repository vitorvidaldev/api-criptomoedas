package com.vitorvidal.criptomoedas.adapter.in.web;

import com.vitorvidal.criptomoedas.adapter.in.web.dto.ClientBalancesResponse;
import com.vitorvidal.criptomoedas.adapter.in.web.dto.TransactionRequest;
import com.vitorvidal.criptomoedas.adapter.in.web.dto.TransactionResponse;
import com.vitorvidal.criptomoedas.application.port.in.GetClientBalancesUseCase;
import com.vitorvidal.criptomoedas.application.port.in.RegisterTransactionUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Transacoes")
public class TransactionController {

    private final RegisterTransactionUseCase registerTransactionUseCase;
    private final GetClientBalancesUseCase getClientBalancesUseCase;

    public TransactionController(
            RegisterTransactionUseCase registerTransactionUseCase,
            GetClientBalancesUseCase getClientBalancesUseCase
    ) {
        this.registerTransactionUseCase = registerTransactionUseCase;
        this.getClientBalancesUseCase = getClientBalancesUseCase;
    }

    @PostMapping("/transactions")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Registra uma compra ou venda de criptomoeda")
    public TransactionResponse register(@Valid @RequestBody TransactionRequest request) {
        return TransactionResponse.from(registerTransactionUseCase.register(request.toCommand()));
    }

    @GetMapping("/clients/{clientId}/balances")
    @Operation(summary = "Consulta saldos consolidados do cliente com cotacao atual")
    public ClientBalancesResponse getBalances(@PathVariable UUID clientId) {
        return ClientBalancesResponse.from(getClientBalancesUseCase.getBalances(clientId));
    }
}
