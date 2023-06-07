package ru.vzotov.accounting.infrastructure.security;

import io.jsonwebtoken.Claims;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

public class JwtFilter extends GenericFilterBean {

    private static final String BEARER = "Bearer ";
    private static final int TOKEN_START_INDEX = BEARER.length();

    private final JwtProvider jwtProvider;

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;

    public JwtFilter(JwtProvider jwtProvider, AuthenticationManager authenticationManager, UserDetailsService userDetailsService) {
        this.jwtProvider = jwtProvider;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        final String token = getTokenFromRequest((HttpServletRequest) request);
        if (token != null && jwtProvider.validateAccessToken(token)) {
            final Claims claims = jwtProvider.getAccessClaims(token);
            final Authentication authResult = authenticationManager.authenticate(new PreAuthenticatedAuthenticationToken(
                    claims.getSubject(),
                    token
            ));
            SecurityContextHolder.getContext().setAuthentication(authResult);
        }
        filterChain.doFilter(request, response);
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        final String bearer = request.getHeader(AUTHORIZATION);
        if (StringUtils.hasText(bearer) && bearer.startsWith(BEARER)) {
            return bearer.substring(TOKEN_START_INDEX);
        }
        return null;
    }

}
