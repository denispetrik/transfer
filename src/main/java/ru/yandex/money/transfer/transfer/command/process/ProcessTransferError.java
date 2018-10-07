package ru.yandex.money.transfer.transfer.command.process;

import ru.yandex.money.transfer.core.command.ResponseError;

/**
 * @author petrique
 */
public interface ProcessTransferError {

    ResponseError OPERATION_NOT_FOUND
            = ResponseError.with("OperationNotFound", "Transfer operation not found");
}
