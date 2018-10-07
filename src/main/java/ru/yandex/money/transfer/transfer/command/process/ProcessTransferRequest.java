package ru.yandex.money.transfer.transfer.command.process;

import static java.util.Objects.requireNonNull;

/**
 * @author petrique
 */
public class ProcessTransferRequest {

    private final String transferId;

    public ProcessTransferRequest(String transferId) {
        this.transferId = requireNonNull(transferId, "transferId is required");
    }

    public String getTransferId() {
        return transferId;
    }

    @Override
    public String toString() {
        return "ProcessTransferRequest{" +
                "transferId='" + transferId + '\'' +
                '}';
    }
}
