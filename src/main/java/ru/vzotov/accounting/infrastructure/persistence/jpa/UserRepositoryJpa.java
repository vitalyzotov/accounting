package ru.vzotov.accounting.infrastructure.persistence.jpa;

import ru.vzotov.accounting.domain.model.User;
import ru.vzotov.accounting.domain.model.UserRepository;
import ru.vzotov.person.domain.model.PersonId;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;

public class UserRepositoryJpa extends JpaRepository implements UserRepository {

    UserRepositoryJpa(EntityManager em) {
        super(em);
    }

    @Override
    public User find(String name) {
        try {
            return em.createQuery("from User where name = :name", User.class)
                    .setParameter("name", name)
                    .getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }

    }

    @Override
    public User findByPersonId(PersonId personId) {
        try {
            return em.createQuery("from User where person.personId = :personId", User.class)
                    .setParameter("personId", personId)
                    .getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }

    @Override
    public void store(User user) {
        em.persist(user);
    }
}
