package ru.yandex.money.transfer.transaction;

import org.jooq.DSLContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.yandex.money.transfer.account.service.AccountService;
import ru.yandex.money.transfer.core.database.TransactionHandler;
import ru.yandex.money.transfer.transaction.dao.TransactionDao;
import ru.yandex.money.transfer.transaction.service.TransactionService;

/**
 * @author petrique
 */
@Configuration
public class TransactionConfiguration {

    @Bean
    TransactionDao transactionDao(DSLContext context) {
        return new TransactionDao(context);
    }

    @Bean
    TransactionService transactionService(
            TransactionHandler transactionHandler,
            TransactionDao transactionDao,
            AccountService accountService
    ) {
        return new TransactionService(transactionHandler, transactionDao, accountService);
    }
}
