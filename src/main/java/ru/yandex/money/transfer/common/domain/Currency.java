package ru.yandex.money.transfer.common.domain;

import static java.util.Objects.requireNonNull;
import static ru.yandex.money.transfer.utils.EnumUtils.byCode;

/**
 * @author petrique
 */
public enum Currency {

    /**
     * Euro
     */
    EUR("EUR"),

    /**
     * American dollar
     */
    USD("USD");

    private final String isoCode;

    Currency(String isoCode) {
        this.isoCode = requireNonNull(isoCode, "isoCode is required");
    }

    public String getIsoCode() {
        return isoCode;
    }

    public static Currency byIsoCode(String isoCode) {
        return byCode(Currency.class, Currency::getIsoCode, isoCode);
    }
}
