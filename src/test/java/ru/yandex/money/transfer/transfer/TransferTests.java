package ru.yandex.money.transfer.transfer;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.money.transfer.account.domain.Account;
import ru.yandex.money.transfer.account.service.AccountService;
import ru.yandex.money.transfer.common.domain.MonetaryAmount;
import ru.yandex.money.transfer.test.IntegrationTest;

import java.util.stream.Stream;

import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static ru.yandex.money.transfer.common.domain.Currency.EUR;
import static ru.yandex.money.transfer.common.domain.Currency.USD;
import static ru.yandex.money.transfer.test.RandomUtils.randomAccountNumber;
import static ru.yandex.money.transfer.test.RandomUtils.randomLong;

/**
 * @author petrique
 */
class TransferTests extends IntegrationTest {

    @Autowired
    private AccountService accountService;

    @ParameterizedTest
    @MethodSource("transferParameters")
    void should_transferFundsFromSenderToReceiver(
            MonetaryAmount senderBalance,
            MonetaryAmount receiverBalance,
            MonetaryAmount transferAmount,
            MonetaryAmount expectedSenderBalance,
            MonetaryAmount expectedReceiverBalance
    ) throws Exception {
        //given
        Account senderAccount = insertAccount(senderBalance);
        Account receiverAccount = insertAccount(receiverBalance);

        //when
        String transferId = prepareTransfer(
                senderAccount.getNumber(),
                receiverAccount.getNumber(),
                transferAmount
        );
        processTransfer(transferId);

        //then
        Account updatedSenderAccount = accountService.findAccountById(senderAccount.getId());
        Account updatedReceiverAccount = accountService.findAccountById(receiverAccount.getId());

        assertEquals(expectedSenderBalance, updatedSenderAccount.getBalance());
        assertEquals(expectedReceiverBalance, updatedReceiverAccount.getBalance());
    }

    @Test
    void should_returnExistentOperationData_when_callProcessSecondTime() throws Exception {
        //given
        Account senderAccount = insertAccount(MonetaryAmount.of("100.00", EUR));
        Account receiverAccount = insertAccount(MonetaryAmount.of("200.00", USD));

        //when
        String transferId = prepareTransfer(
                senderAccount.getNumber(),
                receiverAccount.getNumber(),
                MonetaryAmount.of("10.00", EUR)
        );
        processTransfer(transferId);
        //second call
        processTransfer(transferId);

        //then
        assertEquals(
                MonetaryAmount.of("90.00", EUR),
                accountService.findAccountById(senderAccount.getId()).getBalance()
        );
        assertEquals(
                MonetaryAmount.of("220.00", USD),
                accountService.findAccountById(receiverAccount.getId()).getBalance()
        );
    }

    private static Stream<Arguments> transferParameters() {
        return Stream.of(
                Arguments.of(
                        MonetaryAmount.of("100.00", EUR),
                        MonetaryAmount.of("200.00", EUR),
                        MonetaryAmount.of("10.00", EUR),
                        MonetaryAmount.of("90.00", EUR),
                        MonetaryAmount.of("210.00", EUR)
                ),
                Arguments.of(
                        MonetaryAmount.of("100.00", USD),
                        MonetaryAmount.of("200.00", USD),
                        MonetaryAmount.of("10.00", USD),
                        MonetaryAmount.of("90.00", USD),
                        MonetaryAmount.of("210.00", USD)
                ),
                Arguments.of(
                        MonetaryAmount.of("100.00", EUR),
                        MonetaryAmount.of("200.00", EUR),
                        MonetaryAmount.of("10.00", USD),
                        MonetaryAmount.of("95.00", EUR),
                        MonetaryAmount.of("205.00", EUR)
                ),
                Arguments.of(
                        MonetaryAmount.of("100.00", USD),
                        MonetaryAmount.of("200.00", USD),
                        MonetaryAmount.of("10.00", EUR),
                        MonetaryAmount.of("80.00", USD),
                        MonetaryAmount.of("220.00", USD)
                ),
                Arguments.of(
                        MonetaryAmount.of("100.00", EUR),
                        MonetaryAmount.of("200.00", USD),
                        MonetaryAmount.of("10.00", EUR),
                        MonetaryAmount.of("90.00", EUR),
                        MonetaryAmount.of("220.00", USD)
                ),
                Arguments.of(
                        MonetaryAmount.of("100.00", EUR),
                        MonetaryAmount.of("200.00", USD),
                        MonetaryAmount.of("10.00", USD),
                        MonetaryAmount.of("95.00", EUR),
                        MonetaryAmount.of("210.00", USD)
                ),
                Arguments.of(
                        MonetaryAmount.of("100.00", USD),
                        MonetaryAmount.of("200.00", EUR),
                        MonetaryAmount.of("10.00", EUR),
                        MonetaryAmount.of("80.00", USD),
                        MonetaryAmount.of("210.00", EUR)
                ),
                Arguments.of(
                        MonetaryAmount.of("100.00", USD),
                        MonetaryAmount.of("200.00", EUR),
                        MonetaryAmount.of("10.00", USD),
                        MonetaryAmount.of("90.00", USD),
                        MonetaryAmount.of("205.00", EUR)
                )
        );
    }

    private String prepareTransfer(
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

        String response = mockMvc()
                .perform(post("/api/v1/transfers/")
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON)
                        .content(request)
                )
                .andReturn()
                .getResponse()
                .getContentAsString();
        return JsonPath.parse(response).read("result.transferId");
    }

    private void processTransfer(String transferId) throws Exception {
        mockMvc()
                .perform(put("/api/v1/transfers/{transferId}", transferId)
                        .contentType(APPLICATION_FORM_URLENCODED)
                        .accept(APPLICATION_JSON)
                );
    }

    private Account insertAccount(MonetaryAmount balance) {
        Account account = Account.builder()
                .withId(randomLong())
                .withNumber(randomAccountNumber())
                .withBalance(balance)
                .build();
        accountService.saveAccount(account);
        return account;
    }
}
