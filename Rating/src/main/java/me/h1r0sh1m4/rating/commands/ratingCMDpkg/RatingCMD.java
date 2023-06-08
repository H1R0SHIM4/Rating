package me.h1r0sh1m4.rating.commands.ratingCMDpkg;

import me.h1r0sh1m4.rating.db.MySQLDatabase;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class RatingCMD implements CommandExecutor {
    MySQLDatabase db;

    public RatingCMD(MySQLDatabase db) {
        this.db = db;
    }



    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        try {

            if (!(commandSender instanceof Player)) {
                commandSender.sendMessage("Команда доступна только игрокам!");
                return true;
            }

            Player sender = (Player) commandSender;

            switch (args[0]) {

                case "add":
                    if(!sender.hasPermission("rating.change")){
                        commandSender.sendMessage("§4 У вас недостаточно прав!");
                        break;
                    }

                    if (Bukkit.getPlayer(args[1]) != null || db.exists(args[1]))
                    {
                        db.setRating(args[1], db.getRating(args[1]) + Integer.parseInt(args[2]));
                        commandSender.sendMessage("§a Вы добавили игроку §b" + args[1] + " " + args[2] + " §a рейтинга! ");

                        if (Bukkit.getPlayer(args[1]) != null)
                        {
                            Bukkit.getPlayer(args[1]).sendMessage("§b" + commandSender.getName() + "§a добавил вам §e" + args[2] + "§a рейтинга!");
                        } else
                        {
                            db.addHistory(args[1], ("§b" + commandSender.getName() + "§a добавил вам §e" + args[2] + "§a рейтинга!"), false);
                        }

                    } else {
                        commandSender.sendMessage("§4 Данного пользователя не существует!");
                    }
                    break;



                case "help":
                    commandSender.sendMessage("§b Использование: \n" +
                            "§e/rating add [Игрок] [Количество_рейтинга]§b -§a добавляет§b указанному игроку определённое количество рейтинга.\n" +
                            "§e/rating remove [Игрок] [Количество_рейтинга]§b -§c забирает§b у указанного игрока определённое количество рейтинга.\n" +
                            "§e/rating check [Игрок]§b - Отображает количество рейтинга указанного игрока. Если игрок не указан - отображает ваше количество рейтинга.\n" +
                            "§e/rating reset [Игрок]§b - Обнуляет количество рейтинга указанного человека, нулевым значением является 500.\n" +
                            "§e/rating help§b - Вызывает данное окно.");

                    break;
                case "check":
                    if (args.length < 2 || args[1].equals(commandSender.getName())) {
                        commandSender.sendMessage("§b У вас §e " + db.getRating(commandSender.getName()) + "§b рейтинга!");
                    } else {
                        commandSender.sendMessage("§b У данного игрока §e" + db.getRating(args[1]) + "§b рейтинга!");
                    }

                    break;

                case "reset":
                    if(!sender.hasPermission("rating.change")){
                        commandSender.sendMessage("§4 У вас недостаточно прав!");
                        break;
                    }
                    db.setRating(args[1], 500);
                    commandSender.sendMessage("§b Рейтинг игрока §e" + args[1] + " §b теперь равен 500 !");

                    break;

                case "remove":
                    if(!sender.hasPermission("rating.change")){
                        commandSender.sendMessage("§4 У вас недостаточно прав!");
                        break;
                    }
                    if (Bukkit.getPlayer(args[1]) != null || db.exists(args[1])) {
                        db.setRating(args[1], db.getRating(args[1]) + (Integer.parseInt(args[2]) * -1));
                        commandSender.sendMessage("§c Вы забрали у игрока §b" + args[1] + " " + args[2] + " §c рейтинга!");

                        if (Bukkit.getPlayer(args[1]) != null) {
                            Bukkit.getPlayer(args[1]).sendMessage("§b" + commandSender.getName() + "§c забрал у вас §e" + args[2] + "§c рейтинга!");
                        } else {
                            db.addHistory(args[1], ("§b" + commandSender.getName() + "§c забрал у вас §e" + args[2] + "§c рейтинга!"), false);
                        }

                    } else {
                        commandSender.sendMessage("§4 Данного пользователя не существует!");
                    }

                    break;
                default:
                    commandSender.sendMessage("§4 Неизвестная команда! Список команд в /rating help");

                    break;
            }
        }
        catch (Exception e){
            commandSender.sendMessage("§4 Неправильное использование команды! Правильное использование в /rating help");
        }
        return true;
    }
}
