package ru.yandex.money.transfer.account.service;

import ru.yandex.money.transfer.account.dao.AccountDao;
import ru.yandex.money.transfer.account.domain.Account;
import ru.yandex.money.transfer.common.domain.MonetaryAmount;
import ru.yandex.money.transfer.core.database.TransactionHandler;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * @author petrique
 */
public class AccountService {

    private final TransactionHandler transactionHandler;
    private final AccountDao accountDao;

    public AccountService(TransactionHandler transactionHandler, AccountDao accountDao) {
        this.transactionHandler = transactionHandler;
        this.accountDao = accountDao;
    }

    public Account findAccountById(Long id) {
        return accountDao.findById(id);
    }

    public Account findAndLockAccountById(Long id) {
        return accountDao.findAndLockById(id);
    }

    public Optional<Account> findAccountByNumber(String accountNumber) {
        return accountDao.findByNumber(accountNumber);
    }

    public void saveAccount(Account account) {
        transactionHandler.inTransaction(() ->
                accountDao.save(account)
        );
    }

    public void updateBalance(Account account, BigDecimal newBalanceValue) {
        MonetaryAmount newBalance = MonetaryAmount.of(newBalanceValue, account.getBalance().getCurrency());
        Account updatedAccount = account.toBuilder().withBalance(newBalance).build();
        transactionHandler.inTransaction(() ->
                accountDao.update(updatedAccount)
        );
    }
}
