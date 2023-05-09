package ru.itsjava.dao;

import ru.itsjava.domain.User;

public interface UserDao {
    User findByNameAndPassword(String name, String password);
    User newUserRegistration(String name, String password);
}
