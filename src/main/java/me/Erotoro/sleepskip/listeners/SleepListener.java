package me.Erotoro.sleepskip.listeners;

import me.Erotoro.sleepskip.SleepSkip;
import me.Erotoro.sleepskip.afk.AFKChecker;
import me.Erotoro.sleepskip.utils.ActionBar;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;

public class SleepListener implements Listener {

    private final SleepSkip plugin;
    private final AFKChecker afkChecker;
    private final Set<Player> sleepingPlayers = new HashSet<>();

    public SleepListener(SleepSkip plugin, AFKChecker afkChecker) {
        this.plugin = plugin;
        this.afkChecker = afkChecker;
    }

    @EventHandler
    public void onPlayerSleep(PlayerBedEnterEvent event) {
        if (event.getBedEnterResult() != PlayerBedEnterEvent.BedEnterResult.OK) return;

        Player player = event.getPlayer();
        sleepingPlayers.add(player);
        checkAndSkipNight(player);
    }

    @EventHandler
    public void onPlayerWakeUp(PlayerBedLeaveEvent event) {
        sleepingPlayers.remove(event.getPlayer());
    }

    private void checkAndSkipNight(Player triggeringPlayer) {
        long totalPlayers = Bukkit.getOnlinePlayers().stream()
                .filter(p -> !afkChecker.isPlayerAFK(p))
                .count();

        int sleeping = sleepingPlayers.size();
        int needed = getRequiredSleepers((int) totalPlayers);

        if (sleeping >= needed) {
            // Если достаточно игроков спит, запускаем плавное переключение ночи.
            skipNight(triggeringPlayer.getWorld());
        } else {
            // Читаем сообщение из конфига и заменяем плейсхолдеры
            String statusMsg = plugin.getConfig().getString("messages.sleepingStatus",
                    "&e{sleeping}/{needed} игроков спит. Нужно {needed} для пропуска ночи!");
            statusMsg = statusMsg.replace("{sleeping}", String.valueOf(sleeping))
                    .replace("{needed}", String.valueOf(needed));
            ActionBar.sendToAll(plugin, statusMsg, 5);
        }
    }

    private int getRequiredSleepers(int totalPlayers) {
        String requiredType = plugin.getConfig().getString("settings.required-type", "percent");
        if (requiredType.equalsIgnoreCase("fixed")) {
            return plugin.getConfig().getInt("settings.required-value", 1);
        } else {
            double percent = plugin.getConfig().getDouble("settings.required-value", 50);
            return (int) Math.ceil(totalPlayers * (percent / 100.0));
        }
    }

    private void skipNight(World world) {
        if (Bukkit.getServer().getName().contains("Folia")) {
            Bukkit.getGlobalRegionScheduler().runAtFixedRate(plugin, task -> {
                long currentTime = world.getTime();
                if (currentTime < 12541 || currentTime >= 24000) {
                    world.setTime(0);
                    world.setStorm(false);
                    world.setThundering(false);
                    // Читаем сообщение из конфига и отправляем его
                    String nightMsg = plugin.getConfig().getString("messages.nightSkipped",
                            "&aНочь пропущена! Доброе утро!");
                    ActionBar.sendToAll(plugin, nightMsg, 4);
                    sleepingPlayers.clear();
                    task.cancel();
                    return;
                }
                world.setTime(currentTime + 100);
            }, 1L, 2L);
        } else {
            new BukkitRunnable() {
                @Override
                public void run() {
                    long currentTime = world.getTime();
                    if (currentTime < 12541 || currentTime >= 24000) {
                        world.setTime(0);
                        world.setStorm(false);
                        world.setThundering(false);
                        String nightMsg = plugin.getConfig().getString("messages.nightSkipped",
                                "&aНочь пропущена! Доброе утро!");
                        ActionBar.sendToAll(plugin, nightMsg, 5);
                        sleepingPlayers.clear();
                        cancel();
                        return;
                    }
                    world.setTime(currentTime + 100);
                }
            }.runTaskTimer(plugin, 0L, 2L);
        }
    }
}