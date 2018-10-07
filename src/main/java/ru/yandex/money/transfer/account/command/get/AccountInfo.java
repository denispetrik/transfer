package ru.yandex.money.transfer.account.command.get;

import com.fasterxml.jackson.annotation.JsonProperty;
import ru.yandex.money.transfer.account.domain.Account;
import ru.yandex.money.transfer.common.domain.MonetaryAmount;

import static java.util.Objects.requireNonNull;

/**
 * @author petrique
 */
public class AccountInfo {

    private final String number;
    private final MonetaryAmount balance;

    AccountInfo(Account account) {
        requireNonNull(account, "account is required");
        this.number = account.getNumber();
        this.balance = account.getBalance();
    }

    @JsonProperty("number")
    public String getNumber() {
        return number;
    }

    @JsonProperty("balance")
    public MonetaryAmount getBalance() {
        return balance;
    }
}
