package ru.yandex.money.transfer.transfer.command.prepare;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import ru.yandex.money.transfer.account.domain.Account;
import ru.yandex.money.transfer.account.service.AccountService;
import ru.yandex.money.transfer.common.domain.MonetaryAmount;
import ru.yandex.money.transfer.core.command.Command;
import ru.yandex.money.transfer.core.command.Response;
import ru.yandex.money.transfer.core.database.TransactionHandler;
import ru.yandex.money.transfer.transfer.dao.TransferOperationDao;
import ru.yandex.money.transfer.transfer.domain.TransferOperation;
import ru.yandex.money.transfer.transfer.domain.TransferOperationState;

import java.util.Optional;
import java.util.UUID;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.ResponseEntity.badRequest;
import static org.springframework.http.ResponseEntity.status;
import static ru.yandex.money.transfer.core.command.Response.failWith;
import static ru.yandex.money.transfer.core.command.Response.successWith;
import static ru.yandex.money.transfer.transfer.command.prepare.PrepareTransferError.RECEIVER_MUST_DIFFER;
import static ru.yandex.money.transfer.transfer.command.prepare.PrepareTransferError.RECEIVER_NOT_FOUND;
import static ru.yandex.money.transfer.transfer.command.prepare.PrepareTransferError.SENDER_NOT_FOUND;

/**
 * @author petrique
 */
public class PrepareTransferCommand implements Command<PrepareTransferRequest, TransferContract> {

    private static final Logger log = LoggerFactory.getLogger(PrepareTransferCommand.class);

    private final AccountService accountService;
    private final TransactionHandler transactionHandler;
    private final TransferOperationDao transferOperationDao;

    public PrepareTransferCommand(
            AccountService accountService,
            TransactionHandler transactionHandler,
            TransferOperationDao transferOperationDao
    ) {
        this.accountService = accountService;
        this.transactionHandler = transactionHandler;
        this.transferOperationDao = transferOperationDao;
    }

    @Override
    public ResponseEntity<Response<TransferContract>> execute(PrepareTransferRequest request) {
        Optional<Account> optionalSenderAccount
                = accountService.findAccountByNumber(request.getSenderAccountNumber());
        if (!optionalSenderAccount.isPresent()) {
            log.info("sender account not found: accountNumber={}", request.getSenderAccountNumber());
            return badRequest().body(failWith(SENDER_NOT_FOUND));
        }
        Account senderAccount = optionalSenderAccount.get();

        Optional<Account> optionalReceiverAccount
                = accountService.findAccountByNumber(request.getReceiverAccountNumber());
        if (!optionalReceiverAccount.isPresent()) {
            log.info("receiver account not found: accountNumber={}", request.getReceiverAccountNumber());
            return badRequest().body(failWith(RECEIVER_NOT_FOUND));
        }
        Account receiverAccount = optionalReceiverAccount.get();

        if (senderAccount.getId().equals(receiverAccount.getId())) {
            log.info("receiver must differ from sender");
            return badRequest().body(failWith(RECEIVER_MUST_DIFFER));
        }

        TransferOperation transferOperation = transactionHandler.inTransaction(() -> {
            TransferOperation operation = createTransferOperation(senderAccount, receiverAccount, request.getAmount());
            transferOperationDao.save(operation);
            return operation;
        });

        log.info("transfer operation has been created: transferOperation={}", transferOperation);

        return status(CREATED).body(successWith(
                new TransferContract(transferOperation, senderAccount, receiverAccount)
        ));
    }

    private TransferOperation createTransferOperation(
            Account senderAccount,
            Account receiverAccount,
            MonetaryAmount amount
    ) {
        return TransferOperation.builder()
                .withId(transferOperationDao.generateId())
                .withState(TransferOperationState.CREATED)
                .withExternalId(generateExternalId())
                .withSenderAccountId(senderAccount.getId())
                .withReceiverAccountId(receiverAccount.getId())
                .withAmount(amount)
                .build();
    }

    private static String generateExternalId() {
        return UUID.randomUUID().toString();
    }
}
