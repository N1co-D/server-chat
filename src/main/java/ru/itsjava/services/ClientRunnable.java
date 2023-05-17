package ru.itsjava.services;

import lombok.SneakyThrows;
import ru.itsjava.dao.UserDao;
import ru.itsjava.domain.User;
import ru.itsjava.utils.Props;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ClientRunnable implements Runnable, Observer {
    private final Socket socket;
    private final ServerService serverService;
    private User user;
    private final UserDao userDao;
    private File chattingHistory = new File("C:\\Users\\dmark\\IdeaProjects\\java-online-project\\server-chat\\server-chat\\src\\main\\resources\\chattingHistory.txt");
    private final MessageDao messageDao;
    private final List<String> messageList = new ArrayList<>();

    public ClientRunnable(Socket socket, ServerService serverService, UserDao userDao) {
        this.socket = socket;
        this.serverService = serverService;
        this.userDao = userDao;
        this.messageDao = new MessageDaoImpl(new Props());
    }

    @SneakyThrows
    @Override
    public void run() {
        System.out.println("Client connected");
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String messageFromClient = bufferedReader.readLine();

        if (messageFromClient != null && messageFromClient.startsWith("!autho!")) {
            String login = messageFromClient.substring(7).split(":")[0];
            String password = messageFromClient.substring(7).split(":")[1];
            user = userDao.findByNameAndPassword(login, password);
            serverService.addObserver(this);

            while ((messageFromClient = bufferedReader.readLine()) != null) {
                System.out.println(user.getName() + ": " + messageFromClient);
                serverService.notifyObserverExpectMe(this, (user.getName() + ":" + messageFromClient));

                PrintWriter printWriter = new PrintWriter(new FileWriter(chattingHistory, true));
                printWriter.println(user.getName() + ":" + messageFromClient);
                printWriter.close();

                synchronized (messageList) {
                    messageList.add(messageFromClient);
                    chatLogging(messageList);
                }
            }

        } else if ((messageFromClient.startsWith("!reg!"))) {
            String newLogin = messageFromClient.substring(5).split(":")[0];
            String newPassword = messageFromClient.substring(5).split(":")[1];
            user = userDao.newUserRegistration(newLogin, newPassword);
            serverService.addObserver(this);

            while ((messageFromClient = bufferedReader.readLine()) != null) {
                System.out.println(user.getName() + ": " + messageFromClient);
                serverService.notifyObserverExpectMe(this, (user.getName() + ":" + messageFromClient));

                PrintWriter printWriter = new PrintWriter(chattingHistory);
                printWriter.println(user.getName() + ":" + messageFromClient);

                synchronized (messageList) {
                    messageList.add(messageFromClient);
                    chatLogging(messageList);
                }
            }
        }
    }

    public void chatLogging(List<String> messagesToWrite) {

        synchronized (messageList) {
            // Создаем копию списка сообщений для записи
            messagesToWrite = new ArrayList<>(messageList);
            messageList.clear();
        }

        for (String message : messagesToWrite) {
            messageDao.insertMessageToDatabase(user.getName(), message);
        }
        messagesToWrite.clear();
    }

    @SneakyThrows
    @Override
    public void notifyMe(String message) {
        PrintWriter clientWriter = new PrintWriter(socket.getOutputStream());
        clientWriter.println(message);
        clientWriter.flush();
        synchronized (messageList) {
            messageList.add(message); // Добавляем сообщение в список для записи в базу данных
        }
    }
}


//        if (authorization(bufferedReader)) {
//            serverService.addObserver(this);
//            while ((messageFromClient = bufferedReader.readLine()) != null) {
//                System.out.println(user.getName() + ": " + messageFromClient);
//                serverService.notifyObserverExpectMe(this,(user.getName() + ":" + messageFromClient));
//            }
//        } else {
//            if (registration(bufferedReader)) {
//                serverService.addObserver(this);
//                while ((messageFromClient = bufferedReader.readLine()) != null) {
//                    System.out.println(user.getName() + ": " + messageFromClient);
//                    serverService.notifyObserverExpectMe(this, (user.getName() + ":" + messageFromClient));
//                }
//            }
//        }
//
//    @SneakyThrows
//    private boolean authorization(BufferedReader bufferedReader) {
//        String authorizationMessage;
//        while ((authorizationMessage = bufferedReader.readLine()) != null) {
//            //!autho!login:password
//            if (authorizationMessage.startsWith("!autho!")) {
//                String login = authorizationMessage.substring(7).split(":")[0];
//                String password = authorizationMessage.substring(7).split(":")[1];
//                user = userDao.findByNameAndPassword(login, password);
//                return true;
//            }
//        }
//        return false;
//    }

//    @SneakyThrows
//    private boolean registration(BufferedReader bufferedReader) {
//        String registrationMessage;
//        while ((registrationMessage = bufferedReader.readLine()) != null) {
//            if (registrationMessage.startsWith("!regist!")) {
//                String login = registrationMessage.substring(8).split(":")[0];
//                String password = registrationMessage.substring(8).split(":")[1];
//                user = userDao.newUserRegistration(login, password);
//                return true;
//            }
//        }
//        return false;
//    }

