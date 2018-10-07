package ru.yandex.money.transfer.core.command;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author petrique
 */
public enum ResponseStatus {

    /**
     * Response is successful
     */
    @JsonProperty("Success")
    SUCCESS,

    /**
     * Response is failed
     */
    @JsonProperty("Fail")
    FAIL
}
