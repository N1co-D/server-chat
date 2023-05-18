package ru.itsjava.dao;

import ru.itsjava.domain.User;

import java.util.List;

public interface UserDao {
    List<String> getLastMessages(int count);

    User findByNameAndPassword(String name, String password);
    User newUserRegistration(String name, String password);
}
