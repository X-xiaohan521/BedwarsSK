package com.SevenTap.bedwarssk;

import com.andrei1058.bedwars.api.events.player.PlayerBedBreakEvent;
import com.andrei1058.bedwars.api.events.player.PlayerKillEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class GameListener implements Listener {

    private final BedwarsSKPlugin plugin;

    public GameListener() {
        this.plugin = BedwarsSKPlugin.getInstance();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // 玩家加入时的处理
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        // 如果游戏进行中玩家退出，视为死亡
        if (plugin.getGameManager() != null && plugin.getGameManager().isGameStarted()) {
            plugin.getGameManager().onPlayerDeath(event.getPlayer());
        }
    }

    // BedWars床破坏事件
    @EventHandler
    public void onBedBreak(PlayerBedBreakEvent event) {
        if (plugin.getGameManager() != null) {
            plugin.getGameManager().onBedDestroyed(event.getVictimTeam().getMembers());
        }
    }

    // BedWars玩家击杀事件
    @EventHandler
    public void onPlayerKill(PlayerKillEvent event) {
        if (plugin.getGameManager() != null && event.getVictim() != null) {
            plugin.getGameManager().onPlayerDeath(event.getVictim());
        }
    }
}