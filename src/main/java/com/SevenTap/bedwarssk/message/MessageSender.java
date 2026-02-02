package com.SevenTap.bedwarssk.message;

import com.SevenTap.bedwarssk.PublicRolesAfterDeath;
import com.SevenTap.bedwarssk.Role;
import com.SevenTap.bedwarssk.game.GameManager;
import com.andrei1058.bedwars.api.arena.team.TeamColor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;

public class MessageSender {
    private final GameManager gameManager;

    public MessageSender(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    public void announceRole(Player player) {
        Role role = gameManager.getPlayerRole(player);
        if (role == null || !gameManager.isGameStarted()) {
            return;
        }
        Bukkit.broadcastMessage(
                TeamColor.getChatColor(gameManager.getArena().getTeam(player).getColor().toString())
                        + player.getName() + ChatColor.YELLOW + " 选择公开身份！身份：" +
                        role.getColor() + role.getDisplayName() + ChatColor.YELLOW + "！");
    }

    public void sendGameStatus(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "=== 游戏状态 ===");
        sender.sendMessage(ChatColor.YELLOW + "玩家数量: " + gameManager.getPlayerCount());
        sender.sendMessage(ChatColor.YELLOW + "主公显示: " + gameManager.isEmperorShown());
        sender.sendMessage(ChatColor.YELLOW + "已分配身份玩家: " + gameManager.getAssignedPlayers().size());
        sender.sendMessage(ChatColor.YELLOW + "游戏状态: " +
                (gameManager.isGameStarted() ? "进行中" : "未开始"));
    }

    public void sendRole(Player player) {
        Role role = gameManager.getPlayerRole(player);
        if (role != null) {
            player.sendMessage(ChatColor.GOLD + "=== 你的身份 ===");
            player.sendMessage(ChatColor.YELLOW + "身份: " + role.getDisplayName());
            player.sendMessage(ChatColor.YELLOW + "胜利条件: " + role.getWinCondition());
        } else {
            player.sendMessage(ChatColor.RED + "你还没有被分配身份!");
        }
    }

    public void sendAllPlayerRoles(Player player) {
        player.sendMessage(ChatColor.GOLD + "=== 所有玩家身份 ===");
        for (Map.Entry<String, Role> entry : gameManager.getAllRoles().entrySet()) {
            player.sendMessage(entry.getValue().getColor() + entry.getKey() +
                    ": " + entry.getValue().getDisplayName());
        }
    }

    public void sendHelp(CommandSender sender) {
        if (sender.isOp()) {
            sender.sendMessage(ChatColor.GOLD + "=== BedwarsSK 命令帮助 ===");
            sender.sendMessage(ChatColor.YELLOW + "/bwsk playercounts <5-8> - 设置玩家数量");
            sender.sendMessage(ChatColor.YELLOW + "/bwsk emperorshown <true/false> - 设置主公身份显示");
            sender.sendMessage(ChatColor.YELLOW + "/bwsk assign - 分配身份");
            sender.sendMessage(ChatColor.YELLOW + "/bwsk role - 查看自己身份");
            sender.sendMessage(ChatColor.YELLOW + "/bwsk roleall - 查看所有人身份");
            sender.sendMessage(ChatColor.YELLOW + "/bwsk setpublicrole <true/false/optional> - 设置玩家淘汰后是否公开身份（optional：允许玩家自行选择）");
            sender.sendMessage(ChatColor.YELLOW + "/bwsk publicrole - 宣布自己身份（仅允许在旁观游戏时使用）");
            sender.sendMessage(ChatColor.YELLOW + "/bwsk start - 开始游戏");
            sender.sendMessage(ChatColor.YELLOW + "/bwsk status - 查看游戏状态");
            sender.sendMessage(ChatColor.YELLOW + "/bwsk reset - 重置游戏");
        } else {
            sender.sendMessage(ChatColor.GOLD + "=== BedwarsSK 命令帮助 ===");
            sender.sendMessage(ChatColor.YELLOW + "/bwsk role - 查看自己身份");
            sender.sendMessage(ChatColor.YELLOW + "/bwsk roleall - 查看所有人身份（仅允许在旁观游戏时使用）");
            if (gameManager.getPublicRolesAfterDeath().equals(PublicRolesAfterDeath.OPTIONAL) ||
                    gameManager.getPublicRolesAfterDeath().equals(PublicRolesAfterDeath.FORCE_PUBLIC)) {
                sender.sendMessage(ChatColor.YELLOW + "/bwsk publicrole - 宣布自己身份（仅允许在旁观游戏时使用）");
            }
        }
    }
}
