package ru.yandex.money.transfer.transfer.command.process;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import ru.yandex.money.transfer.account.domain.Account;
import ru.yandex.money.transfer.account.service.AccountService;
import ru.yandex.money.transfer.common.domain.MonetaryAmount;
import ru.yandex.money.transfer.core.command.Command;
import ru.yandex.money.transfer.core.command.Response;
import ru.yandex.money.transfer.core.command.ResponseError;
import ru.yandex.money.transfer.core.database.TransactionHandler;
import ru.yandex.money.transfer.exchange.service.ExchangeService;
import ru.yandex.money.transfer.transaction.service.TransactionService;
import ru.yandex.money.transfer.transfer.dao.TransferOperationDao;
import ru.yandex.money.transfer.transfer.domain.TransferOperation;
import ru.yandex.money.transfer.transfer.domain.TransferOperationState;

import java.util.Optional;

import static org.springframework.http.ResponseEntity.badRequest;
import static org.springframework.http.ResponseEntity.ok;
import static ru.yandex.money.transfer.core.command.Response.failWith;
import static ru.yandex.money.transfer.core.command.Response.successWith;
import static ru.yandex.money.transfer.transfer.command.process.ProcessTransferError.OPERATION_NOT_FOUND;
import static ru.yandex.money.transfer.transfer.domain.TransferOperationErrorCode.NOT_ENOUGH_FUNDS;
import static ru.yandex.money.transfer.transfer.domain.TransferOperationState.FAILED;
import static ru.yandex.money.transfer.transfer.domain.TransferOperationState.PROCESSING;
import static ru.yandex.money.transfer.transfer.domain.TransferOperationState.SUCCESSFUL;

/**
 * @author petrique
 */
public class ProcessTransferCommand implements Command<ProcessTransferRequest, TransferResult> {

    private static final Logger log = LoggerFactory.getLogger(ProcessTransferCommand.class);

    private final AccountService accountService;
    private final ExchangeService exchangeService;
    private final TransactionHandler transactionHandler;
    private final TransferOperationDao transferOperationDao;
    private final TransactionService transactionService;

    public ProcessTransferCommand(
            AccountService accountService,
            ExchangeService exchangeService,
            TransactionHandler transactionHandler,
            TransferOperationDao transferOperationDao,
            TransactionService transactionService
    ) {
        this.accountService = accountService;
        this.exchangeService = exchangeService;
        this.transactionHandler = transactionHandler;
        this.transferOperationDao = transferOperationDao;
        this.transactionService = transactionService;
    }

    @Override
    public ResponseEntity<Response<TransferResult>> execute(ProcessTransferRequest request) {
        Optional<TransferOperation> optionalTransferOperation
                = transferOperationDao.findByExternalId(request.getTransferId());
        if (!optionalTransferOperation.isPresent()) {
            log.info("transfer operation not found: externalId={}", request.getTransferId());
            return error(OPERATION_NOT_FOUND);
        }
        TransferOperation transferOperation = optionalTransferOperation.get();

        if (transferOperation.getState() != TransferOperationState.CREATED) {
            log.info("transfer operation has already been processed");
            return success(new TransferResult(
                    transferOperation.getState(),
                    transferOperation.getErrorCode().orElse(null)
            ));
        }

        TransferResult transferResult = process(transferOperation);

        return success(transferResult);
    }

    private TransferResult process(TransferOperation transferOperation) {
        return transactionHandler.inTransaction(() -> {
            Boolean updated = transferOperationDao.updateStateWithCheck(transferOperation, PROCESSING);
            if (!updated) {
                log.info("transfer operation has already been processed");
                TransferOperation updatedTransferOperation = transferOperationDao.findById(transferOperation.getId());
                return new TransferResult(
                        updatedTransferOperation.getState(),
                        updatedTransferOperation.getErrorCode().orElse(null)
                );
            }

            Account senderAccount;
            Account receiverAccount;
            if (transferOperation.getSenderAccountId() < transferOperation.getReceiverAccountId()) {
                senderAccount = accountService.findAndLockAccountById(transferOperation.getSenderAccountId());
                receiverAccount = accountService.findAndLockAccountById(transferOperation.getReceiverAccountId());
            } else {
                receiverAccount = accountService.findAndLockAccountById(transferOperation.getReceiverAccountId());
                senderAccount = accountService.findAndLockAccountById(transferOperation.getSenderAccountId());
            }

            MonetaryAmount amountToWithdraw = exchangeService.exchange(
                    transferOperation.getAmount(),
                    senderAccount.getBalance().getCurrency()
            );

            MonetaryAmount amountToDeposit = exchangeService.exchange(
                    transferOperation.getAmount(),
                    receiverAccount.getBalance().getCurrency()
            );

            if (senderAccount.getBalance().getValue().compareTo(amountToWithdraw.getValue()) < 0) {
                log.info("not enough funds on sender account: account={}", senderAccount);
                transferOperationDao.updateStateAndErrorCode(transferOperation, FAILED, NOT_ENOUGH_FUNDS);
                return new TransferResult(FAILED, NOT_ENOUGH_FUNDS);
            }

            transactionService.withdrawAmount(senderAccount, amountToWithdraw);
            transactionService.depositAmount(receiverAccount, amountToDeposit);
            transferOperationDao.updateState(transferOperation, SUCCESSFUL);
            return new TransferResult(SUCCESSFUL);
        });
    }

    private static ResponseEntity<Response<TransferResult>> success(TransferResult transferResult) {
        return ok().body(successWith(transferResult));
    }

    private static ResponseEntity<Response<TransferResult>> error(ResponseError error) {
        return badRequest().body(failWith(error));
    }
}
