package me.h1r0sh1m4.rating.commands.ratingCMDpkg;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class RatingCompleter implements TabCompleter{

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        String[] firstArgs = new String[]{"add", "remove", "check", "reset", "help"};
        Player[] players = Bukkit.getOnlinePlayers().toArray(new Player[0]);
        List<String> matches = new ArrayList<>();
        if(args.length == 1){
            for(String string: firstArgs)
            {
                if(string.toLowerCase().startsWith(args[0].toLowerCase()))
                {
                    matches.add(string);
                }
            }
        } else if (args.length == 2) {
            for(Player player: players)
            {
                if(player.getName().toLowerCase().startsWith(args[1].toLowerCase()))
                {
                    matches.add(player.getName());
                }
            }
        } else if (args.length == 3) {
            matches.add("[amount]");
            }
        return matches;
    }
}
