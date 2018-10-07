package ru.yandex.money.transfer.transfer.command.process;

import com.fasterxml.jackson.annotation.JsonProperty;
import ru.yandex.money.transfer.transfer.domain.TransferOperationErrorCode;
import ru.yandex.money.transfer.transfer.domain.TransferOperationState;

import javax.annotation.Nullable;

import static java.util.Objects.requireNonNull;

/**
 * @author petrique
 */
public class TransferResult {

    private final TransferOperationState state;
    @Nullable
    private final TransferOperationErrorCode errorCode;

    TransferResult(TransferOperationState state, @Nullable TransferOperationErrorCode errorCode) {
        this.state = requireNonNull(state, "state is required");
        this.errorCode = errorCode;
    }

    TransferResult(TransferOperationState state) {
        this(state, null);
    }

    @JsonProperty("state")
    public TransferOperationState getState() {
        return state;
    }

    @JsonProperty("errorCode")
    @Nullable
    public TransferOperationErrorCode getErrorCode() {
        return errorCode;
    }
}
