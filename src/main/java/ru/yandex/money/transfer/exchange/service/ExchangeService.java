package ru.yandex.money.transfer.exchange.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.yandex.money.transfer.common.domain.Currency;
import ru.yandex.money.transfer.common.domain.MonetaryAmount;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static java.util.Objects.requireNonNull;

/**
 * @author petrique
 */
public class ExchangeService {

    private static final Logger log = LoggerFactory.getLogger(ExchangeService.class);

    public MonetaryAmount exchange(MonetaryAmount amount, Currency newCurrency) {
        requireNonNull(amount, "amount is required");
        requireNonNull(newCurrency, "newCurrency is required");
        log.info("exchange(amount={}, newCurrency={})", amount, newCurrency);

        if (amount.getCurrency() == newCurrency) {
            return amount;
        }

        if (amount.getCurrency() == Currency.EUR) {
            return MonetaryAmount.of(
                    amount.getValue().multiply(new BigDecimal("2")),
                    Currency.USD
            );
        } else {
            return MonetaryAmount.of(
                    amount.getValue().divide(new BigDecimal("2"), RoundingMode.CEILING),
                    Currency.EUR
            );
        }
    }
}
