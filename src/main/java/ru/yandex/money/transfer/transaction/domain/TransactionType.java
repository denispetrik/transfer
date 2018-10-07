package ru.yandex.money.transfer.transaction.domain;

import static java.util.Objects.requireNonNull;
import static ru.yandex.money.transfer.utils.EnumUtils.byCode;

/**
 * @author petrique
 */
public enum TransactionType {

    /**
     * Founds movement from account
     */
    WITHDRAWAL(1, "Withdrawal"),

    /**
     * Founds movement to account
     */
    DEPOSITION(2, "Deposition");

    private final Integer dbCode;
    private final String externalCode;

    TransactionType(Integer dbCode, String externalCode) {
        this.dbCode = requireNonNull(dbCode, "dbCode is required");
        this.externalCode = requireNonNull(externalCode, "externalCode is required");
    }

    public Integer getDbCode() {
        return dbCode;
    }

    public String getExternalCode() {
        return externalCode;
    }

    public static TransactionType byDbCode(Integer dbCode) {
        return byCode(TransactionType.class, TransactionType::getDbCode, dbCode);
    }

    public static TransactionType byExternalCode(String externalCode) {
        return byCode(TransactionType.class, TransactionType::getExternalCode, externalCode);
    }
}
