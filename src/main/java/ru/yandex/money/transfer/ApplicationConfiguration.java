package ru.yandex.money.transfer;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import ru.yandex.money.transfer.account.AccountConfiguration;
import ru.yandex.money.transfer.core.CoreConfiguration;
import ru.yandex.money.transfer.exchange.ExchangeConfiguration;
import ru.yandex.money.transfer.transaction.TransactionConfiguration;
import ru.yandex.money.transfer.transfer.TransferConfiguration;

/**
 * @author petrique
 */
@EnableAutoConfiguration
@Configuration
@Import({
        CoreConfiguration.class,
        ExchangeConfiguration.class,
        AccountConfiguration.class,
        TransactionConfiguration.class,
        TransferConfiguration.class
})
public class ApplicationConfiguration {
}
