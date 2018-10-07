package ru.yandex.money.transfer.transfer.domain;

import static java.util.Objects.requireNonNull;
import static ru.yandex.money.transfer.utils.EnumUtils.byCode;

/**
 * @author petrique
 */
public enum TransferOperationState {

    /**
     * Operation was created
     */
    CREATED(1, "Created"),

    /**
     * Operation is being processed
     */
    PROCESSING(2, "Processing"),

    /**
     * Operation was successfully processed
     */
    SUCCESSFUL(3, "Successful"),

    /**
     * Operation failed
     */
    FAILED(4, "Failed");

    private final Integer dbCode;
    private final String externalCode;

    TransferOperationState(Integer dbCode, String externalCode) {
        this.dbCode = requireNonNull(dbCode, "dbCode is required");
        this.externalCode = requireNonNull(externalCode, "externalCode is required");
    }

    public Integer getDbCode() {
        return dbCode;
    }

    public String getExternalCode() {
        return externalCode;
    }

    public static TransferOperationState byDbCode(Integer dbCode) {
        return byCode(TransferOperationState.class, TransferOperationState::getDbCode, dbCode);
    }

    public static TransferOperationState byExternalCode(String externalCode) {
        return byCode(TransferOperationState.class, TransferOperationState::getExternalCode, externalCode);
    }
}
