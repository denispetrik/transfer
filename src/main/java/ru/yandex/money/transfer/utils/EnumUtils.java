package ru.yandex.money.transfer.utils;

import java.util.Optional;
import java.util.function.Function;

import static java.lang.String.format;
import static java.util.Arrays.stream;
import static java.util.Objects.requireNonNull;

/**
 * @author petrique
 */
public final class EnumUtils {

    private EnumUtils() {
    }

    public static <EnumT extends Enum, CodeT> Optional<EnumT> optionalByCode(
            Class<EnumT> enumClass,
            Function<EnumT, CodeT> getter,
            CodeT code
    ) {
        requireNonNull(enumClass, "enumClass is required");
        requireNonNull(getter, "getter is required");
        requireNonNull(code, "code is required");

        return stream(enumClass.getEnumConstants())
                .filter(c -> getter.apply(c).equals(code))
                .findFirst();
    }

    public static <EnumT extends Enum, CodeT> EnumT byCode(
            Class<EnumT> enumClass,
            Function<EnumT, CodeT> getter,
            CodeT code
    ) {
        return optionalByCode(enumClass, getter, code)
                .orElseThrow(() -> new IllegalArgumentException(
                        format("Illegal enum code: enumClass=%s, code=%s", enumClass.getSimpleName(), code)
                ));
    }
}
