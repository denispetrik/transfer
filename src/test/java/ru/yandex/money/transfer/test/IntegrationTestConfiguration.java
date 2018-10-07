package ru.yandex.money.transfer.test;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import ru.yandex.money.transfer.ApplicationConfiguration;

import javax.sql.DataSource;

/**
 * @author petrique
 */
@Configuration
@Import(ApplicationConfiguration.class)
class IntegrationTestConfiguration {

    @Primary
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
}
