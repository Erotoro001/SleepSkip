package me.Erotoro.sleepskip.commands;

import me.Erotoro.sleepskip.SleepSkip;
import me.Erotoro.sleepskip.utils.ActionBar;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class SleepCommand implements CommandExecutor {

    private final SleepSkip plugin;

    public SleepCommand(SleepSkip plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            sender.sendMessage("§eИспользуйте: /sleep <reload|status>");
            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            plugin.reloadConfig();
            sender.sendMessage("§aКонфиг перезагружен!");
            return true;
        }

        if (args[0].equalsIgnoreCase("status")) {
            ActionBar.sendToAll("§eПлагин SleepSkip работает корректно.");
            return true;
        }

        return false;
    }
}