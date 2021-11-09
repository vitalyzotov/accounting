package ru.vzotov.accounting.interfaces.accounting.facade;

import ru.vzotov.accounting.domain.model.User;

public interface UserManagementFacade {
    User registerNewUser(String name, String password, String firstName, String lastName, String displayName);
}
