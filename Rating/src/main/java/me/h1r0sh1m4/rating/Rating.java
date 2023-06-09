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
     MySQLDatabase connectDB(){
        FileConfiguration filecfg = getConfig();
        return new MySQLDatabase(
                filecfg.getString("database.host"),
                filecfg.getInt("database.port"),
                filecfg.getString("database.dbname"),
                filecfg.getString("database.username"),
                filecfg.getString("database.password"));
    }
    @Override
    public void onEnable() {
         saveDefaultConfig();
        MySQLDatabase db = connectDB();
        db.close();
        getCommand("rating").setExecutor(new RatingCMD(connectDB()));
        getCommand("rating").setTabCompleter(new RatingCompleter());
        Bukkit.getPluginManager().registerEvents(this, this);
    }


    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent event){
        MySQLDatabase db = connectDB();
        String joinedPlayersName = event.getPlayer().getName();
        if(!db.exists(joinedPlayersName)) {
            db.addPlayer(joinedPlayersName);
        }
        event.getPlayer().sendMessage(db.getHistory(joinedPlayersName));
        db.addHistory(event.getPlayer().getName(),"",true);
        db.close();
    }
    @Override
    public void onDisable() {

    }
}
