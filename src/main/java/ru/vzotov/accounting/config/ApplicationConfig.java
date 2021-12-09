package ru.vzotov.accounting.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import liquibase.integration.spring.SpringLiquibase;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.data.transaction.ChainedTransactionManager;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import ru.vzotov.accounting.AccountingModule;
import ru.vzotov.cashreceipt.CashreceiptsModule;

import javax.sql.DataSource;

@Configuration
@EnableAsync
@EnableTransactionManagement
@Import({
        CashreceiptsModule.class,
        AccountingModule.class,
})
public class ApplicationConfig {

    private final DataSource dataSource;

    public ApplicationConfig(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Bean
    @Qualifier("shared-tx")
    public TransactionManager sharedTx(@Qualifier("accounting-tx") PlatformTransactionManager accountingTx, @Qualifier("cashreceipt-tx") PlatformTransactionManager cashreceiptTx) {
        return new ChainedTransactionManager(accountingTx, cashreceiptTx);
    }

    @Bean
    @Primary
    public ObjectMapper objectMapper(Jackson2ObjectMapperBuilder builder) {
        ObjectMapper objectMapper = builder.createXmlMapper(false).build();
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        return objectMapper;
    }

    @Bean
    @ConfigurationProperties("application.liquibase")
    @Application
    public LiquibaseProperties applicationLiquibaseProperties() {
        return new LiquibaseProperties();
    }

    @Bean
    @Application
    public SpringLiquibase applicationLiquibase() {
        return springLiquibase(dataSource, applicationLiquibaseProperties());
    }

    private static SpringLiquibase springLiquibase(DataSource dataSource, LiquibaseProperties properties) {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setDataSource(dataSource);
        liquibase.setChangeLog(properties.getChangeLog());
        liquibase.setContexts(properties.getContexts());
        liquibase.setDefaultSchema(properties.getDefaultSchema());
        liquibase.setDropFirst(properties.isDropFirst());
        liquibase.setShouldRun(properties.isEnabled());
        liquibase.setLabels(properties.getLabels());
        liquibase.setChangeLogParameters(properties.getParameters());
        liquibase.setRollbackFile(properties.getRollbackFile());
        return liquibase;
    }

}
