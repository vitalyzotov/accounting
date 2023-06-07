package ru.vzotov.accounting.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.support.GenericConversionService;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.acls.AclPermissionEvaluator;
import org.springframework.security.acls.domain.AclAuthorizationStrategy;
import org.springframework.security.acls.domain.AclAuthorizationStrategyImpl;
import org.springframework.security.acls.domain.ConsoleAuditLogger;
import org.springframework.security.acls.domain.DefaultPermissionGrantingStrategy;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.domain.SpringCacheBasedAclCache;
import org.springframework.security.acls.jdbc.BasicLookupStrategy;
import org.springframework.security.acls.jdbc.JdbcMutableAclService;
import org.springframework.security.acls.jdbc.LookupStrategy;
import org.springframework.security.acls.model.AclCache;
import org.springframework.security.acls.model.MutableAclService;
import org.springframework.security.acls.model.PermissionGrantingStrategy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import ru.vzotov.banking.domain.model.Account;
import ru.vzotov.banking.domain.model.AccountNumber;

import javax.sql.DataSource;
import java.util.UUID;

@Configuration
public class AclSecurityConfig {

    @Autowired
    DataSource dataSource;

    @Autowired
    org.springframework.cache.CacheManager springCacheManager;

    @Bean
    public MutableAclService aclService() {
        final JdbcMutableAclService aclService = new JdbcMutableAclService(dataSource, lookupStrategy(), aclCache());
        aclService.setAclClassIdSupported(true);
        return aclService;
    }

    @Bean
    public AclAuthorizationStrategy aclAuthorizationStrategy() {
        return new AclAuthorizationStrategyImpl(new SimpleGrantedAuthority("ROLE_ADMIN"));
    }

    @Bean
    public PermissionGrantingStrategy permissionGrantingStrategy() {
        return new DefaultPermissionGrantingStrategy(new ConsoleAuditLogger());
    }

    @Bean
    public AclCache aclCache() {
        org.springframework.cache.Cache cache = springCacheManager.getCache("acl_cache");
        return new SpringCacheBasedAclCache(cache,
                permissionGrantingStrategy(),
                aclAuthorizationStrategy());
    }

    @Bean
    public LookupStrategy lookupStrategy() {
        final BasicLookupStrategy lookupStrategy = new BasicLookupStrategy(
                dataSource,
                aclCache(),
                aclAuthorizationStrategy(),
                new ConsoleAuditLogger()
        );
        lookupStrategy.setAclClassIdSupported(true);

        GenericConversionService conversionService = new GenericConversionService();
        conversionService.addConverter(String.class, Long.class, new StringToLongConverter());
        conversionService.addConverter(String.class, UUID.class, new StringToUUIDConverter());
        conversionService.addConverter(String.class, AccountNumber.class, new StringToAccountNumberConverter());

        lookupStrategy.setConversionService(conversionService);
        return lookupStrategy;
    }

    @Bean
    public MethodSecurityExpressionHandler defaultMethodSecurityExpressionHandler() {
        DefaultMethodSecurityExpressionHandler expressionHandler
                = new DefaultMethodSecurityExpressionHandler();
        AclPermissionEvaluator permissionEvaluator = new AclPermissionEvaluator(aclService());
        permissionEvaluator.setObjectIdentityRetrievalStrategy(domainObject -> {
            if (domainObject instanceof Account) {
                return new ObjectIdentityImpl(Account.class, ((Account) domainObject).accountNumber());
            }
            return new ObjectIdentityImpl(domainObject);
        });
        expressionHandler.setPermissionEvaluator(permissionEvaluator);
        return expressionHandler;
    }

    private static class StringToLongConverter implements Converter<String, Long> {
        @Override
        public Long convert(String identifierAsString) {
            return Long.parseLong(identifierAsString);
        }
    }

    private static class StringToUUIDConverter implements Converter<String, UUID> {
        @Override
        public UUID convert(String identifierAsString) {
            return UUID.fromString(identifierAsString);
        }

    }

    private static class StringToAccountNumberConverter implements Converter<String, AccountNumber> {
        @Override
        public AccountNumber convert(String source) {
            return new AccountNumber(source);
        }
    }
}
