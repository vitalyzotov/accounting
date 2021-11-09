package ru.vzotov.accounting.interfaces.accounting.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/login")
@CrossOrigin
public class LoginController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping
    private Map<String, Object> login(@RequestBody Map<String, String> payload) throws AuthenticationException {

        final Authentication auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                payload.get("username"),
                payload.get("password")
        ));

        Map<String, Object> result = new HashMap<>();

        result.put("authorities", auth.getAuthorities());

        return result;
    }

}
