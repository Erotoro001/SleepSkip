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
     * Отправляет сообщение в ActionBar всем игрокам указанное количество секунд.
     * @param plugin ссылка на главный класс плагина
     * @param message сообщение для отображения
     * @param durationInSeconds длительность в секундах
     */
    public static void sendToAll(SleepSkip plugin, String message, int durationInSeconds) {
        String formattedMessage = ChatColor.translateAlternateColorCodes('&', message);
        new BukkitRunnable() {
            int ticksLeft = durationInSeconds;

            @Override
            public void run() {
                if (ticksLeft <= 0) {
                    cancel();
                    return;
                }
                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(formattedMessage));
                }
                ticksLeft--;
            }
        }.runTaskTimer(plugin, 0L, 20L); // обновление каждую секунду (20 тиков)
    }
}