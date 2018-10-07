package ru.yandex.money.transfer.transaction.service;

import ru.yandex.money.transfer.account.domain.Account;
import ru.yandex.money.transfer.account.service.AccountService;
import ru.yandex.money.transfer.common.domain.MonetaryAmount;
import ru.yandex.money.transfer.core.database.TransactionHandler;
import ru.yandex.money.transfer.transaction.dao.TransactionDao;
import ru.yandex.money.transfer.transaction.domain.Transaction;
import ru.yandex.money.transfer.transaction.domain.TransactionType;

import java.util.List;
import java.util.UUID;

import static ru.yandex.money.transfer.transaction.domain.TransactionType.DEPOSITION;
import static ru.yandex.money.transfer.transaction.domain.TransactionType.WITHDRAWAL;

/**
 * @author petrique
 */
public class TransactionService {

    private final TransactionHandler transactionHandler;
    private final TransactionDao transactionDao;
    private final AccountService accountService;

    public TransactionService(
            TransactionHandler transactionHandler,
            TransactionDao transactionDao,
            AccountService accountService
    ) {
        this.transactionHandler = transactionHandler;
        this.transactionDao = transactionDao;
        this.accountService = accountService;
    }

    public List<Transaction> findTransactions(Account account) {
        return transactionDao.findByAccount(account);
    }

    public void depositAmount(Account account, MonetaryAmount amount) {
        if (account.getBalance().getCurrency() != amount.getCurrency()) {
            throw new IllegalArgumentException("Amount's currency must be equal to account's currency");
        }

        transactionHandler.inTransaction(() -> {
            accountService.updateBalance(account, account.getBalance().getValue().add(amount.getValue()));
            transactionDao.save(transaction(DEPOSITION, account, amount));
        });
    }

    public void withdrawAmount(Account account, MonetaryAmount amount) {
        if (account.getBalance().getCurrency() != amount.getCurrency()) {
            throw new IllegalArgumentException("Amount's currency must be equal to account's currency");
        }
        if (account.getBalance().getValue().compareTo(amount.getValue()) < 0) {
            throw new IllegalArgumentException("Not enough funds on account's balance");
        }

        transactionHandler.inTransaction(() -> {
            accountService.updateBalance(account, account.getBalance().getValue().subtract(amount.getValue()));
            transactionDao.save(transaction(WITHDRAWAL, account, amount));
        });
    }

    private Transaction transaction(TransactionType type, Account account, MonetaryAmount amount) {
        return Transaction.builder()
                .withId(transactionDao.generateId())
                .withType(type)
                .withExternalId(generateExternalId())
                .withAccountId(account.getId())
                .withAmount(amount)
                .build();
    }

    private String generateExternalId() {
        return UUID.randomUUID().toString();
    }
}
