package ru.yandex.money.transfer.core.database;

import org.springframework.transaction.support.TransactionTemplate;

import static java.util.Objects.requireNonNull;

/**
 * @author petrique
 */
public class TransactionHandler {

    private final TransactionTemplate transactionTemplate;

    public TransactionHandler(TransactionTemplate transactionTemplate) {
        this.transactionTemplate = transactionTemplate;
    }

    public void inTransaction(TransactionalCode code) {
        requireNonNull(code, "code is required");
        transactionTemplate.execute(status -> {
            code.call();
            return null;
        });
    }

    public <T> T inTransaction(TransactionalCodeWithResult<T> code) {
        requireNonNull(code, "code is required");
        return transactionTemplate.execute(status -> code.call());
    }


    @FunctionalInterface
    public interface TransactionalCode {
        void call();
    }

    @FunctionalInterface
    public interface TransactionalCodeWithResult<T> {
        T call();
    }
}
