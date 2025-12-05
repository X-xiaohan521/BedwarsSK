package com.SevenTap.bedwarssk;

import com.andrei1058.bedwars.api.arena.GameState;
import com.andrei1058.bedwars.api.events.gameplay.GameStateChangeEvent;
import com.andrei1058.bedwars.api.events.player.PlayerBedBreakEvent;
import com.andrei1058.bedwars.api.events.player.PlayerKillEvent;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class GameListener implements Listener {

    private final BedwarsSKPlugin plugin;
    private final GameManager gameManager;

    public GameListener() {
        this.plugin = BedwarsSKPlugin.getInstance();
        this.gameManager = plugin.getGameManager();
    }

    @EventHandler
    public void onGameStart(GameStateChangeEvent event) {
        if (event.getNewState().equals(GameState.playing)) {
            if (gameManager.getAssignedPlayers().size() < gameManager.getPlayerCount()) {
                Bukkit.broadcastMessage(ChatColor.RED + "未分配三国杀身份，进行普通起床!");
                return;
            } else {
                gameManager.startGame();
            }
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