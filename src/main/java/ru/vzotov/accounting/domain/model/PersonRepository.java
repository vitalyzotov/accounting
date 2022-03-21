package ru.vzotov.accounting.domain.model;

import ru.vzotov.person.domain.model.Person;
import ru.vzotov.person.domain.model.PersonId;

public interface PersonRepository {
    Person find(PersonId personId);
}
