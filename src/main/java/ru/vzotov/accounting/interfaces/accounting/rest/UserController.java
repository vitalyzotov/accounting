package ru.vzotov.accounting.interfaces.accounting.rest;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.vzotov.accounting.infrastructure.security.SecurityUtils;
import ru.vzotov.person.domain.model.PersonId;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/users")
@CrossOrigin
public class UserController {

    @GetMapping
    private Map<String, Object> current() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Map<String, Object> result = new HashMap<>();

        result.put("name", auth.getName());
        result.put("person", Optional.ofNullable(SecurityUtils.getCurrentPerson()).map(PersonId::value).orElse(null));
        result.put("authorities", auth.getAuthorities());

        return result;
    }
}
