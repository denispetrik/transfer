package ru.yandex.money.transfer.account.domain;

import ru.yandex.money.transfer.common.domain.MonetaryAmount;

import static java.util.Objects.requireNonNull;

/**
 * @author petrique
 */
public final class Account {

    private final Long id;
    private final String number;
    private final MonetaryAmount balance;

    private Account(Long id, String number, MonetaryAmount balance) {
        this.id = requireNonNull(id, "id is required");
        this.number = requireNonNull(number, "number is required");
        this.balance = requireNonNull(balance, "balance is required");
    }

    public static Builder builder() {
        return new Builder();
    }

    public Builder toBuilder() {
        return builder()
                .withId(id)
                .withNumber(number)
                .withBalance(balance);
    }

    public Long getId() {
        return id;
    }

    public String getNumber() {
        return number;
    }

    public MonetaryAmount getBalance() {
        return balance;
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", number='" + number + '\'' +
                ", balance=" + balance +
                '}';
    }


    public static final class Builder {

        private Long id;
        private String number;
        private MonetaryAmount balance;

        private Builder() {
        }

        public Builder withId(Long id) {
            this.id = id;
            return this;
        }

        public Builder withNumber(String number) {
            this.number = number;
            return this;
        }

        public Builder withBalance(MonetaryAmount balance) {
            this.balance = balance;
            return this;
        }

        public Account build() {
            return new Account(id, number, balance);
        }
    }
}
