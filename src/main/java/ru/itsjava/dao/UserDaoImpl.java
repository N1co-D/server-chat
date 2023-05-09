package ru.itsjava.dao;

import lombok.AllArgsConstructor;
import ru.itsjava.domain.User;
import ru.itsjava.utils.Props;

import java.sql.*;

@AllArgsConstructor
public class UserDaoImpl implements UserDao {
    private final Props props;

    @Override
    public User findByNameAndPassword(String name, String password) {
        try (Connection connection = DriverManager.getConnection(
                props.getValue("db.url"),
                props.getValue("db.login"),
                props.getValue("db.password"));
        ) {
            PreparedStatement preparedStatement = connection.prepareStatement("select count(*) cnt from schema_java.users where name = ? and password = ?;");
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, password);

            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();

            int userCount = resultSet.getInt("cnt");

            if (userCount == 1) {
                return new User(name, password);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        throw new RuntimeException("User not found!");
    }
}

