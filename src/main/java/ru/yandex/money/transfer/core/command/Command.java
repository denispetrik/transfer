package ru.yandex.money.transfer.core.command;

import org.springframework.http.ResponseEntity;

/**
 * @author petrique
 */
public interface Command<RequestT, ResultT> {

    ResponseEntity<Response<ResultT>> execute(RequestT request);
}
