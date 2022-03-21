package ru.vzotov.accounting.interfaces.accounting.facade.impl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.vzotov.accounting.domain.model.PersonRepository;
import ru.vzotov.accounting.domain.model.User;
import ru.vzotov.accounting.domain.model.UserRepository;
import ru.vzotov.accounting.infrastructure.security.SecurityUtils;
import ru.vzotov.accounting.interfaces.accounting.facade.UserManagementFacade;
import ru.vzotov.person.domain.model.Person;
import ru.vzotov.person.domain.model.PersonId;

import java.util.Collections;

@Service
public class UserManagementFacadeImpl implements UserManagementFacade {

    private final UserRepository userRepository;

    private final PersonRepository personRepository;

    private final PasswordEncoder passwordEncoder;

    public UserManagementFacadeImpl(UserRepository userRepository,
                                    PersonRepository personRepository,
                                    PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.personRepository = personRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional(value = "accounting-app-tx")
    public User registerNewUser(String name, String password, String firstName, String lastName, String displayName) {
        Person person = new Person(PersonId.nextId(), firstName, lastName, displayName);
        User user = new User(name, passwordEncoder.encode(password), person, Collections.singletonList("ROLE_USER"));

        userRepository.store(user);

        return user;
    }

    @Override
    @Transactional(value = "accounting-app-tx", readOnly = true)
    public User getUserByPersonId(String personId) {
        PersonId pid = new PersonId(personId);
        return SecurityUtils.getAuthorizedPersons().contains(pid) ? userRepository.findByPersonId(pid) : null;
    }

    @Override
    @Transactional(value = "accounting-app-tx", readOnly = true)
    public Person getPerson(String personId) {
        PersonId pid = new PersonId(personId);
        return SecurityUtils.getAuthorizedPersons().contains(pid) ? personRepository.find(pid) : null;
    }
}
