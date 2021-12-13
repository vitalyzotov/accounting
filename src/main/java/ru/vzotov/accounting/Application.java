package ru.vzotov.accounting;

import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.jdbc.DatabaseDriver;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.jdbc.support.DatabaseStartupValidator;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.WebApplicationInitializer;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.stream.Stream;

@SpringBootApplication(
        scanBasePackages = {
                "ru.vzotov.cashreceipt",
                "ru.vzotov.accounting",
                "ru.vzotov.alfabank",
                "ru.vzotov.tinkoff",
//                "ru.vzotov.gpb"
        }
)
@EnableScheduling
@EnableAspectJAutoProxy
public class Application extends SpringBootServletInitializer implements WebApplicationInitializer {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(Application.class);
    }

    @Bean
    public static BeanFactoryPostProcessor dependsOnPostProcessor() {
        return beanFactory -> Stream.of(beanFactory.getBeanNamesForType(EntityManagerFactory.class))
                .map(beanFactory::getBeanDefinition)
                .forEach(bean -> bean.setDependsOn("databaseStartupValidator"));
    }

    @Bean
    public DatabaseStartupValidator databaseStartupValidator(DataSource dataSource) {
        DatabaseStartupValidator bean = new DatabaseStartupValidator();
        bean.setDataSource(dataSource);
        bean.setValidationQuery(DatabaseDriver.MYSQL.getValidationQuery());
        return bean;
    }
}
