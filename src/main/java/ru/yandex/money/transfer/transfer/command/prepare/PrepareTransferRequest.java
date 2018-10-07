package ru.yandex.money.transfer.transfer.command.prepare;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import ru.yandex.money.transfer.common.domain.MonetaryAmount;

import javax.validation.constraints.NotNull;

/**
 * @author petrique
 */
public class PrepareTransferRequest {

    @NotNull
    private final String senderAccountNumber;

    @NotNull
    private final String receiverAccountNumber;

    @NotNull
    private final MonetaryAmount amount;

    @JsonCreator
    public PrepareTransferRequest(
            @JsonProperty("senderAccountNumber") String senderAccountNumber,
            @JsonProperty("receiverAccountNumber") String receiverAccountNumber,
            @JsonProperty("amount") MonetaryAmount amount
    ) {
        this.senderAccountNumber = senderAccountNumber;
        this.receiverAccountNumber = receiverAccountNumber;
        this.amount = amount;
    }

    public String getSenderAccountNumber() {
        return senderAccountNumber;
    }

    public String getReceiverAccountNumber() {
        return receiverAccountNumber;
    }

    public MonetaryAmount getAmount() {
        return amount;
    }

    @Override
    public String toString() {
        return "PrepareTransferRequest{" +
                "senderAccountNumber=" + senderAccountNumber +
                ", receiverAccountNumber=" + receiverAccountNumber +
                ", amount=" + amount +
                '}';
    }
}
