package com.vitorvidal.criptomoedas.application.service;

import com.vitorvidal.criptomoedas.application.command.RegisterTransactionCommand;
import com.vitorvidal.criptomoedas.application.port.in.GetClientBalancesUseCase;
import com.vitorvidal.criptomoedas.application.port.in.RegisterTransactionUseCase;
import com.vitorvidal.criptomoedas.application.port.out.ClientBalancePort;
import com.vitorvidal.criptomoedas.application.port.out.CryptoTransactionPort;
import com.vitorvidal.criptomoedas.application.port.out.PricePort;
import com.vitorvidal.criptomoedas.application.port.out.UnitOfWorkPort;
import com.vitorvidal.criptomoedas.application.result.ClientBalancesResult;
import com.vitorvidal.criptomoedas.application.result.CryptoBalanceResult;
import com.vitorvidal.criptomoedas.application.result.TransactionResult;
import com.vitorvidal.criptomoedas.domain.ClientBalance;
import com.vitorvidal.criptomoedas.domain.CryptoTransaction;
import com.vitorvidal.criptomoedas.domain.Cryptocurrency;
import com.vitorvidal.criptomoedas.domain.DecimalSupport;
import com.vitorvidal.criptomoedas.domain.TransactionType;
import com.vitorvidal.criptomoedas.exception.InsufficientBalanceException;
import com.vitorvidal.criptomoedas.exception.InvalidTransactionRequestException;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TransactionApplicationService implements RegisterTransactionUseCase, GetClientBalancesUseCase {
    private static final Logger log = LoggerFactory.getLogger(TransactionApplicationService.class);

    private final CryptoTransactionPort transactionPort;
    private final ClientBalancePort balancePort;
    private final PricePort pricePort;
    private final UnitOfWorkPort unitOfWorkPort;

    public TransactionApplicationService(
            CryptoTransactionPort transactionPort,
            ClientBalancePort balancePort,
            PricePort pricePort,
            UnitOfWorkPort unitOfWorkPort
    ) {
        this.transactionPort = transactionPort;
        this.balancePort = balancePort;
        this.pricePort = pricePort;
        this.unitOfWorkPort = unitOfWorkPort;
    }

    @Override
    public TransactionResult register(RegisterTransactionCommand command) {
        NormalizedTransaction normalized = normalize(command);
        BigDecimal exchangeRate = pricePort.getCurrentPrice(normalized.cryptocurrency());
        Instant now = Instant.now();

        BigDecimal amountBRL = normalized.type() == TransactionType.BUY
                ? DecimalSupport.toMoney(normalized.amountBRL())
                : DecimalSupport.moneyFromCrypto(normalized.amountCrypto(), exchangeRate);
        BigDecimal amountCrypto = normalized.type() == TransactionType.BUY
                ? DecimalSupport.cryptoFromMoney(normalized.amountBRL(), exchangeRate)
                : DecimalSupport.toCrypto(normalized.amountCrypto());

        CryptoTransaction transaction = Objects.requireNonNull(unitOfWorkPort.execute(() -> {
            ClientBalance balance = balancePort
                    .findByClientIdAndCryptocurrencyForUpdate(normalized.clientId(), normalized.cryptocurrency())
                    .orElseGet(() -> new ClientBalance(normalized.clientId(), normalized.cryptocurrency(), now));

            if (normalized.type() == TransactionType.SELL
                    && balance.getTotalQuantity().compareTo(amountCrypto) < 0) {
                throw new InsufficientBalanceException(
                        "Insufficient " + normalized.cryptocurrency() + " balance for client " + normalized.clientId()
                );
            }

            if (normalized.type() == TransactionType.BUY) {
                balance.increase(amountCrypto, now);
            } else {
                balance.decrease(amountCrypto, now);
            }

            balancePort.save(balance);
            CryptoTransaction saved = transactionPort.save(CryptoTransaction.create(
                    normalized.clientId(),
                    normalized.type(),
                    normalized.cryptocurrency(),
                    amountBRL,
                    amountCrypto,
                    exchangeRate,
                    now
            ));

            log.info(
                    "Registered transaction id={} clientId={} type={} cryptocurrency={} amountBRL={} amountCrypto={} exchangeRate={}",
                    saved.getId(),
                    saved.getClientId(),
                    saved.getType(),
                    saved.getCryptocurrency(),
                    DecimalSupport.toMoney(saved.getAmountBRL()),
                    DecimalSupport.toCrypto(saved.getAmountCrypto()),
                    DecimalSupport.toMoney(saved.getExchangeRate())
            );
            return saved;
        }));

        return TransactionResult.from(transaction);
    }

    @Override
    public ClientBalancesResult getBalances(UUID clientId) {
        if (clientId == null) {
            throw new InvalidTransactionRequestException("Client id is required");
        }

        Map<Cryptocurrency, ClientBalance> balancesByCrypto = balancePort.findByClientId(clientId)
                .stream()
                .collect(Collectors.toMap(ClientBalance::getCryptocurrency, Function.identity()));

        Map<Cryptocurrency, BigDecimal> prices = new EnumMap<>(Cryptocurrency.class);
        for (Cryptocurrency cryptocurrency : Cryptocurrency.values()) {
            prices.put(cryptocurrency, pricePort.getCurrentPrice(cryptocurrency));
        }

        Map<String, CryptoBalanceResult> responseBalances = new LinkedHashMap<>();
        BigDecimal totalValueBRL = BigDecimal.ZERO;

        for (Cryptocurrency cryptocurrency : Cryptocurrency.values()) {
            BigDecimal quantity = balancesByCrypto.containsKey(cryptocurrency)
                    ? DecimalSupport.toCrypto(balancesByCrypto.get(cryptocurrency).getTotalQuantity())
                    : DecimalSupport.zeroCrypto();
            BigDecimal valueBRL = DecimalSupport.moneyFromCrypto(quantity, prices.get(cryptocurrency));

            responseBalances.put(cryptocurrency.name(), new CryptoBalanceResult(quantity, valueBRL));
            totalValueBRL = totalValueBRL.add(valueBRL);
        }

        log.info("Consulted balances for clientId={}", clientId);
        return new ClientBalancesResult(
                clientId,
                responseBalances,
                DecimalSupport.toMoney(totalValueBRL),
                Instant.now()
        );
    }

    private NormalizedTransaction normalize(RegisterTransactionCommand command) {
        if (command == null) {
            throw new InvalidTransactionRequestException("Request body is required");
        }
        if (command.clientId() == null) {
            throw new InvalidTransactionRequestException("clientId is required");
        }
        if (command.type() == null) {
            throw new InvalidTransactionRequestException("type is required");
        }
        if (command.cryptocurrency() == null) {
            throw new InvalidTransactionRequestException("cryptocurrency is required");
        }

        if (command.type() == TransactionType.BUY) {
            validateBuy(command);
            return new NormalizedTransaction(
                    command.clientId(),
                    command.type(),
                    command.cryptocurrency(),
                    DecimalSupport.toMoney(command.amountBRL()),
                    null
            );
        }

        validateSell(command);
        return new NormalizedTransaction(
                command.clientId(),
                command.type(),
                command.cryptocurrency(),
                null,
                DecimalSupport.toCrypto(command.amountCrypto())
        );
    }

    private void validateBuy(RegisterTransactionCommand command) {
        if (command.amountBRL() == null) {
            throw new InvalidTransactionRequestException("amountBRL is required for buy transactions");
        }
        if (command.amountCrypto() != null) {
            throw new InvalidTransactionRequestException("amountCrypto must not be sent for buy transactions");
        }
        if (command.amountBRL().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidTransactionRequestException("amountBRL must be greater than zero");
        }
        if (!DecimalSupport.hasAtMostScale(command.amountBRL(), DecimalSupport.BRL_SCALE)) {
            throw new InvalidTransactionRequestException("amountBRL accepts at most 2 decimal places");
        }
    }

    private void validateSell(RegisterTransactionCommand command) {
        if (command.amountCrypto() == null) {
            throw new InvalidTransactionRequestException("amountCrypto is required for sell transactions");
        }
        if (command.amountBRL() != null) {
            throw new InvalidTransactionRequestException("amountBRL must not be sent for sell transactions");
        }
        if (command.amountCrypto().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidTransactionRequestException("amountCrypto must be greater than zero");
        }
        if (!DecimalSupport.hasAtMostScale(command.amountCrypto(), DecimalSupport.CRYPTO_SCALE)) {
            throw new InvalidTransactionRequestException("amountCrypto accepts at most 8 decimal places");
        }
    }

    private record NormalizedTransaction(
            UUID clientId,
            TransactionType type,
            Cryptocurrency cryptocurrency,
            BigDecimal amountBRL,
            BigDecimal amountCrypto
    ) {
    }
}
