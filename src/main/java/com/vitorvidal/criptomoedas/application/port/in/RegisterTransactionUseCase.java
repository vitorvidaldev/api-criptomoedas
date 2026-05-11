package com.vitorvidal.criptomoedas.application.port.in;

import com.vitorvidal.criptomoedas.application.command.RegisterTransactionCommand;
import com.vitorvidal.criptomoedas.application.result.TransactionResult;

public interface RegisterTransactionUseCase {
    TransactionResult register(RegisterTransactionCommand command);
}
