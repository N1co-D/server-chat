package ru.itsjava;

import ru.itsjava.services.ServerService;
import ru.itsjava.services.ServerServiceImpl;

public class Application {
    public static void main(String[] args) {
        ServerService serverService = new ServerServiceImpl();
        serverService.start();

//        Props props = new Props();
//        System.out.println("props.getValue(\"db.url\") = " + props.getValue("db.url"));
//        UserDao userDao = new UserDaoImpl(new Props());
//        System.out.println("userDao.findByNameAndPassword(\"Alex\", \"qwerty\") = " + userDao.findByNameAndPassword("Alex", "qwerty"));
//        UserDao userDao = new UserDaoImpl(new Props());
//        userDao.newUserRegistration("ll", "lll");

    }
}
