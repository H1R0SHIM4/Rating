package me.h1r0sh1m4.rating.events;

import me.h1r0sh1m4.rating.db.MySQLDatabase;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerListener implements Listener {

    private final MySQLDatabase database;

    public PlayerListener(MySQLDatabase database) {
        this.database = database;
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
