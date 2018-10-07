package ru.yandex.money.transfer.transfer.domain;

import ru.yandex.money.transfer.common.domain.MonetaryAmount;

import javax.annotation.Nullable;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

/**
 * @author petrique
 */
public final class TransferOperation {

    private final Long id;
    private final TransferOperationState state;
    private final String externalId;
    private final Long senderAccountId;
    private final Long receiverAccountId;
    private final MonetaryAmount amount;
    @Nullable
    private final TransferOperationErrorCode errorCode;

    private TransferOperation(
            Long id,
            TransferOperationState state,
            String externalId,
            Long senderAccountId,
            Long receiverAccountId,
            MonetaryAmount amount,
            @Nullable TransferOperationErrorCode errorCode
    ) {
        this.id = requireNonNull(id, "id is required");
        this.state = requireNonNull(state, "state is required");
        this.externalId = requireNonNull(externalId, "externalId is required");
        this.senderAccountId = requireNonNull(senderAccountId, "senderAccountId is required");
        this.receiverAccountId = requireNonNull(receiverAccountId, "receiverAccountId is required");
        this.amount = requireNonNull(amount, "amount is required");
        this.errorCode = errorCode;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Long getId() {
        return id;
    }

    public TransferOperationState getState() {
        return state;
    }

    public String getExternalId() {
        return externalId;
    }

    public Long getSenderAccountId() {
        return senderAccountId;
    }

    public Long getReceiverAccountId() {
        return receiverAccountId;
    }

    public MonetaryAmount getAmount() {
        return amount;
    }

    public Optional<TransferOperationErrorCode> getErrorCode() {
        return Optional.ofNullable(errorCode);
    }

    @Override
    public String toString() {
        return "TransferOperation{" +
                "id=" + id +
                ", state=" + state +
                ", externalId='" + externalId + '\'' +
                ", senderAccountId=" + senderAccountId +
                ", receiverAccountId=" + receiverAccountId +
                ", amount=" + amount +
                ", errorCode=" + errorCode +
                '}';
    }


    public static final class Builder {

        private Long id;
        private TransferOperationState state;
        private String externalId;
        private Long senderAccountId;
        private Long receiverAccountId;
        private MonetaryAmount amount;
        private TransferOperationErrorCode errorCode;

        private Builder() {
        }

        public Builder withId(Long id) {
            this.id = id;
            return this;
        }

        public Builder withState(TransferOperationState state) {
            this.state = state;
            return this;
        }

        public Builder withExternalId(String externalId) {
            this.externalId = externalId;
            return this;
        }

        public Builder withSenderAccountId(Long senderAccountId) {
            this.senderAccountId = senderAccountId;
            return this;
        }

        public Builder withReceiverAccountId(Long receiverAccountId) {
            this.receiverAccountId = receiverAccountId;
            return this;
        }

        public Builder withAmount(MonetaryAmount amount) {
            this.amount = amount;
            return this;
        }

        public Builder withErrorCode(@Nullable TransferOperationErrorCode errorCode) {
            this.errorCode = errorCode;
            return this;
        }

        public TransferOperation build() {
            return new TransferOperation(
                    id, state, externalId, senderAccountId, receiverAccountId, amount, errorCode
            );
        }
    }
}
