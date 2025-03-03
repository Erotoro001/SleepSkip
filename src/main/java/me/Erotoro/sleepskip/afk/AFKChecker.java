package me.Erotoro.sleepskip.afk;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class AFKChecker {

    // Заглушки для CMI и EssentialsX. После подключения реальных зависимостей замените типы.
    private final Object cmi;
    private final Object essentials;
    private final boolean ignoreAfk;

    public AFKChecker(@NotNull Plugin plugin) {
        Plugin cmiPlugin = Bukkit.getPluginManager().getPlugin("CMI");
        // Если требуется, можно проверить instanceof, но если плагин не найден, получим null.
        this.cmi = cmiPlugin; // заглушка

        Plugin essPlugin = Bukkit.getPluginManager().getPlugin("Essentials");
        this.essentials = essPlugin; // заглушка

        this.ignoreAfk = plugin.getConfig().getBoolean("settings.ignore-afk", true);
    }

    public boolean isPlayerAFK(Player player) {
        if (!ignoreAfk) return false;

        // Если CMI установлен, используйте его API (пример):
        if (cmi != null) {
            // Пример: return ((CMIUser)cmi.getPlayerManager().getUser(player)).isAfk();
            return false; // заглушка
        }

        // Если Essentials установлен, используйте его API (пример):
        if (essentials != null) {
            // Пример: return ((IEssentials)essentials).getUser(player).isAfk();
            return false; // заглушка
        }
        return false;
    }
}