package me.h1r0sh1m4.rating;

import me.h1r0sh1m4.rating.commands.RatingCmd;
import me.h1r0sh1m4.rating.db.MySQLDatabase;
import me.h1r0sh1m4.rating.events.PlayerListener;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

public final class Rating extends JavaPlugin {

    private MySQLDatabase database;

    @Override
    public void onEnable() {

        saveDefaultConfig();

        database = connectDB();

        RatingCmd ratingCmd = new RatingCmd(database);
        getCommand("rating").setExecutor(ratingCmd);
        getCommand("rating").setTabCompleter(ratingCmd);

        Bukkit.getPluginManager().registerEvents(new PlayerListener(database), this);
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);
        database.close();
    }

    private MySQLDatabase connectDB() {
        FileConfiguration config = getConfig();
        String host = config.getString("database.host");
        int port = config.getInt("database.port");
        String dbName = config.getString("database.dbname");
        String username = config.getString("database.username");
        String password = config.getString("database.password");
        return new MySQLDatabase(host, port, dbName, username, password);
    }
}