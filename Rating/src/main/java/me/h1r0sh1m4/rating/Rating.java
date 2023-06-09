package me.h1r0sh1m4.rating;

import me.h1r0sh1m4.rating.commands.ratingCMDpkg.RatingCMD;
import me.h1r0sh1m4.rating.commands.ratingCMDpkg.RatingCompleter;
import me.h1r0sh1m4.rating.db.MySQLDatabase;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public final class Rating extends JavaPlugin implements Listener {
    private MySQLDatabase database;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        database = connectDB();
        getCommand("rating").setExecutor(new RatingCMD(database));
        getCommand("rating").setTabCompleter(new RatingCompleter());
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
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

    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent event) {
        String playerName = event.getPlayer().getName();
        if (!database.exists(playerName)) {
            database.addPlayer(playerName);
        }
        event.getPlayer().sendMessage(database.getHistory(playerName));
        database.addHistory(playerName, "", true);
    }
}