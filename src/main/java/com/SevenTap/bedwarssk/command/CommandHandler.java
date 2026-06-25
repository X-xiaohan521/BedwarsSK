package com.SevenTap.bedwarssk.command;

import java.util.ArrayList;
import java.util.List;

import com.SevenTap.bedwarssk.game.GameManager;
import com.SevenTap.bedwarssk.PlayerStatus;
import com.SevenTap.bedwarssk.PublicRolesAfterDeath;
import com.SevenTap.bedwarssk.item.MagicWandManager;
import com.SevenTap.bedwarssk.item.MagicWandType;
import com.SevenTap.bedwarssk.message.MessageSender;
import com.andrei1058.bedwars.BedWars;
import com.andrei1058.bedwars.api.arena.IArena;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class CommandHandler implements CommandExecutor {

    private final GameManager gameManager;
    private final MessageSender messageSender;
    private final MagicWandManager magicWandManager;

    public CommandHandler(GameManager gameManager, MessageSender messageSender, MagicWandManager magicWandManager) {
        this.gameManager = gameManager;
        this.messageSender = messageSender;
        this.magicWandManager = magicWandManager;
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
                    messageSender.sendHelp(sender);
                    return true;
                }

                String subCommand = args[0].toLowerCase();

                if (!sender.isOp()) {
                    switch (subCommand) {
                        case "roleall":
                            // 允许非 OP 玩家（在淘汰为旁观者后）使用 `/bwsk roleall` 命令查看所有玩家身份
                            if (gameManager.getPlayerStatus(player).equals(PlayerStatus.FINAL_DEAD)) {
                                messageSender.sendAllPlayerRoles(player);
                            } else {
                                sender.sendMessage(ChatColor.RED + "你只能在被淘汰后使用该指令。");
                            }
                            return true;
                        case "publicrole":
                            // 允许非 OP 玩家（在淘汰为旁观者后）使用 `/bwsk publicrole` 命令宣布自己身份
                            if (gameManager.getPlayerStatus(player).equals(PlayerStatus.FINAL_DEAD)) {
                                if (gameManager.getPublicRolesAfterDeath().equals(PublicRolesAfterDeath.OPTIONAL) ||
                                        gameManager.getPublicRolesAfterDeath().equals(PublicRolesAfterDeath.FORCE_PUBLIC)) {
                                    messageSender.announceRole(player);
                                } else {
                                    sender.sendMessage(ChatColor.RED + "本局游戏不允许自行公开身份。");
                                }
                            } else {
                                sender.sendMessage(ChatColor.RED + "你只能在被淘汰后使用该指令。");
                            }
                            return true;
                        case "role":
                            // 允许非 OP 玩家查看自己身份
                            messageSender.sendRole(player);
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
                        messageSender.sendRole(player);
                        break;

                    case "roleall":
                        messageSender.sendAllPlayerRoles(player);
                        break;

                    case "setpublicrole":
                        if (args.length == 2) {
                            switch (args[1]) {
                                case "true":
                                    gameManager.setPublicRolesAfterDeath(PublicRolesAfterDeath.FORCE_PUBLIC);
                                    sender.sendMessage(ChatColor.GREEN + "玩家身份将在淘汰后公开。");
                                    break;
                                case "false":
                                    gameManager.setPublicRolesAfterDeath(PublicRolesAfterDeath.FORCE_NOT_PUBLIC);
                                    sender.sendMessage(ChatColor.GREEN + "玩家身份将不会在淘汰后公开。");
                                    break;
                                case "optional":
                                    gameManager.setPublicRolesAfterDeath(PublicRolesAfterDeath.OPTIONAL);
                                    sender.sendMessage(ChatColor.GREEN + "玩家可在死亡后选择是否公开身份。");
                                    break;
                                default:
                                    sender.sendMessage(ChatColor.RED + "请输入有效的值（true/false/optional）!");
                            }
                        } else {
                            sender.sendMessage(ChatColor.RED + "用法: /bwsk setpublicrole <true/false/optional>");
                        }
                        break;

                    case "publicrole":
                        // 允许 OP 玩家使用 `/bwsk publicrole` 命令宣布自己身份
                        if (gameManager.getPublicRolesAfterDeath().equals(PublicRolesAfterDeath.OPTIONAL) ||
                                gameManager.getPublicRolesAfterDeath().equals(PublicRolesAfterDeath.FORCE_PUBLIC)) {
                            messageSender.announceRole(player);
                        } else {
                            sender.sendMessage(ChatColor.RED + "本局游戏不允许自行公开身份，可用 /bwsk setpublicrole 设置。");
                        }
                        return true;

                    case "givewands":
                        if (args.length != 2) {
                            sender.sendMessage(ChatColor.RED + "用法: /bwsk givewands <player>");
                            return true;
                        }
                        Player target = Bukkit.getPlayer(args[1]);
                        if (target == null) {
                            sender.sendMessage(ChatColor.RED + "玩家未在线或不存在: " + args[1]);
                            return true;
                        }
                        target.getInventory().addItem(magicWandManager.createWand(MagicWandType.LEVEL1));
                        target.getInventory().addItem(magicWandManager.createWand(MagicWandType.LEVEL2));
                        target.getInventory().addItem(magicWandManager.createWand(MagicWandType.LEVEL3));
                        sender.sendMessage(ChatColor.GREEN + "已向 " + target.getName() + " 发放三把魔法锄头。");
                        target.sendMessage(ChatColor.GREEN + "你已获得三把 BedwarsSK 魔法锄头。");
                        return true;

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
                        messageSender.sendGameStatus(sender);
                        break;

                    case "reset":
                        if (gameManager.isGameStarted()) {
                            gameManager.resetGame();
                        } else {
                            sender.sendMessage(ChatColor.RED + "游戏未开始!");
                        }
                        break;

                    default:
                        messageSender.sendHelp(sender);
                        break;
                }
                return true;
            }
        }

        return false;
    }
}
