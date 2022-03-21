package ru.vzotov.accounting.domain.model;

import ru.vzotov.person.domain.model.PersonId;

public interface UserRepository {
    User find(String name);
    User findByPersonId(PersonId personId);
    void store(User user);
}
