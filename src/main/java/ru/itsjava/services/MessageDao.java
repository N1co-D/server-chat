package ru.itsjava.services;

public interface MessageDao {
    void insertMessageToDatabase(String name, String message);
}
