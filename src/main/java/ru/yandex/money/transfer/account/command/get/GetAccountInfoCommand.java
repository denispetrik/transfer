package ru.yandex.money.transfer.account.command.get;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import ru.yandex.money.transfer.account.domain.Account;
import ru.yandex.money.transfer.account.service.AccountService;
import ru.yandex.money.transfer.core.command.Command;
import ru.yandex.money.transfer.core.command.Response;

import java.util.Optional;

import static org.springframework.http.ResponseEntity.badRequest;
import static org.springframework.http.ResponseEntity.ok;
import static ru.yandex.money.transfer.account.command.get.GetAccountInfoError.ACCOUNT_NOT_FOUND;
import static ru.yandex.money.transfer.core.command.Response.failWith;
import static ru.yandex.money.transfer.core.command.Response.successWith;

/**
 * @author petrique
 */
public class GetAccountInfoCommand implements Command<GetAccountInfoRequest, AccountInfo> {

    private static final Logger log = LoggerFactory.getLogger(GetAccountInfoCommand.class);

    private final AccountService accountService;

    public GetAccountInfoCommand(AccountService accountService) {
        this.accountService = accountService;
    }

    @Override
    public ResponseEntity<Response<AccountInfo>> execute(GetAccountInfoRequest request) {
        Optional<Account> account = accountService.findAccountByNumber(request.getAccountNumber());
        if (!account.isPresent()) {
            log.info("account not found: number={}", request.getAccountNumber());
            return badRequest().body(failWith(ACCOUNT_NOT_FOUND));
        }
        return ok(successWith(new AccountInfo(account.get())));
    }
}
