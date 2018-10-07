package ru.yandex.money.transfer.account.command.get;

import static java.util.Objects.requireNonNull;

/**
 * @author petrique
 */
public class GetAccountInfoRequest {

    private final String accountNumber;

    public GetAccountInfoRequest(String accountNumber) {
        this.accountNumber = requireNonNull(accountNumber, "accountNumber is required");
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    @Override
    public String toString() {
        return "GetAccountInfoRequest{" +
                "accountNumber='" + accountNumber + '\'' +
                '}';
    }
}
