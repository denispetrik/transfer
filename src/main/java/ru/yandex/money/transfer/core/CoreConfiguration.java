package ru.yandex.money.transfer.core;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.support.TransactionTemplate;
import ru.yandex.money.transfer.core.database.TransactionHandler;
import ru.yandex.money.transfer.core.json.JsonModule;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.sql.DataSource;

/**
 * @author petrique
 */
@Configuration
@EnableSwagger2
public class CoreConfiguration {

    @Bean
    DataSource dataSource() {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDriverClassName("org.hsqldb.jdbc.JDBCDriver");
        hikariConfig.setJdbcUrl("jdbc:hsqldb:mem:transfer");
        hikariConfig.setUsername("sa");
        hikariConfig.setPassword("");
        hikariConfig.setAutoCommit(false);
        hikariConfig.addDataSourceProperty("sql.syntax_pgs", "true");
        return new HikariDataSource(hikariConfig);
    }

    @Bean
    FlywayMigrationStrategy flywayMigrationStrategy() {
        return flyway -> {
            flyway.setSchemas("p"); //in-memory HSQLDB schema
            flyway.migrate();
        };
    }

    @Bean
    TransactionHandler transactionHandler(TransactionTemplate transactionTemplate) {
        return new TransactionHandler(transactionTemplate);
    }

    @Bean
    JsonModule jsonModule() {
        return new JsonModule();
    }

    @Bean
    Docket swaggerConfig() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build()
                .pathMapping("/")
                .genericModelSubstitutes(ResponseEntity.class);
    }
}
