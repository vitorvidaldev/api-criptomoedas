package com.vitorvidal.criptomoedas.application.port.out;

import com.vitorvidal.criptomoedas.domain.CryptoTransaction;

public interface CryptoTransactionPort {
    CryptoTransaction save(CryptoTransaction transaction);
}
