package ru.vzotov.accounting.infrastructure.persistence.jpa;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import ru.vzotov.accounting.domain.model.UserRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@Configuration("jpa-accounting-app")
public class AppJpaConfig {
    private final DataSource dataSource;

    public AppJpaConfig(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Bean
    @ConditionalOnMissingBean
    public UserRepository userRepository(@Qualifier("accounting-app-emf") EntityManager em) {
        return new UserRepositoryJpa(em);
    }

    @Bean("accounting-app-emf")
    public LocalContainerEntityManagerFactoryBean accountingAppEntityManagerFactory(EntityManagerFactoryBuilder builder) {
        return builder
                .dataSource(dataSource)
                .persistenceUnit("accounting-app")
                .build();
    }


    @Bean("accounting-app-tx")
    public JpaTransactionManager transactionManager(@Qualifier("accounting-app-emf") final EntityManagerFactory emf) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(emf);
        return transactionManager;
    }
}
