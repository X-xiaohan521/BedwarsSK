package com.SevenTap.bedwarssk;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.andrei1058.bedwars.BedWars;
import com.andrei1058.bedwars.api.arena.IArena;

import java.util.ArrayList;
import java.util.List;

public class BedwarsSKPlugin extends JavaPlugin {

    private static BedwarsSKPlugin instance;
    private GameManager gameManager;

    @Override
    public void onEnable() {
        instance = this;
        gameManager = new GameManager();

        // 检查BedWars1058是否加载
        if (Bukkit.getPluginManager().getPlugin("BedWars1058") == null) {
            getLogger().warning("BedWars1058未找到！某些功能可能无法正常工作。");
        }

        // 注册事件监听器
        Bukkit.getPluginManager().registerEvents(new GameListener(), this);

        getLogger().info("BedwarsSK插件已启用!");
        getLogger().info("作者: SevenTap");
        getLogger().info("版本: 1.0.0");
    }

    @Override
    public void onDisable() {
        getLogger().info("BedwarsSK插件已禁用!");
    }

    public static BedwarsSKPlugin getInstance() {
        return instance;
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("bwsk")) {
            if (args.length == 0) {
                sendHelp(sender);
                return true;
            }

            String subCommand = args[0].toLowerCase();

            if (subCommand.equals("playercounts")) {
                if (args.length == 2) {
                    try {
                        int count = Integer.parseInt(args[1]);
                        if (count < 5 || count > 8) {
                            sender.sendMessage(ChatColor.RED + "玩家数量必须在5-8之间!");
                            return true;
                        }
                        gameManager.setPlayerCount(count);
                        sender.sendMessage(ChatColor.GREEN + "已设置玩家数量为: " + count);
                    } catch (NumberFormatException e) {
                        sender.sendMessage(ChatColor.RED + "请输入有效的数字!");
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "用法: /bwsk playercounts <5-8>");
                }
            } else if (subCommand.equals("emperorshown")) {
                if (args.length == 2) {
                    boolean isShown = Boolean.parseBoolean(args[1]);
                    gameManager.setEmperorShown(isShown);
                    sender.sendMessage(ChatColor.GREEN + "主公身份显示已设置为: " + isShown);
                    if (isShown) {
                        sender.sendMessage(ChatColor.YELLOW + "主公身份将对所有人可见");
                    } else {
                        sender.sendMessage(ChatColor.YELLOW + "主公身份将对其他人隐藏");
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "用法: /bwsk emperorshown <true/false>");
                }
            } else if (subCommand.equals("assign")) {
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    if (gameManager.getPlayerCount() == 0) {
                        player.sendMessage(ChatColor.RED + "请先设置玩家数量!");
                        return true;
                    }

                    List<Player> onlinePlayers = new ArrayList<Player>(Bukkit.getOnlinePlayers());
                    if (onlinePlayers.size() < gameManager.getPlayerCount()) {
                        player.sendMessage(ChatColor.RED + "在线玩家不足! 需要 " + gameManager.getPlayerCount() + " 人，当前只有 " + onlinePlayers.size() + " 人");
                        return true;
                    }

                    // 分配身份
                    gameManager.assignRoles(onlinePlayers.subList(0, gameManager.getPlayerCount()));
                    player.sendMessage(ChatColor.GREEN + "身份分配完成!");

                    // 显示主公身份
                    if (gameManager.isEmperorShown()) {
                        gameManager.showEmperor();
                    }
                }
            } else if (subCommand.equals("role")) {
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    Role role = gameManager.getPlayerRole(player);
                    if (role != null) {
                        player.sendMessage(ChatColor.GOLD + "=== 你的身份 ===");
                        player.sendMessage(ChatColor.YELLOW + "身份: " + role.getDisplayName());
                        player.sendMessage(ChatColor.YELLOW + "胜利条件: " + role.getWinCondition());
                    } else {
                        player.sendMessage(ChatColor.RED + "你还没有被分配身份!");
                    }
                }
            } else if (subCommand.equals("roleall")) {
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    player.sendMessage(ChatColor.GOLD + "=== 所有玩家身份 ===");
                    for (String playerName : gameManager.getAllRoles().keySet()) {
                        Player target = Bukkit.getPlayer(playerName);
                        if (target != null) {
                            Role role = gameManager.getPlayerRole(target);
                            if (role != null) {
                                player.sendMessage(ChatColor.YELLOW + target.getName() + ": " +
                                        role.getDisplayName());
                            }
                        }
                    }
                }
            } else if (subCommand.equals("start")) {
                if (gameManager.getAssignedPlayers().size() < gameManager.getPlayerCount()) {
                    sender.sendMessage(ChatColor.RED + "请先分配身份!");
                    return true;
                }
                if (sender instanceof Player) {
                    if (!BedWars.getAPI().getArenaUtil().isPlaying((Player)sender)) {
                        sender.sendMessage(ChatColor.RED + "请先进入起床战争!");
                        return true;
                    } else {
                        IArena arena = BedWars.getAPI().getArenaUtil().getArenaByPlayer((Player)sender);
                        gameManager.startGame(arena);
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "不允许控制台启动游戏!");
                }                
                
                sender.sendMessage(ChatColor.GREEN + "游戏开始!");
            } else if (subCommand.equals("status")) {
                sender.sendMessage(ChatColor.GOLD + "=== 游戏状态 ===");
                sender.sendMessage(ChatColor.YELLOW + "玩家数量: " + gameManager.getPlayerCount());
                sender.sendMessage(ChatColor.YELLOW + "主公显示: " + gameManager.isEmperorShown());
                sender.sendMessage(ChatColor.YELLOW + "已分配身份玩家: " + gameManager.getAssignedPlayers().size());
                sender.sendMessage(ChatColor.YELLOW + "游戏状态: " +
                        (gameManager.isGameStarted() ? "进行中" : "未开始"));
            } else if (subCommand.equals("reset")) {
                gameManager.resetGame();
                sender.sendMessage(ChatColor.GREEN + "游戏已重置!");
            } else {
                sendHelp(sender);
            }
            return true;
        }
        return false;
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "=== BedwarsSK 命令帮助 ===");
        sender.sendMessage(ChatColor.YELLOW + "/bwsk playercounts <5-8> - 设置玩家数量");
        sender.sendMessage(ChatColor.YELLOW + "/bwsk emperorshown <true/false> - 设置主公身份显示");
        sender.sendMessage(ChatColor.YELLOW + "/bwsk assign - 分配身份");
        sender.sendMessage(ChatColor.YELLOW + "/bwsk role - 查看自己身份");
        sender.sendMessage(ChatColor.YELLOW + "/bwsk roleall - 查看所有人身份");
        sender.sendMessage(ChatColor.YELLOW + "/bwsk start - 开始游戏");
        sender.sendMessage(ChatColor.YELLOW + "/bwsk status - 查看游戏状态");
        sender.sendMessage(ChatColor.YELLOW + "/bwsk reset - 重置游戏");
    }
}