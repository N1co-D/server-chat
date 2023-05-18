package ru.itsjava.dao;

import lombok.AllArgsConstructor;
import ru.itsjava.domain.User;
import ru.itsjava.utils.Props;

import java.net.Socket;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class UserDaoImpl implements UserDao {
    private final Props props;
    private Socket socket;

    public UserDaoImpl(Props props) {
        this.props = props;
    }

    @Override
    public List<String> getLastMessages(int count) {
        List<String> lastMessages = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(
                props.getValue("db.url"),
                props.getValue("db.login"),
                props.getValue("db.password"))
        ) {
            String query = "SELECT message FROM chatLog ORDER BY timestamp DESC LIMIT ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, count);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String message = resultSet.getString("message");
                lastMessages.add(message);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lastMessages;
    }

    @Override
    public User findByNameAndPassword(String name, String password) {
        try (Connection connection = DriverManager.getConnection(
                props.getValue("db.url"),
                props.getValue("db.login"),
                props.getValue("db.password"))
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
        return newUserRegistration(name, password);
    }

    @Override
    public User newUserRegistration(String name, String password) {
        if (!isUsernameTaken(name)) {
            try (Connection connection = DriverManager.getConnection(
                    props.getValue("db.url"),
                    props.getValue("db.login"),
                    props.getValue("db.password"))
            ) {
                PreparedStatement preparedStatement = connection.prepareStatement("insert into schema_java.users (name, password) values (?, ?);");
                preparedStatement.setString(1, name);
                preparedStatement.setString(2, password);
                preparedStatement.executeUpdate();

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } else {
            throw new UserALreadyExistException("User already exists!");
        }
        return new User(name, password);
    }

    private boolean isUsernameTaken(String name) {
        try (Connection connection = DriverManager.getConnection(
                props.getValue("db.url"),
                props.getValue("db.login"),
                props.getValue("db.password"))
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

//@AllArgsConstructor
//public class UserDaoImpl implements UserDao {
//    private final Props props;
//    private Socket socket;
//
//    public UserDaoImpl(Props props) {
//        this.props = props;
//    }
//
//    @Override
//    public List<String> getLastMessages(int count) {
//        List<String> lastMessages = new ArrayList<>();
//
//        try (Connection connection = DriverManager.getConnection(
//                props.getValue("db.url"),
//                props.getValue("db.login"),
//                props.getValue("db.password"));
//        ) {
//            String query = "SELECT message FROM chatLog ORDER BY timestamp DESC LIMIT ?";
//            PreparedStatement preparedStatement = connection.prepareStatement(query);
//            preparedStatement.setInt(1, count);
//            ResultSet resultSet = preparedStatement.executeQuery();
//
//            while (resultSet.next()) {
//                String message = resultSet.getString("message");
//                lastMessages.add(message);
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return lastMessages;
//    }
//
//        @Override
//        public User findByNameAndPassword (String name, String password){
//            try (Connection connection = DriverManager.getConnection(
//                    props.getValue("db.url"),
//                    props.getValue("db.login"),
//                    props.getValue("db.password"));
//            ) {
//                PreparedStatement preparedStatement = connection.prepareStatement("select count(*) cnt from schema_java.users where name = ? and password = ?;");
//                preparedStatement.setString(1, name);
//                preparedStatement.setString(2, password);
//
//                ResultSet resultSet = preparedStatement.executeQuery();
//                resultSet.next();
//
//                int userCount = resultSet.getInt("cnt");
//
//                if (userCount == 1) {
//                    return new User(name, password);
//                }
//            } catch (SQLException throwables) {
//                throwables.printStackTrace();
//            }
//            return newUserRegistration(name, password);
//        }
//
//        @Override
//        public User newUserRegistration (String name, String password){
//            if (!isUsernameTaken(name)) {
//                try (Connection connection = DriverManager.getConnection(
//                        props.getValue("db.url"),
//                        props.getValue("db.login"),
//                        props.getValue("db.password"));
//                ) {
//                    PreparedStatement preparedStatement = connection.prepareStatement("insert into schema_java.users (name, password) values (?, ?);");
//                    preparedStatement.setString(1, name);
//                    preparedStatement.setString(2, password);
//                    preparedStatement.executeUpdate();
//
//                } catch (SQLException e) {
//                    throw new RuntimeException(e);
//                }
//            } else {
//                throw new UserALreadyExistException("User already exists!");
//            }
//            return new User(name, password);
//        }
//
//        private boolean isUsernameTaken (String name){
//            try (Connection connection = DriverManager.getConnection(
//                    props.getValue("db.url"),
//                    props.getValue("db.login"),
//                    props.getValue("db.password"));
//            ) {
//                PreparedStatement preparedStatement = connection.prepareStatement("select count(*) cnt from schema_java.users where name = ?");
//
//                preparedStatement.setString(1, name);
//                ResultSet resultSet = preparedStatement.executeQuery();
//                resultSet.next();
//                int count = resultSet.getInt(1);
//                resultSet.close();
//                preparedStatement.close();
//                return count > 0;
//            } catch (SQLException e) {
//                throw new RuntimeException(e);
//            }
//        }
//    }