package ru.yandex.money.transfer;

import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Import;

/**
 * @author petrique
 */
@Import(ApplicationConfiguration.class)
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
