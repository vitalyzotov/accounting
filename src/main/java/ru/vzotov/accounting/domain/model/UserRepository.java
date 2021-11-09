package ru.vzotov.accounting.domain.model;

public interface UserRepository {
    User find(String name);
    void store(User user);
}
