package ru.vzotov.accounting.interfaces.accounting.facade;

import ru.vzotov.accounting.domain.model.User;
import ru.vzotov.person.domain.model.Person;

public interface UserManagementFacade {
    User registerNewUser(String name, String password, String firstName, String lastName, String displayName);
    User getUserByPersonId(String personId);
    Person getPerson(String personId);
}
