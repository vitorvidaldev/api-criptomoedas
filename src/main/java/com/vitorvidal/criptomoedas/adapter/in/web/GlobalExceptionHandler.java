package com.vitorvidal.criptomoedas.adapter.in.web;

import com.vitorvidal.criptomoedas.exception.ExternalPriceException;
import com.vitorvidal.criptomoedas.exception.InsufficientBalanceException;
import com.vitorvidal.criptomoedas.exception.InvalidTransactionRequestException;
import com.vitorvidal.criptomoedas.exception.UnsupportedCryptocurrencyException;
import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidTransactionRequestException.class)
    ProblemDetail handleInvalidTransaction(InvalidTransactionRequestException ex) {
        return problem(HttpStatus.BAD_REQUEST, "invalid-transaction-request", ex.getMessage());
    }

    @ExceptionHandler(UnsupportedCryptocurrencyException.class)
    ProblemDetail handleUnsupportedCryptocurrency(UnsupportedCryptocurrencyException ex) {
        return problem(HttpStatus.BAD_REQUEST, "unsupported-cryptocurrency", ex.getMessage());
    }

    @ExceptionHandler(InsufficientBalanceException.class)
    ProblemDetail handleInsufficientBalance(InsufficientBalanceException ex) {
        return problem(HttpStatus.UNPROCESSABLE_ENTITY, "insufficient-balance", ex.getMessage());
    }

    @ExceptionHandler(ExternalPriceException.class)
    ProblemDetail handleExternalPrice(ExternalPriceException ex) {
        return problem(HttpStatus.BAD_GATEWAY, "external-price-provider-error", ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ProblemDetail handleValidation(MethodArgumentNotValidException ex) {
        ProblemDetail problem = problem(HttpStatus.BAD_REQUEST, "validation-error", "Invalid request body");
        Map<String, String> errors = new LinkedHashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        problem.setProperty("errors", errors);
        return problem;
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    ProblemDetail handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        return problem(HttpStatus.BAD_REQUEST, "invalid-path-parameter", "Invalid value for " + ex.getName());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    ProblemDetail handleUnreadable(HttpMessageNotReadableException ex) {
        UnsupportedCryptocurrencyException unsupportedCryptocurrency = findCause(ex, UnsupportedCryptocurrencyException.class);
        if (unsupportedCryptocurrency != null) {
            return handleUnsupportedCryptocurrency(unsupportedCryptocurrency);
        }

        IllegalArgumentException illegalArgument = findCause(ex, IllegalArgumentException.class);
        if (illegalArgument != null) {
            return handleIllegalArgument(illegalArgument);
        }

        return problem(HttpStatus.BAD_REQUEST, "malformed-json", "Request body is malformed or contains invalid values");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    ProblemDetail handleIllegalArgument(IllegalArgumentException ex) {
        return problem(HttpStatus.BAD_REQUEST, "invalid-request", ex.getMessage());
    }

    private ProblemDetail problem(HttpStatus status, String type, String detail) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail);
        problem.setTitle(status.getReasonPhrase());
        problem.setType(URI.create("https://criptomoedas.local/problems/" + type));
        return problem;
    }

    private <T extends Throwable> T findCause(Throwable throwable, Class<T> type) {
        Throwable current = throwable;
        while (current != null) {
            if (type.isInstance(current)) {
                return type.cast(current);
            }
            current = current.getCause();
        }
        return null;
    }
}
