package ru.yandex.money.transfer.core.command;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.annotation.Nullable;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

/**
 * @author petrique
 */
public class Response<ResultT> {

    private final ResponseStatus status;
    @Nullable
    private final ResultT result;
    @Nullable
    private final ResponseError error;

    private Response(
            ResponseStatus status,
            @Nullable ResultT result,
            @Nullable ResponseError error
    ) {
        this.status = requireNonNull(status, "status is required");
        this.result = result;
        this.error = error;
    }

    public static <ResultT> Response<ResultT> successWith(ResultT result) {
        requireNonNull(result, "result is required");
        return new Response<>(ResponseStatus.SUCCESS, result, null);
    }

    public static <ResultT> Response<ResultT> failWith(ResponseError error) {
        requireNonNull(error, "error is required");
        return new Response<>(ResponseStatus.FAIL, null, error);
    }

    @JsonProperty("status")
    public ResponseStatus getStatus() {
        return status;
    }

    @JsonProperty("result")
    public Optional<ResultT> getResult() {
        return Optional.ofNullable(result);
    }

    @JsonProperty("error")
    public Optional<ResponseError> getError() {
        return Optional.ofNullable(error);
    }
}
