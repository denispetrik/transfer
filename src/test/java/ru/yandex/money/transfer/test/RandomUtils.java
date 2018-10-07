package ru.yandex.money.transfer.test;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author petrique
 */
public final class RandomUtils {

    private static final AtomicLong LONG_SEQUENCE = new AtomicLong(0);

    private RandomUtils() {
    }

    public static long randomLong() {
        return LONG_SEQUENCE.incrementAndGet();
    }

    public static String randomAccountNumber() {
        return Long.toString(randomLong());
    }

    public static String randomUUID() {
        return UUID.randomUUID().toString();
    }
}
