package ru.yandex.money.transfer.transfer.domain;

import static java.util.Objects.requireNonNull;
import static ru.yandex.money.transfer.utils.EnumUtils.byCode;

/**
 * @author petrique
 */
public enum TransferOperationErrorCode {

    /**
     * Not enough funds on sender account
     */
    NOT_ENOUGH_FUNDS(1, "NotEnoughFunds");

    private final Integer dbCode;
    private final String externalCode;

    TransferOperationErrorCode(Integer dbCode, String externalCode) {
        this.dbCode = requireNonNull(dbCode, "dbCode is required");
        this.externalCode = requireNonNull(externalCode, "externalCode is required");
    }

    public Integer getDbCode() {
        return dbCode;
    }

    public String getExternalCode() {
        return externalCode;
    }

    public static TransferOperationErrorCode byDbCode(Integer dbCode) {
        return byCode(TransferOperationErrorCode.class, TransferOperationErrorCode::getDbCode, dbCode);
    }
}
