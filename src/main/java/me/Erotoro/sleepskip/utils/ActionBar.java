package me.Erotoro.sleepskip.utils;

import me.Erotoro.sleepskip.SleepSkip;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class ActionBar {

    /**
     * Отправляет сообщение в ActionBar всем игрокам в течение durationInSeconds секунд.
     * Сообщение обрабатывается через MiniMessage с поддержкой RGB (например, "<gradient:#FF0000:#00FF00>Привет</gradient>").
     *
     * @param plugin             ссылка на главный класс плагина
     * @param message            сообщение для отображения (форматируется через MiniMessage)
     * @param durationInSeconds  длительность показа в секундах
     */
    public static void sendToAll(SleepSkip plugin, String message, int durationInSeconds) {
        Component component = MiniMessage.miniMessage().deserialize(message);
        if (Bukkit.getServer().getName().contains("Folia")) {
            final int[] iterations = {0};
            Bukkit.getGlobalRegionScheduler().runAtFixedRate(plugin, task -> {
                iterations[0]++;
                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.sendActionBar(component);
                }
                if (iterations[0] >= durationInSeconds) {
                    task.cancel();
                }
            }, 1L, 20L);
        } else {
            new BukkitRunnable() {
                int secondsLeft = durationInSeconds;
                @Override
                public void run() {
                    if (secondsLeft <= 0) {
                        cancel();
                        return;
                    }
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        player.sendActionBar(component);
                    }
                    secondsLeft--;
                }
            }.runTaskTimer(plugin, 0L, 20L);
        }
    }
}