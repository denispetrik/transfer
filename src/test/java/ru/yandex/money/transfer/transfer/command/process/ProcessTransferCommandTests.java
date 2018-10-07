package ru.yandex.money.transfer.transfer.command.process;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultActions;
import ru.yandex.money.transfer.account.domain.Account;
import ru.yandex.money.transfer.account.service.AccountService;
import ru.yandex.money.transfer.common.domain.MonetaryAmount;
import ru.yandex.money.transfer.core.database.TransactionHandler;
import ru.yandex.money.transfer.test.IntegrationTest;
import ru.yandex.money.transfer.transfer.dao.TransferOperationDao;
import ru.yandex.money.transfer.transfer.domain.TransferOperation;

import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.yandex.money.transfer.common.domain.Currency.EUR;
import static ru.yandex.money.transfer.common.domain.Currency.USD;
import static ru.yandex.money.transfer.test.RandomUtils.randomAccountNumber;
import static ru.yandex.money.transfer.test.RandomUtils.randomLong;
import static ru.yandex.money.transfer.test.RandomUtils.randomUUID;
import static ru.yandex.money.transfer.transfer.domain.TransferOperationState.CREATED;

/**
 * @author petrique
 */
@TestInstance(PER_CLASS)
class ProcessTransferCommandTests extends IntegrationTest {

    @Autowired
    private AccountService accountService;
    @Autowired
    private TransactionHandler transactionHandler;
    @Autowired
    private TransferOperationDao transferOperationDao;

    private Long senderAccountId;
    private Long receiverAccountId;

    @BeforeAll
    void init() {
        senderAccountId = randomLong();
        accountService.saveAccount(Account.builder()
                .withId(senderAccountId)
                .withNumber(randomAccountNumber())
                .withBalance(MonetaryAmount.of("100.00", EUR))
                .build()
        );

        receiverAccountId = randomLong();
        accountService.saveAccount(Account.builder()
                .withId(receiverAccountId)
                .withNumber(randomAccountNumber())
                .withBalance(MonetaryAmount.of("200.00", USD))
                .build()
        );
    }

    @Test
    void should_returnOperationNotFoundError_when_transferOperationDoesNotExist() throws Exception {
        callCommandWith("nonexistent")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("status").value("Fail"))
                .andExpect(jsonPath("result").doesNotHaveJsonPath())
                .andExpect(jsonPath("error.code").value("OperationNotFound"))
                .andExpect(jsonPath("error.message").value("Transfer operation not found"));
    }

    @Test
    void should_returnNotEnoughFundsResult_when_senderDoesNotHaveEnoughFunds() throws Exception {
        String externalId = insertTransferOperation(MonetaryAmount.of("1000.00", EUR));

        callCommandWith(externalId)
                .andExpect(status().isOk())
                .andExpect(jsonPath("status").value("Success"))
                .andExpect(jsonPath("error").doesNotHaveJsonPath())
                .andExpect(jsonPath("result.state").value("Failed"))
                .andExpect(jsonPath("result.errorCode").value("NotEnoughFunds"));
    }

    @Test
    void should_processTransferOperation() throws Exception {
        String externalId = insertTransferOperation(MonetaryAmount.of("10.00", EUR));

        callCommandWith(externalId)
                .andExpect(status().isOk())
                .andExpect(jsonPath("status").value("Success"))
                .andExpect(jsonPath("error").doesNotHaveJsonPath())
                .andExpect(jsonPath("result.state").value("Successful"))
                .andExpect(jsonPath("result.errorCode").doesNotHaveJsonPath());
    }

    private ResultActions callCommandWith(String transferId) throws Exception {
        return mockMvc()
                .perform(put("/api/v1/transfers/{transferId}", transferId)
                        .contentType(APPLICATION_FORM_URLENCODED)
                        .accept(APPLICATION_JSON)
                );
    }

    private String insertTransferOperation(MonetaryAmount amount) {
        TransferOperation transferOperation = TransferOperation.builder()
                .withId(randomLong())
                .withState(CREATED)
                .withExternalId(randomUUID())
                .withSenderAccountId(senderAccountId)
                .withReceiverAccountId(receiverAccountId)
                .withAmount(amount)
                .build();
        transactionHandler.inTransaction(() -> transferOperationDao.save(transferOperation));
        return transferOperation.getExternalId();
    }
}
