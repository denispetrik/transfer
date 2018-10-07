package ru.yandex.money.transfer.account.command.get;

import ru.yandex.money.transfer.core.command.ResponseError;

/**
 * @author petrique
 */
public interface GetAccountInfoError {

    ResponseError ACCOUNT_NOT_FOUND = ResponseError.with("AccountNotFound", "Account not found");
}
