package me.h1r0sh1m4.rating.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.*;
import java.util.ArrayList;

public class MySQLDatabase{
    private static final String playersRating = "playersRating";
    private final HikariDataSource src;



    public MySQLDatabase(String host, int port, String database, String user, String password) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database);
        config.setUsername(user);
        config.setPassword(password);
        config.addDataSourceProperty("cachePrepStmts", true);
        config.addDataSourceProperty("prepStmtCacheSize", 250);
        config.addDataSourceProperty("prepStmtCacheSqlLimit", 2048);

        src = new HikariDataSource(config);

        try {
            Connection c = connect(); PreparedStatement ppst = c.prepareStatement(String.format(
                "CREATE TABLE IF NOT EXISTS `%s` (" +
                        "`id` INTEGER AUTO_INCREMENT PRIMARY KEY, " +
                        "`name` TEXT NOT NULL," +
                        "`rating` INTEGER NOT NULL," +
                        "`messagesHistory` TEXT);"
                ,playersRating));
            ppst.executeUpdate();
        } catch (SQLException e) {throw new RuntimeException(e);}

    }



    public boolean exists(String playersName){
        try (Connection c = connect(); PreparedStatement ppst = c.prepareStatement(String.format("SELECT * FROM `%s` WHERE `name` = ?", playersRating))){
                ppst.setString(1,playersName);
                ResultSet rs = ppst.executeQuery();
                return rs.next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }



    public void addPlayer(String playersName){
        try (Connection c = connect(); PreparedStatement ppst = c.prepareStatement(String.format("INSERT INTO `%s` (`name`, `rating`,`messagesHistory`) VALUES (?, ?, ?)", playersRating))){
            ppst.setString(1,playersName);
            ppst.setInt(2,500);
            ppst.setString(3,"");
            ppst.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }



    public void setRating(String playersName, int rating){
        try (Connection c = connect(); PreparedStatement ppst = c.prepareStatement(String.format("UPDATE `%s` SET rating = ? WHERE name = ?", playersRating))){
            ppst.setInt(1,rating);
            ppst.setString(2,playersName);
            ppst.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }



    public int getRating(String playersName){
        try (Connection c = connect(); PreparedStatement ppst = c.prepareStatement(String.format("SELECT rating FROM `%s` WHERE name = ?", playersRating))){
            ppst.setString(1,playersName);
            ResultSet rs = ppst.executeQuery();
            rs.next();
            return rs.getInt(1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public String getHistory(String playersName){
        try (Connection c = connect(); PreparedStatement ppst = c.prepareStatement(String.format("SELECT messagesHistory FROM `%s` WHERE name = ?", playersRating))){
            ppst.setString(1,playersName);
            ResultSet rs = ppst.executeQuery();
            rs.next();
            return rs.getString(1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void addHistory(String playersName, String newMessage, boolean deleteHistory){
        if (!deleteHistory) {
            newMessage = getHistory(playersName) + "§9---------------------------------\n" + newMessage + "\n§9---------------------------------\n\n";
        }
        else{
            newMessage = "";
        }
        try (Connection c = connect(); PreparedStatement ppst = c.prepareStatement(String.format("UPDATE `%s` SET messagesHistory = ? WHERE name = ?", playersRating))){
            ppst.setString(1,newMessage);
            ppst.setString(2,playersName);
            ppst.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Connection connect() throws SQLException {
        return src.getConnection();
    }



    public void close(){
        src.close();
    }
}