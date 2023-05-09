package ru.itsjava.dao;

public class UserALreadyExistException extends RuntimeException {
    public UserALreadyExistException(String message) {
        super(message);
    }
}
