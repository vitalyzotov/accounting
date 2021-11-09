package ru.vzotov.accounting.domain.model;

import org.apache.commons.lang.Validate;
import ru.vzotov.banking.domain.model.Person;
import ru.vzotov.ddd.shared.AggregateRoot;
import ru.vzotov.ddd.shared.Entity;

import java.util.Objects;

@AggregateRoot
public class User implements Entity<User> {

    private String name;

    private String password;

    private Person person;

    public User(String name, String password, Person person) {
        Validate.notEmpty(name);
        Validate.notEmpty(password);
        Validate.notNull(person);

        this.name = name;
        this.password = password;
        this.person = person;
    }

    public String name() {
        return name;
    }

    public String password() {
        return password;
    }

    public Person person() {
        return person;
    }

    @Override
    public boolean sameIdentityAs(User user) {
        return user != null && this.name.equalsIgnoreCase(user.name());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return sameIdentityAs(user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                '}';
    }

    protected User() {
        // for Hibernate
    }

    private Long id;
}
