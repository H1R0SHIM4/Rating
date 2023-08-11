package me.h1r0sh1m4.rating.commands.ratingCMDpkg;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RatingCompleter implements TabCompleter {

    private static final List<String> FIRST_ARGUMENTS = Arrays.asList("add", "remove", "check", "reset", "help");

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        List<String> completionsToReturn = new ArrayList<>();
        switch (args.length) {

            case 1:
                for (String arg : FIRST_ARGUMENTS) {
                    if (arg.startsWith(args[0].toLowerCase())) {
                        completionsToReturn.add(arg);
                    }
                }
                break;

            case 2:
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (player.getName().startsWith(args[1].toLowerCase())) {
                        completionsToReturn.add(player.getName());
                    }
                }
                break;

            case 3:
                completionsToReturn.add("[amount]");
                break;
        }
        return completionsToReturn;
    }
}