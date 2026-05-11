package com.vitorvidal.criptomoedas.application.port.out;

import com.vitorvidal.criptomoedas.domain.Cryptocurrency;
import java.math.BigDecimal;

public interface PricePort {
    BigDecimal getCurrentPrice(Cryptocurrency cryptocurrency);
}
