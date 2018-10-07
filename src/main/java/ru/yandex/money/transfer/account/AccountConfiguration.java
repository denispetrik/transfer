package ru.yandex.money.transfer.account;

import org.jooq.DSLContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.yandex.money.transfer.account.command.get.GetAccountInfoCommand;
import ru.yandex.money.transfer.account.controller.AccountController;
import ru.yandex.money.transfer.account.dao.AccountDao;
import ru.yandex.money.transfer.account.service.AccountService;
import ru.yandex.money.transfer.core.database.TransactionHandler;

/**
 * @author petrique
 */
@Configuration
public class AccountConfiguration {

    @Bean
    AccountDao accountDao(DSLContext context) {
        return new AccountDao(context);
    }

    @Bean
    AccountService accountService(TransactionHandler transactionHandler, AccountDao accountDao) {
        return new AccountService(transactionHandler, accountDao);
    }

    @Bean
    GetAccountInfoCommand getAccountInfoCommand(AccountService accountService) {
        return new GetAccountInfoCommand(accountService);
    }

    @Bean
    AccountController accountController(GetAccountInfoCommand getAccountInfoCommand) {
        return new AccountController(getAccountInfoCommand);
    }
}
