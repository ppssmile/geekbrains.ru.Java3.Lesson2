package Lesson3.DB;

import Lesson3.server.ClientHandler;

import java.sql.*;

public final class ConnectionService {

    private ConnectionService() {
    }

    public static Connection connect() {
        try {
            return DriverManager.getConnection("jdbc:postgresql://127.0.0.1:8889/test1", "postgres", "root");
        } catch (SQLException a) {
            throw new RuntimeException("SWW", a);
        }
    }

    public static void rollback(Connection connection) {
        try {
            connection.rollback();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public static void close(Connection connection) {
        try {
            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public static boolean selectUserExist(String login, String password) {
        Connection connection = connect();
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM users WHERE login = ? AND pass = ?");
            statement.setString(1, login);
            statement.setString(2, password);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                return true;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            ConnectionService.close(connection);
        }
        return false;
    }

    public static synchronized boolean updateUsersName(ClientHandler clientHandler, String oldName, String newName) {
        Connection connection = connect();
        try {
            connection.setAutoCommit(false);
            PreparedStatement statement = connection.prepareStatement("UPDATE users SET login = ? WHERE login = ?");
            statement.setString(1, newName);
            statement.setString(2, oldName);
            int rs = statement.executeUpdate();
            connection.commit();
            if (rs == 1) {
                return true;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            clientHandler.sendMessage(throwables.getMessage());
        } finally {
            ConnectionService.close(connection);
        }
        return false;
    }
}