package ru.yandex.money.transfer.account.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.money.transfer.account.command.get.AccountInfo;
import ru.yandex.money.transfer.account.command.get.GetAccountInfoCommand;
import ru.yandex.money.transfer.account.command.get.GetAccountInfoRequest;
import ru.yandex.money.transfer.core.command.Response;

import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * @author petrique
 */
@RestController
@RequestMapping(
        path = "/api/v1/accounts",
        produces = APPLICATION_JSON_VALUE
)
public class AccountController {

    private static final Logger log = LoggerFactory.getLogger(AccountController.class);

    private final GetAccountInfoCommand getAccountInfoCommand;

    public AccountController(GetAccountInfoCommand getAccountInfoCommand) {
        this.getAccountInfoCommand = getAccountInfoCommand;
    }

    @GetMapping(path = "/{accountNumber}", consumes = APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<Response<AccountInfo>> getAccountInfo(
            @PathVariable("accountNumber") String accountNumber
    ) {
        GetAccountInfoRequest request = new GetAccountInfoRequest(accountNumber);
        log.info("getting account info: request={}", request);
        return getAccountInfoCommand.execute(request);
    }
}
