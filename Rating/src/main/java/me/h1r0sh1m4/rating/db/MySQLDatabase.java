package me.h1r0sh1m4.rating.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.*;

public class MySQLDatabase {
    private static final String PLAYERS_RATING_TABLE = "playersRating";
    private final HikariDataSource dataSource;

    public MySQLDatabase(String host, int port, String database, String user, String password) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(String.format("jdbc:mysql://%s:%d/%s", host, port, database));
        config.setUsername(user);
        config.setPassword(password);
        config.addDataSourceProperty("cachePrepStmts", true);
        config.addDataSourceProperty("prepStmtCacheSize", 250);
        config.addDataSourceProperty("prepStmtCacheSqlLimit", 2048);

        dataSource = new HikariDataSource(config);
        createPlayersRatingTable();
    }

    private void createPlayersRatingTable() {
        try (Connection connection = dataSource.getConnection(); Statement statement = connection.createStatement()) {
            String sql = String.format("CREATE TABLE IF NOT EXISTS `%s` (" +
                    "`id` INT AUTO_INCREMENT PRIMARY KEY, " +
                    "`name` TEXT NOT NULL, " +
                    "`rating` INT NOT NULL, " +
                    "`messagesHistory` TEXT)", PLAYERS_RATING_TABLE);
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create playersRating table", e);
        }
    }

    public boolean exists(String playerName) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     String.format("SELECT * FROM `%s` WHERE `name` = ?", PLAYERS_RATING_TABLE))) {
            statement.setString(1, playerName);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to check player existence", e);
        }
    }

    public void addPlayer(String playerName) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     String.format("INSERT INTO `%s` (`name`, `rating`, `messagesHistory`) VALUES (?, ?, ?)", PLAYERS_RATING_TABLE))) {
            statement.setString(1, playerName);
            statement.setInt(2, 500);
            statement.setString(3, "");
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to add player", e);
        }
    }

    public void setRating(String playerName, int rating) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     String.format("UPDATE `%s` SET `rating` = ? WHERE `name` = ?", PLAYERS_RATING_TABLE))) {
            statement.setInt(1, rating);
            statement.setString(2, playerName);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to set player rating", e);
        }
    }

    public int getRating(String playerName) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     String.format("SELECT `rating` FROM `%s` WHERE `name` = ?", PLAYERS_RATING_TABLE))) {
            statement.setString(1, playerName);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
            return 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get player rating", e);
        }
    }

    public String getHistory(String playerName) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     String.format("SELECT `messagesHistory` FROM `%s` WHERE `name` = ?", PLAYERS_RATING_TABLE))) {
            statement.setString(1, playerName);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString(1);
            }
            return "";
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get player history", e);
        }
    }

    public void addHistory(String playerName, String newMessage, boolean deleteHistory) {
        String history = getHistory(playerName);
        history += "§9==================================\n" + newMessage + "\n§9==================================\n\n\n";
        if (deleteHistory) {
            history = "";
        }
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     String.format("UPDATE `%s` SET `messagesHistory` = ? WHERE `name` = ?", PLAYERS_RATING_TABLE))) {
            statement.setString(1, history);
            statement.setString(2, playerName);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to add player history", e);
        }
    }

    public void close() {
        dataSource.close();
    }
}