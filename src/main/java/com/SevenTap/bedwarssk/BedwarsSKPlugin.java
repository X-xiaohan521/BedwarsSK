package com.SevenTap.bedwarssk;

import com.andrei1058.bedwars.api.arena.team.TeamColor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.andrei1058.bedwars.BedWars;
import com.andrei1058.bedwars.api.arena.IArena;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
        getLogger().info("作者: SevenTap & UniMilk");
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
            // 过滤控制台请求
            if (sender instanceof ConsoleCommandSender) {
                sender.sendMessage(ChatColor.RED + "请勿在控制台使用该指令。");
                return true;
            } else if (sender instanceof Player) {
                Player player = (Player) sender;

                if (args.length == 0) {
                    sendHelp(sender);
                    return true;
                }

                String subCommand = args[0].toLowerCase();
                
                if (!sender.isOp()) {
                    switch (subCommand) {
                        case "roleall":
                            // 允许非 OP 玩家（在淘汰为旁观者后）使用 `/bwsk roleall` 命令查看所有玩家身份
                            if (gameManager.getPlayerStatus(player).equals(PlayerStatus.FINAL_DEAD)) {
                                sendAllPlayerRoles(player);
                            } else {
                                sender.sendMessage(ChatColor.RED + "你只能在被淘汰后使用该指令。");
                            }
                            return true;
                        case "publicrole":
                            // 允许非 OP 玩家（在淘汰为旁观者后）使用 `/bwsk publicrole` 命令宣布自己身份
                            if (gameManager.getPlayerStatus(player).equals(PlayerStatus.FINAL_DEAD)) {
                                if (gameManager.getPublicRolesAfterDeath().equals(PublicRolesAfterDeath.OPTIONAL) ||
                                        gameManager.getPublicRolesAfterDeath().equals(PublicRolesAfterDeath.FORCE_PUBLIC)) {
                                    announceRole(player);
                                } else {
                                    sender.sendMessage(ChatColor.RED + "本局游戏不允许自行公开身份。");
                                }
                            } else {
                                sender.sendMessage(ChatColor.RED + "你只能在被淘汰后使用该指令。");
                            }
                            return true;
                        case "role":
                            // 允许非 OP 玩家查看自己身份
                            sendRole(player);
                            return true;
                        default:
                            sender.sendMessage(ChatColor.RED + "你没有权限使用该指令。");
                            return true;
                    }
                }


                switch (subCommand) {
                    case "playercounts":
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
                        break;

                    case "emperorshown":
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
                        break;

                    case "assign":
                        if (gameManager.getPlayerCount() == 0) {
                            sender.sendMessage(ChatColor.RED + "请先设置玩家数量!");
                            return true;
                        }

                        List<Player> onlinePlayers = new ArrayList<>(Bukkit.getOnlinePlayers());
                        if (onlinePlayers.size() < gameManager.getPlayerCount()) {
                            sender.sendMessage(ChatColor.RED + "在线玩家不足! 需要 " + gameManager.getPlayerCount() + " 人，当前只有 " + onlinePlayers.size() + " 人");
                            return true;
                        }

                        // 分配身份
                        gameManager.assignRoles(onlinePlayers.subList(0, gameManager.getPlayerCount()));
                        sender.sendMessage(ChatColor.GREEN + "身份分配完成!");
                        break;

                    case "role":
                        sendRole(player);
                        break;

                    case "roleall":
                        sendAllPlayerRoles(player);
                        break;

                    case "setpublicrole":
                        if (args.length == 2) {
                            switch (args[1]) {
                                case "true":
                                    gameManager.setPublicRolesAfterDeath(PublicRolesAfterDeath.FORCE_PUBLIC);
                                    sender.sendMessage(ChatColor.YELLOW + "玩家身份将在死亡后公开。");
                                    break;
                                case "false":
                                    gameManager.setPublicRolesAfterDeath(PublicRolesAfterDeath.FORCE_NOT_PUBLIC);
                                    sender.sendMessage(ChatColor.YELLOW + "玩家身份将在死亡后公开。");
                                    break;
                                case "optional":
                                    gameManager.setPublicRolesAfterDeath(PublicRolesAfterDeath.OPTIONAL);

                                    break;
                                default:
                                    sender.sendMessage(ChatColor.RED + "请输入有效的值（true/false/optional）!");
                            }
                        } else {
                            sender.sendMessage(ChatColor.RED + "用法: /bwsk setpublicrole <true/false/optional>");
                        }
                        break;

                    case "start":
                        if (gameManager.getAssignedPlayers().size() < gameManager.getPlayerCount()) {
                            sender.sendMessage(ChatColor.RED + "请先分配身份!");
                            return true;
                        }

                        if (!BedWars.getAPI().getArenaUtil().isPlaying((Player) sender)) {
                            sender.sendMessage(ChatColor.RED + "请先进入起床战争!");
                            return true;
                        } else {
                            IArena arena = BedWars.getAPI().getArenaUtil().getArenaByPlayer((Player) sender);
                            gameManager.startGame(arena);
                        }

                        sender.sendMessage(ChatColor.GREEN + "游戏开始!");
                        break;

                    case "status":
                        sendGameStatus(sender);
                        break;

                    case "reset":
                        if (gameManager.isGameStarted()) {
                            gameManager.resetGame();
                        } else {
                            sender.sendMessage(ChatColor.RED + "游戏未开始!");
                        }
                        break;

                    default:
                        sendHelp(sender);
                        break;
                }
                return true;
            }
        }

        return false;
    }

    private void announceRole(Player player) {
        Role role = gameManager.getPlayerRole(player);
        if (role == null || !gameManager.isGameStarted()) {
            return;
        }
        Bukkit.broadcastMessage(
                TeamColor.getChatColor(gameManager.getArena().getTeam(player).getColor().toString())
                + player.getName() + ChatColor.YELLOW + " 选择公开身份！身份：" +
                role.getColor() + role.getDisplayName() + ChatColor.YELLOW + "！");
    }

    private void sendGameStatus(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "=== 游戏状态 ===");
        sender.sendMessage(ChatColor.YELLOW + "玩家数量: " + gameManager.getPlayerCount());
        sender.sendMessage(ChatColor.YELLOW + "主公显示: " + gameManager.isEmperorShown());
        sender.sendMessage(ChatColor.YELLOW + "已分配身份玩家: " + gameManager.getAssignedPlayers().size());
        sender.sendMessage(ChatColor.YELLOW + "游戏状态: " +
                (gameManager.isGameStarted() ? "进行中" : "未开始"));
    }

    private void sendRole(Player player) {
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

    private void sendHelp(CommandSender sender) {
        if (sender.isOp()) {
            sender.sendMessage(ChatColor.GOLD + "=== BedwarsSK 命令帮助 ===");
            sender.sendMessage(ChatColor.YELLOW + "/bwsk playercounts <5-8> - 设置玩家数量");
            sender.sendMessage(ChatColor.YELLOW + "/bwsk emperorshown <true/false> - 设置主公身份显示");
            sender.sendMessage(ChatColor.YELLOW + "/bwsk assign - 分配身份");
            sender.sendMessage(ChatColor.YELLOW + "/bwsk role - 查看自己身份");
            sender.sendMessage(ChatColor.YELLOW + "/bwsk roleall - 查看所有人身份");
            sender.sendMessage(ChatColor.YELLOW + "/bwsk setpublicrole <true/false/optional> - 设置玩家淘汰后是否公开身份（optional：允许玩家自行选择）");
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