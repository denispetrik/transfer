package ru.yandex.money.transfer.transfer.command.prepare;

import com.fasterxml.jackson.annotation.JsonProperty;
import ru.yandex.money.transfer.account.domain.Account;
import ru.yandex.money.transfer.common.domain.MonetaryAmount;
import ru.yandex.money.transfer.transfer.domain.TransferOperation;

import static java.util.Objects.requireNonNull;

/**
 * @author petrique
 */
public final class TransferContract {

    private final String transferId;
    private final String senderAccountNumber;
    private final String receiverAccountNumber;
    private final MonetaryAmount amount;

    TransferContract(
            TransferOperation transferOperation,
            Account senderAccount,
            Account receiverAccount
    ) {
        requireNonNull(transferOperation, "transferOperation is required");
        requireNonNull(senderAccount, "senderAccount is required");
        requireNonNull(receiverAccount, "receiverAccount is required");

        this.transferId = transferOperation.getExternalId();
        this.senderAccountNumber = senderAccount.getNumber();
        this.receiverAccountNumber = receiverAccount.getNumber();
        this.amount = transferOperation.getAmount();
    }

    @JsonProperty("transferId")
    public String getTransferId() {
        return transferId;
    }

    @JsonProperty("senderAccountNumber")
    public String getSenderAccountNumber() {
        return senderAccountNumber;
    }

    @JsonProperty("receiverAccountNumber")
    public String getReceiverAccountNumber() {
        return receiverAccountNumber;
    }

    @JsonProperty("amount")
    public MonetaryAmount getAmount() {
        return amount;
    }
}
