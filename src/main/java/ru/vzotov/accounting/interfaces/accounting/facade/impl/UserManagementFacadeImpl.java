package ru.vzotov.accounting.interfaces.accounting.facade.impl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.vzotov.accounting.domain.model.User;
import ru.vzotov.accounting.domain.model.UserRepository;
import ru.vzotov.accounting.interfaces.accounting.facade.UserManagementFacade;
import ru.vzotov.banking.domain.model.Person;
import ru.vzotov.banking.domain.model.PersonId;

@Service
public class UserManagementFacadeImpl implements UserManagementFacade {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    public UserManagementFacadeImpl(UserRepository userRepository,
                                    PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional(value = "accounting-app-tx")
    public User registerNewUser(String name, String password, String firstName, String lastName, String displayName) {
        Person person = new Person(PersonId.nextId(), firstName, lastName, displayName);
        User user = new User(name, passwordEncoder.encode(password), person);

        userRepository.store(user);
        return user;
    }
}
