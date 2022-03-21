package ru.vzotov.accounting.infrastructure.persistence.jpa;

import ru.vzotov.accounting.domain.model.PersonRepository;
import ru.vzotov.person.domain.model.Person;
import ru.vzotov.person.domain.model.PersonId;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

public class PersonRepositoryJpa extends JpaRepository implements PersonRepository {

    public PersonRepositoryJpa(EntityManager em) {
        super(em);
    }

    @Override
    public Person find(PersonId personId) {
        try {
            return em.createQuery("from Person where personId = :personId", Person.class)
                    .setParameter("personId", personId)
                    .getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }

    }
}
