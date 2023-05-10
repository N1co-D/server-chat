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
            } else {
                newUserRegistration(name, password);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return newUserRegistration(name, password);
//        throw new UserNotFoundException("User not found!");
    }

    @Override
    public User newUserRegistration(String name, String password) {
        if (!isUsernameTaken(name)) {
            try (Connection connection = DriverManager.getConnection(
                    props.getValue("db.url"),
                    props.getValue("db.login"),
                    props.getValue("db.password"));
            ) {
                PreparedStatement preparedStatement = connection.prepareStatement("insert into schema_java.users (name, password) values (?, ?);");
                preparedStatement.setString(1, name);
                preparedStatement.setString(2, password);
                preparedStatement.executeUpdate();

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
//        } else {
//            throw new UserALreadyExistException("User already exists!");
        }
        return new User(name, password);
    }

    //                preparedStatement.executeUpdate();
//                preparedStatement.close();
//                System.out.println("Регистрация прошла успешно!");
//                return new User(name, password);

    private boolean isUsernameTaken(String name) {
        try (Connection connection = DriverManager.getConnection(
                props.getValue("db.url"),
                props.getValue("db.login"),
                props.getValue("db.password"));
        ) {
            PreparedStatement preparedStatement = connection.prepareStatement("select count(*) cnt from schema_java.users where name = ?");

            preparedStatement.setString(1, name);
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            int count = resultSet.getInt(1);
            resultSet.close();
            preparedStatement.close();
            return count > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


}

