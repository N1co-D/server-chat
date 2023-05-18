package ru.itsjava.services;

public interface Observer {
    void notifyMe(String message);

    void update(Observable o, Object arg);
}
