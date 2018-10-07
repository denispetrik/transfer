package ru.yandex.money.transfer.exchange;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.yandex.money.transfer.exchange.service.ExchangeService;

/**
 * @author petrique
 */
@Configuration
public class ExchangeConfiguration {

    @Bean
    ExchangeService exchangeService() {
        return new ExchangeService();
    }
}
