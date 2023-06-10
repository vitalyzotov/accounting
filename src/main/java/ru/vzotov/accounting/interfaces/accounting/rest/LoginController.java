package ru.vzotov.accounting.interfaces.accounting.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.vzotov.accounting.infrastructure.security.JwtAuthService;
import ru.vzotov.accounting.infrastructure.security.JwtAuthService.JwtResponse;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@CrossOrigin
public class LoginController {

    private final AuthenticationManager authenticationManager;

    private final JwtAuthService jwtAuthService;

    public LoginController(AuthenticationManager authenticationManager, JwtAuthService jwtAuthService) {
        this.authenticationManager = authenticationManager;
        this.jwtAuthService = jwtAuthService;
    }

    @PostMapping(path = "login", params = {"!jwt"})
    private Map<String, Object> login(@RequestBody LoginRequest payload) throws AuthenticationException {

        final Authentication auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                payload.username(),
                payload.password()
        ));

        Map<String, Object> result = new HashMap<>();

        result.put("authorities", auth.getAuthorities());

        return result;
    }

    @PostMapping(path = "login", params = {"jwt"})
    private ResponseEntity<JwtResponse> login(@RequestBody LoginRequest payload, @RequestParam() boolean jwt) throws AuthenticationException {
        return ResponseEntity.ok(jwtAuthService.login(payload.username(), payload.password()));
    }

    @PostMapping("token")
    public ResponseEntity<JwtResponse> getNewAccessToken(@RequestBody RefreshJwtRequest request) {
        final JwtResponse token = jwtAuthService.getAccessToken(request.refreshToken());
        return ResponseEntity.ok(token);
    }

    @PostMapping("refresh")
    public ResponseEntity<JwtResponse> getNewRefreshToken(@RequestBody RefreshJwtRequest request) {
        final JwtResponse token = jwtAuthService.refresh(request.refreshToken());
        return ResponseEntity.ok(token);
    }

    public record LoginRequest(String username, String password) {
    }

    public record RefreshJwtRequest(String refreshToken) {
    }
}
