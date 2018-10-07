package ru.yandex.money.transfer.core.command;

import com.fasterxml.jackson.annotation.JsonProperty;

import static java.util.Objects.requireNonNull;

/**
 * @author petrique
 */
public class ResponseError {

    private final String code;
    private final String message;

    private ResponseError(String code, String message) {
        this.code = requireNonNull(code, "code is required");
        this.message = requireNonNull(message, "message is required");
    }

    public static ResponseError with(String code, String message) {
        return new ResponseError(code, message);
    }

    @JsonProperty("code")
    public String getCode() {
        return code;
    }

    @JsonProperty("message")
    public String getMessage() {
        return message;
    }
}
