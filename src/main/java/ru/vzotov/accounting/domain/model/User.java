package ru.vzotov.accounting.domain.model;

import org.apache.commons.lang.Validate;
import ru.vzotov.person.domain.model.Person;
import ru.vzotov.ddd.shared.AggregateRoot;
import ru.vzotov.ddd.shared.Entity;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@AggregateRoot
public class User implements Entity<User> {

    private String name;

    private String password;

    private Person person;

    private String roles;

    private List<String> rolesList;

    public User(String name, String password, Person person, List<String> roles) {
        Validate.notEmpty(name);
        Validate.notEmpty(password);
        Validate.notNull(person);
        Validate.notEmpty(roles);

        this.name = name;
        this.password = password;
        this.person = person;
        this.roles = String.join(",", roles);
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

    public List<String> roles() {
        if(rolesList == null) {
            rolesList = Arrays.stream(roles.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toList());
        }
        return rolesList;
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
