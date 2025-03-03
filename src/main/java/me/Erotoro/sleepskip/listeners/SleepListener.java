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
            World world = triggeringPlayer.getWorld();
            skipNight(world);
            ActionBar.sendToAll("&aНочь пропущена! Доброе утро!");
            sleepingPlayers.clear();
        } else {
            ActionBar.sendToAll("&e" + sleeping + "/" + needed + " игроков спит. Нужно " + needed + " для пропуска ночи!");
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
        // Если сервер работает под Folia, используем глобальный регион scheduler для изменения времени
        if (Bukkit.getServer().getName().contains("Folia")) {
            Bukkit.getGlobalRegionScheduler().execute(plugin, () -> {
                world.setTime(0);
                world.setStorm(false);
                world.setThundering(false);
            });
        } else {
            world.setTime(0);
            world.setStorm(false);
            world.setThundering(false);
        }
    }
}