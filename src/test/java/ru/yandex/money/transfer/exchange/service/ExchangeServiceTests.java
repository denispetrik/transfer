package ru.yandex.money.transfer.exchange.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.yandex.money.transfer.common.domain.MonetaryAmount;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.yandex.money.transfer.common.domain.Currency.EUR;
import static ru.yandex.money.transfer.common.domain.Currency.USD;

/**
 * @author petrique
 */
class ExchangeServiceTests {

    private static ExchangeService exchangeService;

    @BeforeAll
    static void init() {
        exchangeService = new ExchangeService();
    }

    @Test
    void should_notExchange_when_newCurrencyIsTheSame() {
        MonetaryAmount amount = MonetaryAmount.of("42.00", EUR);

        MonetaryAmount exchanged = exchangeService.exchange(amount, EUR);

        assertEquals(exchanged, amount);
    }

    @Test
    void should_exchangeEuroToDollars() {
        MonetaryAmount amount = MonetaryAmount.of("42.00", EUR);

        MonetaryAmount exchanged = exchangeService.exchange(amount, USD);

        assertEquals(MonetaryAmount.of("84.00", USD), exchanged);
    }

    @Test
    void should_exchangeDollarsToEuro() {
        MonetaryAmount amount = MonetaryAmount.of("42.00", USD);

        MonetaryAmount exchanged = exchangeService.exchange(amount, EUR);

        assertEquals(MonetaryAmount.of("21.00", EUR), exchanged);
    }
}
