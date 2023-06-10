package ru.vzotov.accounting.interfaces.accounting.rest;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.vzotov.accounting.domain.model.User;
import ru.vzotov.accounting.infrastructure.security.SecurityUtils;
import ru.vzotov.accounting.interfaces.accounting.facade.UserManagementFacade;
import ru.vzotov.person.domain.model.Person;
import ru.vzotov.person.domain.model.PersonId;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
@CrossOrigin
public class UserController {

    private final UserManagementFacade userManagementFacade;

    public UserController(UserManagementFacade userManagementFacade) {
        this.userManagementFacade = userManagementFacade;
    }

    @GetMapping
    private List<UserDTO> search(@RequestParam(required = false, defaultValue = "true") boolean self,
                                 @RequestParam(required = false) boolean related) {
        final List<UserDTO> result = new ArrayList<>();
        final PersonId current = SecurityUtils.getCurrentPerson();

        if (self) {
            Optional.ofNullable(current)
                    .map(PersonId::value)
                    .map(userManagementFacade::getUserByPersonId)
                    .map(UserDTO::from)
                    .ifPresent(result::add);
        }

        if (related) {
            SecurityUtils.getAuthorizedPersons().stream()
                    .filter(pid -> !pid.equals(current))
                    .map(PersonId::value)
                    .map(personId -> {
                        User user = userManagementFacade.getUserByPersonId(personId);
                        if (user == null) {
                            Person person = userManagementFacade.getPerson(personId);
                            if (person == null) return null;
                            return UserDTO.from(person);
                        }
                        return UserDTO.from(user);
                    })
                    .forEach(result::add);
        }

        return result;
    }

    record UserDTO(String name, String person, String firstName, String lastName, String displayName,
                   List<AuthorityDTO> authorities) {
        public static UserDTO from(Person person) {
            return new UserDTO(
                    null,
                    person.personId().value(),
                    person.firstName(),
                    person.lastName(),
                    person.displayName(),
                    Collections.emptyList()
            );
        }

        public static UserDTO from(User user) {
            return new UserDTO(
                    user.name(),
                    user.person().personId().value(),
                    user.person().firstName(),
                    user.person().lastName(),
                    user.person().displayName(),
                    user.roles().stream().map(AuthorityDTO::new).collect(Collectors.toList())
            );
        }

    }

    record AuthorityDTO(String authority) {
    }

}

