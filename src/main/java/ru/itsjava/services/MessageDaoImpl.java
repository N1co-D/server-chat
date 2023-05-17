package ru.itsjava.services;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import ru.itsjava.utils.Props;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

@AllArgsConstructor
public class MessageDaoImpl implements MessageDao {
    private final Props props;

    @SneakyThrows
    public void insertMessageToDatabase(String name, String message) {
        try (Connection connection = DriverManager.getConnection(
                props.getValue("db.url"),
                props.getValue("db.login"),
                props.getValue("db.password"));
        ) {
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO chatLog (name, message) VALUES (?, ?)");
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, message);
            preparedStatement.executeUpdate();
        }
    }
}

