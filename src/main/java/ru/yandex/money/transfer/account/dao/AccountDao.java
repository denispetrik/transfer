package ru.yandex.money.transfer.account.dao;

import org.jooq.DSLContext;
import org.jooq.RecordMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.yandex.money.transfer.account.domain.Account;
import ru.yandex.money.transfer.common.domain.Currency;
import ru.yandex.money.transfer.common.domain.MonetaryAmount;
import ru.yandex.money.transfer.jooq.tables.records.AccountRecord;

import java.util.Optional;

import static java.util.Objects.requireNonNull;
import static ru.yandex.money.transfer.jooq.Tables.ACCOUNT;

/**
 * @author petrique
 */
public class AccountDao {

    private static final Logger log = LoggerFactory.getLogger(AccountDao.class);

    private static final RecordMapper<AccountRecord, Account> RECORD_MAPPER = record ->
            Account.builder()
                    .withId(record.getId())
                    .withNumber(record.getNumber())
                    .withBalance(MonetaryAmount.of(
                            record.getBalanceValue(),
                            Currency.byIsoCode(record.getBalanceCurrency())
                    ))
                    .build();

    private final DSLContext context;

    public AccountDao(DSLContext context) {
        this.context = context;
    }

    public Account findById(Long id) {
        requireNonNull(id, "id is required");
        log.info("findById(id={})", id);

        return context
                .selectFrom(ACCOUNT)
                .where(ACCOUNT.ID.equal(id))
                .fetchOne(RECORD_MAPPER);
    }

    public Account findAndLockById(Long id) {
        requireNonNull(id, "id is required");
        log.info("findAndLockById(id={})", id);

        return context
                .selectFrom(ACCOUNT)
                .where(ACCOUNT.ID.equal(id))
                .forUpdate()
                .fetchOne(RECORD_MAPPER);
    }

    public Optional<Account> findByNumber(String accountNumber) {
        requireNonNull(accountNumber, "accountNumber is required");
        log.info("findByNumber(accountNumber={})", accountNumber);

        return context
                .selectFrom(ACCOUNT)
                .where(ACCOUNT.NUMBER.equal(accountNumber))
                .fetchOptional(RECORD_MAPPER);
    }

    public void save(Account account) {
        requireNonNull(account, "account is required");
        log.info("save(account={})", account);

        context.insertInto(ACCOUNT)
                .set(ACCOUNT.ID, account.getId())
                .set(ACCOUNT.NUMBER, account.getNumber())
                .set(ACCOUNT.BALANCE_VALUE, account.getBalance().getValue())
                .set(ACCOUNT.BALANCE_CURRENCY, account.getBalance().getCurrency().getIsoCode())
                .execute();
    }

    public void update(Account account) {
        requireNonNull(account, "account is required");
        log.info("update(account={})", account);

        context.update(ACCOUNT)
                .set(ACCOUNT.BALANCE_VALUE, account.getBalance().getValue())
                .where(ACCOUNT.ID.equal(account.getId()))
                .execute();
    }
}
