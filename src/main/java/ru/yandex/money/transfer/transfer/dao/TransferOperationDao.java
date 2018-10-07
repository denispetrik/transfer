package ru.yandex.money.transfer.transfer.dao;

import org.jooq.DSLContext;
import org.jooq.RecordMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.yandex.money.transfer.common.domain.Currency;
import ru.yandex.money.transfer.common.domain.MonetaryAmount;
import ru.yandex.money.transfer.jooq.tables.records.TransferOperationRecord;
import ru.yandex.money.transfer.transfer.domain.TransferOperation;
import ru.yandex.money.transfer.transfer.domain.TransferOperationErrorCode;
import ru.yandex.money.transfer.transfer.domain.TransferOperationState;

import java.util.Optional;

import static java.util.Objects.requireNonNull;
import static ru.yandex.money.transfer.jooq.Sequences.SEQ_TRANSFER_OPERATION_ID;
import static ru.yandex.money.transfer.jooq.Tables.TRANSFER_OPERATION;

/**
 * @author petrique
 */
public class TransferOperationDao {

    private static final Logger log = LoggerFactory.getLogger(TransferOperationDao.class);

    private static final RecordMapper<TransferOperationRecord, TransferOperation> RECORD_MAPPER = record ->
            TransferOperation.builder()
                    .withId(record.getId())
                    .withState(TransferOperationState.byDbCode(record.getState()))
                    .withExternalId(record.getExternalId())
                    .withSenderAccountId(record.getSenderAccountId())
                    .withReceiverAccountId(record.getReceiverAccountId())
                    .withAmount(MonetaryAmount.of(
                            record.getAmountValue(),
                            Currency.byIsoCode(record.getAmountCurrency())
                    ))
                    .withErrorCode(record.getErrorCode() != null
                            ? TransferOperationErrorCode.byDbCode(record.getErrorCode())
                            : null
                    )
                    .build();

    private final DSLContext context;

    public TransferOperationDao(DSLContext context) {
        this.context = context;
    }

    public TransferOperation findById(Long id) {
        requireNonNull(id, "id is required");
        log.info("findById(id={})", id);

        return context
                .selectFrom(TRANSFER_OPERATION)
                .where(TRANSFER_OPERATION.ID.equal(id))
                .fetchOne(RECORD_MAPPER);
    }

    public Optional<TransferOperation> findByExternalId(String externalId) {
        requireNonNull(externalId, "externalId is required");
        log.info("findByExternalId(externalId={})", externalId);

        return context
                .selectFrom(TRANSFER_OPERATION)
                .where(TRANSFER_OPERATION.EXTERNAL_ID.equal(externalId))
                .fetchOptional(RECORD_MAPPER);
    }

    public Long generateId() {
        log.info("generateId()");
        return context.nextval(SEQ_TRANSFER_OPERATION_ID);
    }

    public void save(TransferOperation transferOperation) {
        requireNonNull(transferOperation, "transferOperation is required");
        log.info("save(transferOperation={})", transferOperation);

        context
                .insertInto(TRANSFER_OPERATION)
                .set(TRANSFER_OPERATION.ID, transferOperation.getId())
                .set(TRANSFER_OPERATION.STATE, transferOperation.getState().getDbCode())
                .set(TRANSFER_OPERATION.EXTERNAL_ID, transferOperation.getExternalId())
                .set(TRANSFER_OPERATION.SENDER_ACCOUNT_ID, transferOperation.getSenderAccountId())
                .set(TRANSFER_OPERATION.RECEIVER_ACCOUNT_ID, transferOperation.getReceiverAccountId())
                .set(TRANSFER_OPERATION.AMOUNT_VALUE, transferOperation.getAmount().getValue())
                .set(TRANSFER_OPERATION.AMOUNT_CURRENCY, transferOperation.getAmount().getCurrency().getIsoCode())
                .set(
                        TRANSFER_OPERATION.ERROR_CODE,
                        transferOperation.getErrorCode().map(TransferOperationErrorCode::getDbCode).orElse(null)
                )
                .execute();
    }

    public boolean updateStateWithCheck(TransferOperation transferOperation, TransferOperationState newState) {
        requireNonNull(transferOperation, "transferOperation is required");
        requireNonNull(newState, "newState is required");
        log.info("updateStateWithCheck(transferOperation={}, newState={})", transferOperation, newState);

        return context
                .update(TRANSFER_OPERATION)
                .set(TRANSFER_OPERATION.STATE, newState.getDbCode())
                .where(TRANSFER_OPERATION.ID.equal(transferOperation.getId())
                        .and(TRANSFER_OPERATION.STATE.equal(transferOperation.getState().getDbCode())))
                .execute() > 0;
    }

    public void updateState(TransferOperation transferOperation, TransferOperationState newState) {
        requireNonNull(transferOperation, "transferOperation is required");
        requireNonNull(newState, "newState is required");
        log.info("updateState(transferOperation={}, newState={})", transferOperation, newState);

        context
                .update(TRANSFER_OPERATION)
                .set(TRANSFER_OPERATION.STATE, newState.getDbCode())
                .where(TRANSFER_OPERATION.ID.equal(transferOperation.getId()))
                .execute();
    }

    public void updateStateAndErrorCode(
            TransferOperation transferOperation,
            TransferOperationState newState,
            TransferOperationErrorCode errorCode
    ) {
        requireNonNull(transferOperation, "transferOperation is required");
        requireNonNull(newState, "newState is required");
        requireNonNull(errorCode, "errorCode is required");
        log.info("updateStateAndErrorCode(transferOperation={}, newState={}, errorCode={})",
                transferOperation, newState, errorCode
        );

        context
                .update(TRANSFER_OPERATION)
                .set(TRANSFER_OPERATION.STATE, newState.getDbCode())
                .set(TRANSFER_OPERATION.ERROR_CODE, errorCode.getDbCode())
                .where(TRANSFER_OPERATION.ID.equal(transferOperation.getId()))
                .execute();
    }
}
