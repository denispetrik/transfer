package ru.yandex.money.transfer.transfer.command.prepare;

import ru.yandex.money.transfer.core.command.ResponseError;

/**
 * @author petrique
 */
public interface PrepareTransferError {

    ResponseError SENDER_NOT_FOUND = ResponseError.with("SenderNotFound", "Sender account not found");

    ResponseError RECEIVER_NOT_FOUND = ResponseError.with("ReceiverNotFound", "Receiver account not found");

    ResponseError RECEIVER_MUST_DIFFER = ResponseError.with("ReceiverMustDiffer", "Receiver must differ from sender");
}
