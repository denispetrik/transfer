package ru.yandex.money.transfer.common.domain;

import java.math.BigDecimal;
import java.util.Objects;

import static java.util.Objects.requireNonNull;

/**
 * @author petrique
 */
public final class MonetaryAmount {

    private final BigDecimal value;
    private final Currency currency;

    private MonetaryAmount(BigDecimal value, Currency currency) {
        this.value = requireNonNull(value, "value is required");
        this.currency = requireNonNull(currency, "currency is required");
    }

    public static MonetaryAmount of(BigDecimal value, Currency currency) {
        return new MonetaryAmount(value, currency);
    }

    public static MonetaryAmount of(String value, Currency currency) {
        return new MonetaryAmount(new BigDecimal(value), currency);
    }

    public BigDecimal getValue() {
        return value;
    }

    public Currency getCurrency() {
        return currency;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MonetaryAmount that = (MonetaryAmount) o;
        return Objects.equals(value, that.value) &&
                currency == that.currency;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, currency);
    }

    @Override
    public String toString() {
        return "MonetaryAmount{" +
                "value=" + value +
                ", currency=" + currency +
                '}';
    }
}
