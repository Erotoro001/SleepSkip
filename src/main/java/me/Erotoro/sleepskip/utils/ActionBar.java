package me.Erotoro.sleepskip.utils;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import me.Erotoro.sleepskip.SleepSkip;

@SuppressWarnings("deprecation")
public class ActionBar {

    /**
     * Отправляет сообщение в ActionBar всем игрокам в течение durationInSeconds секунд.
     * Если сервер работает под Folia, используется глобальный региональный scheduler.
     *
     * @param plugin             ссылка на главный класс плагина
     * @param message            сообщение для отображения
     * @param durationInSeconds  длительность в секундах
     */
    public static void sendToAll(SleepSkip plugin, String message, int durationInSeconds) {
        String formattedMessage = ChatColor.translateAlternateColorCodes('&', message);
        if (Bukkit.getServer().getName().contains("Folia")) {
            final int[] iterations = {0};
            // 20 тиков = 1 секунда
            Bukkit.getGlobalRegionScheduler().runAtFixedRate(plugin, task -> {
                iterations[0]++;
                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(formattedMessage));
                }
                if (iterations[0] >= durationInSeconds) {
                    task.cancel();
                }
            }, 1L, 20L); // initial delay изменён на 1L
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
                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(formattedMessage));
                    }
                    secondsLeft--;
                }
            }.runTaskTimer(plugin, 0L, 20L);
        }
    }
}