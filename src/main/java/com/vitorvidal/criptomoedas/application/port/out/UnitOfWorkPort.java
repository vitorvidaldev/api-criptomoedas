package com.vitorvidal.criptomoedas.application.port.out;

import java.util.function.Supplier;

public interface UnitOfWorkPort {
    <T> T execute(Supplier<T> operation);
}
