package ru.yandex.money.transfer.transfer.command.prepare;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultActions;
import ru.yandex.money.transfer.account.domain.Account;
import ru.yandex.money.transfer.account.service.AccountService;
import ru.yandex.money.transfer.common.domain.MonetaryAmount;
import ru.yandex.money.transfer.test.IntegrationTest;

import static java.lang.String.format;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.yandex.money.transfer.common.domain.Currency.EUR;
import static ru.yandex.money.transfer.common.domain.Currency.USD;
import static ru.yandex.money.transfer.test.RandomUtils.randomAccountNumber;
import static ru.yandex.money.transfer.test.RandomUtils.randomLong;

/**
 * @author petrique
 */
@TestInstance(PER_CLASS)
class PrepareTransferCommandTests extends IntegrationTest {

    @Autowired
    private AccountService accountService;

    private String senderAccountNumber;
    private String receiverAccountNumber;

    @BeforeAll
    void init() {
        senderAccountNumber = insertAccount(MonetaryAmount.of("100.00", EUR));
        receiverAccountNumber = insertAccount(MonetaryAmount.of("200.00", USD));
    }

    @Test
    void should_returnSenderNotFoundError_when_senderAccountDoesNotExist() throws Exception {
        callCommandWith("nonexistent", receiverAccountNumber, MonetaryAmount.of("10.00", EUR))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("status").value("Fail"))
                .andExpect(jsonPath("result").doesNotHaveJsonPath())
                .andExpect(jsonPath("error.code").value("SenderNotFound"))
                .andExpect(jsonPath("error.message").value("Sender account not found"));
    }

    @Test
    void should_returnReceiverNotFoundError_when_receiverAccountDoesNotExist() throws Exception {
        callCommandWith(senderAccountNumber, "nonexistent", MonetaryAmount.of("10.00", EUR))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("status").value("Fail"))
                .andExpect(jsonPath("result").doesNotHaveJsonPath())
                .andExpect(jsonPath("error.code").value("ReceiverNotFound"))
                .andExpect(jsonPath("error.message").value("Receiver account not found"));
    }

    @Test
    void should_returnReceiverMustDefferError_when_receiverEqualsToSender() throws Exception {
        callCommandWith(senderAccountNumber, senderAccountNumber, MonetaryAmount.of("10.00", EUR))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("status").value("Fail"))
                .andExpect(jsonPath("result").doesNotHaveJsonPath())
                .andExpect(jsonPath("error.code").value("ReceiverMustDiffer"))
                .andExpect(jsonPath("error.message").value("Receiver must differ from sender"));
    }

    @Test
    void should_prepareTransferOperation() throws Exception {
        callCommandWith(senderAccountNumber, receiverAccountNumber, MonetaryAmount.of("10.00", EUR))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("status").value("Success"))
                .andExpect(jsonPath("error").doesNotHaveJsonPath())
                .andExpect(jsonPath("result.transferId").isString())
                .andExpect(jsonPath("result.senderAccountNumber").value(senderAccountNumber))
                .andExpect(jsonPath("result.receiverAccountNumber").value(receiverAccountNumber))
                .andExpect(jsonPath("result.amount.value").value("10.00"))
                .andExpect(jsonPath("result.amount.currency").value("EUR"));
    }

    private ResultActions callCommandWith(
            String senderAccountNumber,
            String receiverAccountNumber,
            MonetaryAmount amount
    ) throws Exception {
        String request = format("{" +
                        "\"senderAccountNumber\":\"%s\"," +
                        "\"receiverAccountNumber\":\"%s\"," +
                        "\"amount\":{\"value\":\"%s\",\"currency\":\"%s\"}}",
                senderAccountNumber,
                receiverAccountNumber,
                amount.getValue().toPlainString(),
                amount.getCurrency().getIsoCode()
        );

        return mockMvc()
                .perform(post("/api/v1/transfers/")
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON)
                        .content(request)
                );
    }

    private String insertAccount(MonetaryAmount balance) {
        Account account = Account.builder()
                .withId(randomLong())
                .withNumber(randomAccountNumber())
                .withBalance(balance)
                .build();
        accountService.saveAccount(account);
        return account.getNumber();
    }
}
