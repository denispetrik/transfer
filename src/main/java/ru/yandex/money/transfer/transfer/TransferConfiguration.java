package ru.yandex.money.transfer.transfer;

import org.jooq.DSLContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.yandex.money.transfer.account.service.AccountService;
import ru.yandex.money.transfer.core.database.TransactionHandler;
import ru.yandex.money.transfer.exchange.service.ExchangeService;
import ru.yandex.money.transfer.transaction.service.TransactionService;
import ru.yandex.money.transfer.transfer.command.prepare.PrepareTransferCommand;
import ru.yandex.money.transfer.transfer.command.process.ProcessTransferCommand;
import ru.yandex.money.transfer.transfer.controller.TransferController;
import ru.yandex.money.transfer.transfer.dao.TransferOperationDao;

/**
 * @author petrique
 */
@Configuration
public class TransferConfiguration {

    @Bean
    TransferOperationDao transferOperationDao(DSLContext context) {
        return new TransferOperationDao(context);
    }

    @Bean
    PrepareTransferCommand prepareTransferCommand(
            AccountService accountService,
            TransactionHandler transactionHandler,
            TransferOperationDao transferOperationDao
    ) {
        return new PrepareTransferCommand(accountService, transactionHandler, transferOperationDao);
    }

    @Bean
    ProcessTransferCommand processTransferCommand(
            AccountService accountService,
            ExchangeService exchangeService,
            TransactionHandler transactionHandler,
            TransferOperationDao transferOperationDao,
            TransactionService transactionService
    ) {
        return new ProcessTransferCommand(
                accountService,
                exchangeService,
                transactionHandler,
                transferOperationDao,
                transactionService
        );
    }

    @Bean
    TransferController transferController(
            PrepareTransferCommand prepareTransferCommand,
            ProcessTransferCommand processTransferCommand
    ) {
        return new TransferController(prepareTransferCommand, processTransferCommand);
    }
}
