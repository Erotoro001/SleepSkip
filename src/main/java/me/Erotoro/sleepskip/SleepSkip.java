package me.Erotoro.sleepskip;

import me.Erotoro.sleepskip.afk.AFKChecker;
import me.Erotoro.sleepskip.commands.SleepCommand;
import me.Erotoro.sleepskip.listeners.SleepListener;
import org.bukkit.plugin.java.JavaPlugin;

public class SleepSkip extends JavaPlugin {

    private AFKChecker afkChecker;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        afkChecker = new AFKChecker(this);

        // Проверка, что команда "sleep" определена в plugin.yml
        if (getCommand("sleep") != null) {
            getCommand("sleep").setExecutor(new SleepCommand(this));
        } else {
            getLogger().warning("Команда 'sleep' не определена в plugin.yml!");
        }

        getServer().getPluginManager().registerEvents(new SleepListener(this, afkChecker), this);
        getLogger().info("SleepSkip включен!");
    }

    @Override
    public void onDisable() {
        getLogger().info("SleepSkip отключен!");
    }

    public AFKChecker getAfkChecker() {
        return afkChecker;
    }
}