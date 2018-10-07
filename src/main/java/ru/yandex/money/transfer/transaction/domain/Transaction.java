package ru.yandex.money.transfer.transaction.domain;

import ru.yandex.money.transfer.common.domain.MonetaryAmount;

import static java.util.Objects.requireNonNull;

/**
 * @author petrique
 */
public final class Transaction {

    private final Long id;
    private final TransactionType type;
    private final String externalId;
    private final Long accountId;
    private final MonetaryAmount amount;

    private Transaction(
            Long id,
            TransactionType type,
            String externalId,
            Long accountId,
            MonetaryAmount amount
    ) {
        this.id = requireNonNull(id, "id is required");
        this.type = requireNonNull(type, "type is required");
        this.externalId = requireNonNull(externalId, "externalId is required");
        this.accountId = requireNonNull(accountId, "accountId is required");
        this.amount = requireNonNull(amount, "amount is required");
    }

    public static Builder builder() {
        return new Builder();
    }

    public Long getId() {
        return id;
    }

    public TransactionType getType() {
        return type;
    }

    public String getExternalId() {
        return externalId;
    }

    public Long getAccountId() {
        return accountId;
    }

    public MonetaryAmount getAmount() {
        return amount;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", type=" + type +
                ", externalId='" + externalId + '\'' +
                ", accountId=" + accountId +
                ", amount=" + amount +
                '}';
    }


    public static final class Builder {

        private Long id;
        private TransactionType type;
        private String externalId;
        private Long accountId;
        private MonetaryAmount amount;

        private Builder() {
        }

        public Builder withId(Long id) {
            this.id = id;
            return this;
        }

        public Builder withType(TransactionType type) {
            this.type = type;
            return this;
        }

        public Builder withExternalId(String externalId) {
            this.externalId = externalId;
            return this;
        }

        public Builder withAccountId(Long accountId) {
            this.accountId = accountId;
            return this;
        }

        public Builder withAmount(MonetaryAmount amount) {
            this.amount = amount;
            return this;
        }

        public Transaction build() {
            return new Transaction(id, type, externalId, accountId, amount);
        }
    }
}
