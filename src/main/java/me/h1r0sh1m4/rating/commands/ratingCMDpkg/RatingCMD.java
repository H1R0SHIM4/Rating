package me.h1r0sh1m4.rating.commands.ratingCMDpkg;

import me.h1r0sh1m4.rating.db.MySQLDatabase;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class RatingCMD implements CommandExecutor {
    private final MySQLDatabase db;

    public RatingCMD(MySQLDatabase database) {
        this.db = database;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            handleHelpCommand(sender);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "add":
                handleAddCommand(sender, args);
                break;
            case "remove":
                handleRemoveCommand(sender, args);
                break;
            case "check":
                handleCheckCommand(sender, args);
                break;
            case "reset":
                handleResetCommand(sender, args);
                break;
            case "help":
                handleHelpCommand(sender);
                break;
            default:
                sender.sendMessage("§4Неправильное использование команды! Правильное использование находится в /rating help");
        }

        return true;
    }

    private void handleAddCommand(CommandSender sender, String[] args) {
        if (!sender.hasPermission("rating.change")) {
            sender.sendMessage("§4У вас недостаточно прав!");
            return;
        }

        if (args.length < 3) {
            sender.sendMessage("§4Неправильное использование команды! Правильное использование находится в /rating help");
            return;
        }

        String playerName = args[1];
        int ratingToAdd = parseRating(args[2], sender);

        if (ratingToAdd == -1) {
            return;
        }

        Player player = Bukkit.getPlayerExact(playerName);

        if (player == null && !db.exists(playerName)) {
            sender.sendMessage("§4Игрок §e" + playerName + " §4не найден!");
            return;
        }

        int currentRating = db.getRating(playerName);
        db.setRating(playerName, currentRating + ratingToAdd);
        sender.sendMessage("§bВы §aдобавили §bигроку §e" + playerName + " " + ratingToAdd + " §bрейтинга!");

        if (player != null) {
            player.sendMessage("§bВам было §aдобавлено §e" + ratingToAdd + "§b рейтинга!");
            return;
        }

        db.addHistory(playerName, "§bВам было §aдобавлено §e" + ratingToAdd + "§b рейтинга!", false);
    }

    private void handleRemoveCommand(CommandSender sender, String[] args) {
        if (!sender.hasPermission("rating.change")) {
            sender.sendMessage("§4У вас недостаточно прав!");
            return;
        }

        if (args.length < 3) {
            sender.sendMessage("§4Неправильное использование команды! Правильное использование находится в /rating help");
            return;
        }

        String playerName = args[1];
        int ratingToRemove = parseRating(args[2], sender);

        if (ratingToRemove == -1) {
            return;
        }

        Player player = Bukkit.getPlayerExact(playerName);

        if (player == null && db.exists(playerName)) {
            sender.sendMessage("§4Игрок §c" + playerName + " §4не найден!");
            return;
        }

        int currentRating = db.getRating(playerName);
        db.setRating(playerName, currentRating - ratingToRemove);
        sender.sendMessage("§bВы §cудалили §bу игрока §e" + playerName + " §c" + ratingToRemove + "§b рейтинга!");

        if (player != null) {
            player.sendMessage("§bУ вас было §cудалено §e" + ratingToRemove + "§b рейтинга!");
            return;
        }

        db.addHistory(playerName, "§bУ вас было удалено §e" + ratingToRemove + "§b рейтинга!", false);
    }

    private void handleResetCommand(CommandSender sender, String[] args) {
        if (!sender.hasPermission("rating.change")) {
            sender.sendMessage("§4У вас недостаточно прав!");
            return;
        }

        if (args.length < 2) {
            sender.sendMessage("§4Неправильное использование команды! Правильное использование находится в /rating help");
            return;
        }

        String playerName = args[1];
        Player player = Bukkit.getPlayerExact(playerName);

        if (player == null && db.exists(playerName)) {
            sender.sendMessage("§4Игрок §e" + playerName + " §4не найден!");
        }

        db.setRating(playerName, 500);
        sender.sendMessage("§aВы сбросили рейтинг игрока §b" + playerName + "§a!");

        if (player != null) {
            player.sendMessage("§cВаш рейтинг был сброшен!");
            return;
        }

        db.addHistory(playerName, "§cВаш рейтинг был сброшен!", false);
    }

    private void handleCheckCommand(CommandSender sender, String[] args) {
        if (args.length == 1) {
            String playerName = sender.getName();
            int rating = db.getRating(playerName);
            sender.sendMessage("§bВаш рейтинг: §e" + rating);
            return;
        }
        if (args.length == 2) {
            String playerName = args[1];
            Player player = Bukkit.getPlayerExact(playerName);

            if (player == null && !db.exists(playerName)) {
                sender.sendMessage("§4Игрок §c" + playerName + " §4не найден!");
                return;
            }
            int rating = db.getRating(playerName);
            sender.sendMessage("§bРейтинг игрока §e" + playerName + "§b: §e" + rating);
            return;
        }

        sender.sendMessage("§4Неправильное использование команды! Правильное использование находится в /rating help");
    }

    private void handleHelpCommand(CommandSender sender) {
        sender.sendMessage("§1===========================§b Использование§e /rating§1 =============================");
        sender.sendMessage("§e/rating add [Игрок] [Количество_рейтинга]§9 - §aДобавить§9 рейтинг игроку");
        sender.sendMessage("§e/rating remove [Игрок] [Количество_рейтинга]§9 - §cУдалить§9 рейтинг у игрока");
        sender.sendMessage("§e/rating check [Игрок] §9- Проверить рейтинг игрока, выводит ваш рейтинг, если не писать игрока.");
        sender.sendMessage("§e/rating reset [Игрок] §9- §4Сбросить §9рейтинг игрока");
        sender.sendMessage("§e/rating help §9- Показать помощь по командам рейтинга");
    }

    private int parseRating(String ratingString, CommandSender sender) {
        try {
            int rating = Integer.parseInt(ratingString);
            if (rating <= 0) {
                sender.sendMessage("§4Количество рейтинга должно быть положительным числом!");
                return -1;
            }
            return rating;
        } catch (NumberFormatException e) {
            sender.sendMessage("§4Неправильное использование команды! Правильное использование: /rating add [Игрок] [Количество_рейтинга]");
            return -1;
        }
    }
}