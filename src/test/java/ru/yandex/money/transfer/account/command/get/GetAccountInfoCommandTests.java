package ru.yandex.money.transfer.account.command.get;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultActions;
import ru.yandex.money.transfer.account.domain.Account;
import ru.yandex.money.transfer.account.service.AccountService;
import ru.yandex.money.transfer.common.domain.MonetaryAmount;
import ru.yandex.money.transfer.test.IntegrationTest;

import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.yandex.money.transfer.common.domain.Currency.USD;
import static ru.yandex.money.transfer.test.RandomUtils.randomAccountNumber;
import static ru.yandex.money.transfer.test.RandomUtils.randomLong;

/**
 * @author petrique
 */
@TestInstance(PER_CLASS)
public class GetAccountInfoCommandTests extends IntegrationTest {

    @Autowired
    private AccountService accountService;

    private Account account;

    @BeforeAll
    void init() {
        account = insertAccountWith(MonetaryAmount.of("100.00", USD));
    }

    @Test
    void should_returnAccountNotFoundError_when_accountDoesNotExist() throws Exception {
        callCommandWith("nonexistent")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("status").value("Fail"))
                .andExpect(jsonPath("result").doesNotHaveJsonPath())
                .andExpect(jsonPath("error.code").value("AccountNotFound"))
                .andExpect(jsonPath("error.message").value("Account not found"));
    }

    @Test
    void should_returnAccountInfo() throws Exception {
        callCommandWith(account.getNumber())
                .andExpect(status().isOk())
                .andExpect(jsonPath("status").value("Success"))
                .andExpect(jsonPath("error").doesNotHaveJsonPath())
                .andExpect(jsonPath("result.number").value(account.getNumber()))
                .andExpect(jsonPath("result.balance.value").value(account.getBalance().getValue().toPlainString()))
                .andExpect(jsonPath("result.balance.currency").value(account.getBalance().getCurrency().getIsoCode()));
    }

    private ResultActions callCommandWith(String accountNumber) throws Exception {
        return mockMvc()
                .perform(get("/api/v1/accounts/{accountNumber}", accountNumber)
                        .contentType(APPLICATION_FORM_URLENCODED)
                        .accept(APPLICATION_JSON)
                );
    }

    private Account insertAccountWith(MonetaryAmount balance) {
        Account account = Account.builder()
                .withId(randomLong())
                .withNumber(randomAccountNumber())
                .withBalance(balance)
                .build();
        accountService.saveAccount(account);
        return account;
    }
}
