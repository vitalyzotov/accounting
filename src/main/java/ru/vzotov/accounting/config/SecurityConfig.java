package ru.vzotov.accounting.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsByNameServiceWrapper;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.scrypt.SCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import ru.vzotov.accounting.infrastructure.security.JwtFilter;
import ru.vzotov.accounting.infrastructure.security.JwtProvider;

import java.util.Map;

import static org.springframework.security.config.Customizer.withDefaults;

@ConditionalOnProperty(prefix = "accounting", value = "security", havingValue = "production", matchIfMissing = true)
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtProvider jwtProvider;

    private final UserDetailsService userDetailsService;

    public SecurityConfig(JwtProvider jwtProvider, UserDetailsService userDetailsService) {

        this.jwtProvider = jwtProvider;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity http,
            JwtFilter jwtFilter) throws Exception {
        return http
                .sessionManagement(sessions -> sessions.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(csrf -> csrf.disable())
                .cors(withDefaults())
                .authorizeHttpRequests(auth ->
                        auth.requestMatchers("/auth/login", "/auth/token", "/auth/signup").permitAll()
                                .requestMatchers(HttpMethod.OPTIONS).permitAll()
                                .anyRequest().authenticated()
                )
                .authenticationProvider(preAuthenticatedAuthenticationProvider())
                .httpBasic(withDefaults())
                .addFilterBefore(jwtFilter, BasicAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public JwtFilter jwtFilter(AuthenticationManager authenticationManager) {
        return new JwtFilter(jwtProvider, authenticationManager, userDetailsService);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        String prefix = "scrypt@5.8";
        PasswordEncoder current = SCryptPasswordEncoder.defaultsForSpringSecurity_v4_1();
        PasswordEncoder upgraded = SCryptPasswordEncoder.defaultsForSpringSecurity_v5_8();
        DelegatingPasswordEncoder delegating = new DelegatingPasswordEncoder(prefix, Map.of(prefix, upgraded));
        delegating.setDefaultPasswordEncoderForMatches(current);
        return delegating;
    }

    public PreAuthenticatedAuthenticationProvider preAuthenticatedAuthenticationProvider() {
        PreAuthenticatedAuthenticationProvider provider = new PreAuthenticatedAuthenticationProvider();
        provider.setPreAuthenticatedUserDetailsService(new UserDetailsByNameServiceWrapper<>(userDetailsService));
        return provider;
    }
}
