package com.vitorvidal.criptomoedas.adapter.out.transaction;

import com.vitorvidal.criptomoedas.application.port.out.UnitOfWorkPort;
import java.util.function.Supplier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

@Component
public class SpringUnitOfWorkAdapter implements UnitOfWorkPort {

    private final TransactionTemplate transactionTemplate;

    public SpringUnitOfWorkAdapter(TransactionTemplate transactionTemplate) {
        this.transactionTemplate = transactionTemplate;
    }

    @Override
    public <T> T execute(Supplier<T> operation) {
        return transactionTemplate.execute(status -> operation.get());
    }
}
