package ru.yandex.money.transfer.transaction.dao;

import org.jooq.DSLContext;
import org.jooq.RecordMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.yandex.money.transfer.account.domain.Account;
import ru.yandex.money.transfer.common.domain.Currency;
import ru.yandex.money.transfer.common.domain.MonetaryAmount;
import ru.yandex.money.transfer.jooq.tables.records.UserTransactionRecord;
import ru.yandex.money.transfer.transaction.domain.Transaction;
import ru.yandex.money.transfer.transaction.domain.TransactionType;

import java.util.List;

import static java.util.Objects.requireNonNull;
import static ru.yandex.money.transfer.jooq.Sequences.SEQ_USER_TRANSACTION_ID;
import static ru.yandex.money.transfer.jooq.Tables.USER_TRANSACTION;

/**
 * @author petrique
 */
public class TransactionDao {

    private static final Logger log = LoggerFactory.getLogger(TransactionDao.class);

    private static final RecordMapper<UserTransactionRecord, Transaction> RECORD_MAPPER = record ->
            Transaction.builder()
                    .withId(record.getId())
                    .withType(TransactionType.byDbCode(record.getType()))
                    .withExternalId(record.getExternalId())
                    .withAccountId(record.getAccountId())
                    .withAmount(MonetaryAmount.of(
                            record.getAmountValue(),
                            Currency.byIsoCode(record.getAmountCurrency())
                    ))
                    .build();

    private final DSLContext context;

    public TransactionDao(DSLContext context) {
        this.context = context;
    }

    public List<Transaction> findByAccount(Account account) {
        requireNonNull(account, "account is required");
        log.info("findByAccount(accountId={})", account.getId());

        return context
                .selectFrom(USER_TRANSACTION)
                .where(USER_TRANSACTION.ACCOUNT_ID.equal(account.getId()))
                .fetch(RECORD_MAPPER);
    }

    public Long generateId() {
        log.info("generateId()");
        return context.nextval(SEQ_USER_TRANSACTION_ID);
    }

    public void save(Transaction transaction) {
        requireNonNull(transaction, "transaction is required");
        log.info("save(transaction={})", transaction);

        context
                .insertInto(USER_TRANSACTION)
                .set(USER_TRANSACTION.ID, transaction.getId())
                .set(USER_TRANSACTION.TYPE, transaction.getType().getDbCode())
                .set(USER_TRANSACTION.EXTERNAL_ID, transaction.getExternalId())
                .set(USER_TRANSACTION.ACCOUNT_ID, transaction.getAccountId())
                .set(USER_TRANSACTION.AMOUNT_VALUE, transaction.getAmount().getValue())
                .set(USER_TRANSACTION.AMOUNT_CURRENCY, transaction.getAmount().getCurrency().getIsoCode())
                .execute();
    }
}
