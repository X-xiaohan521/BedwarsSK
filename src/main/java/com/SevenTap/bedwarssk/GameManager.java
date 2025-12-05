package com.SevenTap.bedwarssk;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.*;

public class GameManager {

    private int playerCount = 8; // 默认8人
    private boolean emperorShown = true; // 默认显示主公
    private boolean gameStarted = false;

    // 存储玩家身份 - 修复Diamond操作符
    private final Map<String, Role> playerRoles = new HashMap<String, Role>();
    // 存储玩家状态 - 修复Diamond操作符
    private final Map<String, PlayerStatus> playerStatus = new HashMap<String, PlayerStatus>();
    // 主公玩家
    private String emperorPlayer;

    // 计分板相关
    private Scoreboard scoreboard;

    // Role枚举定义
    public enum Role {
        EMPEROR("主公", ChatColor.RED, "消灭所有反贼和内奸"),
        LOYALIST("忠臣", ChatColor.BLUE, "保护主公，消灭反贼和内奸"),
        TRAITOR("内奸", ChatColor.DARK_PURPLE, "消灭除自己外的所有玩家"),
        REBEL("反贼", ChatColor.GREEN, "消灭主公");

        private final String displayName;
        private final ChatColor color;
        private final String winCondition;

        Role(String displayName, ChatColor color, String winCondition) {
            this.displayName = displayName;
            this.color = color;
            this.winCondition = winCondition;
        }

        public String getDisplayName() {
            return color + displayName;
        }

        public ChatColor getColor() {
            return color;
        }

        public String getWinCondition() {
            return winCondition;
        }
    }

    // 玩家状态枚举
    public enum PlayerStatus {
        ALIVE("存活"),
        BED_BROKEN("床被破坏"),
        DEAD("死亡");

        private final String display;

        PlayerStatus(String display) {
            this.display = display;
        }

        public String getDisplay() {
            return display;
        }
    }

    public GameManager() {
        // 初始化计分板
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        if (manager != null) {
            scoreboard = manager.getNewScoreboard();
        }
    }

    public void setPlayerCount(int count) {
        this.playerCount = count;
    }

    public int getPlayerCount() {
        return playerCount;
    }

    public void setEmperorShown(boolean shown) {
        this.emperorShown = shown;
    }

    public boolean isEmperorShown() {
        return emperorShown;
    }

    public boolean isGameStarted() {
        return gameStarted;
    }

    public void assignRoles(List<Player> players) {
        if (players.size() != playerCount) {
            Bukkit.broadcastMessage(ChatColor.RED + "错误：玩家数量不匹配!");
            return;
        }

        // 重置之前的分配
        playerRoles.clear();
        playerStatus.clear();
        emperorPlayer = null;

        // 准备身份列表 - 修复Diamond操作符
        List<Role> roles = generateRoles();
        Collections.shuffle(roles);
        Collections.shuffle(players);

        // 分配身份
        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            Role role = roles.get(i);
            playerRoles.put(player.getName(), role);
            playerStatus.put(player.getName(), PlayerStatus.ALIVE);

            // 发送私人消息
            player.sendMessage(ChatColor.GOLD + "=== 身份分配结果 ===");
            player.sendMessage(ChatColor.YELLOW + "你的身份是: " + role.getDisplayName());
            player.sendMessage(ChatColor.YELLOW + "胜利条件: " + role.getWinCondition());
            player.sendMessage(ChatColor.GRAY + "输入 /bwsk role 可再次查看身份");

            // 如果是主公，记录下来
            if (role == Role.EMPEROR) {
                emperorPlayer = player.getName();
            }
        }

        Bukkit.broadcastMessage(ChatColor.GREEN + "身份分配完成！");
    }

    private List<Role> generateRoles() {
        // 修复Diamond操作符
        List<Role> roles = new ArrayList<Role>();

        switch (playerCount) {
            case 5:
                roles.add(Role.EMPEROR);
                roles.add(Role.LOYALIST);
                roles.add(Role.TRAITOR);
                roles.add(Role.REBEL);
                roles.add(Role.REBEL);
                break;
            case 6:
                roles.add(Role.EMPEROR);
                roles.add(Role.LOYALIST);
                roles.add(Role.TRAITOR);
                roles.add(Role.REBEL);
                roles.add(Role.REBEL);
                roles.add(Role.REBEL);
                break;
            case 7:
                roles.add(Role.EMPEROR);
                roles.add(Role.LOYALIST);
                roles.add(Role.LOYALIST);
                roles.add(Role.TRAITOR);
                roles.add(Role.REBEL);
                roles.add(Role.REBEL);
                roles.add(Role.REBEL);
                break;
            case 8:
            default:
                roles.add(Role.EMPEROR);
                roles.add(Role.LOYALIST);
                roles.add(Role.LOYALIST);
                roles.add(Role.TRAITOR);
                roles.add(Role.REBEL);
                roles.add(Role.REBEL);
                roles.add(Role.REBEL);
                roles.add(Role.REBEL);
                break;
        }

        return roles;
    }

    public void showEmperor() {
        if (emperorPlayer == null) return;

        Player emperor = Bukkit.getPlayer(emperorPlayer);
        if (emperor == null) return;

        // 设置名称前缀
        emperor.setDisplayName(ChatColor.RED + "[主公] " + ChatColor.WHITE + emperor.getName());
        emperor.setPlayerListName(ChatColor.RED + "[主公] " + ChatColor.WHITE + emperor.getName());

        // 广播主公身份
        Bukkit.broadcastMessage(ChatColor.GOLD + "=================================");
        Bukkit.broadcastMessage(ChatColor.RED + "主公是: " + emperor.getName());
        Bukkit.broadcastMessage(ChatColor.YELLOW + "主公已亮明身份!");
        Bukkit.broadcastMessage(ChatColor.GOLD + "=================================");
    }

    public void hideEmperor() {
        if (emperorPlayer != null) {
            Player emperor = Bukkit.getPlayer(emperorPlayer);
            if (emperor != null) {
                emperor.setDisplayName(emperor.getName());
                emperor.setPlayerListName(emperor.getName());
            }
        }
    }

    public Role getPlayerRole(Player player) {
        if (player == null) return null;
        return playerRoles.get(player.getName());
    }

    // 修复：添加getAllRoles方法
    public Map<String, Role> getAllRoles() {
        return new HashMap<String, Role>(playerRoles);
    }

    public Set<String> getAssignedPlayers() {
        return playerRoles.keySet();
    }

    public void startGame() {
        gameStarted = true;
        Bukkit.broadcastMessage(ChatColor.GOLD + "=================================");
        Bukkit.broadcastMessage(ChatColor.GREEN + "身份起床战争正式开始!");
        Bukkit.broadcastMessage(ChatColor.YELLOW + "祝各位游戏愉快!");
        Bukkit.broadcastMessage(ChatColor.GOLD + "=================================");
    }

    public void resetGame() {
        playerRoles.clear();
        playerStatus.clear();
        gameStarted = false;
        emperorPlayer = null;

        // 重置玩家显示名称
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.setDisplayName(player.getName());
            player.setPlayerListName(player.getName());
        }

        Bukkit.broadcastMessage(ChatColor.GREEN + "游戏已重置!");
    }

    // 床被破坏时的处理 - 暂时留空，等BedWars事件正常后再实现
    public void onBedDestroyed(List<Player> victimPlayers) {
        for (Player player : victimPlayers) {
            if (!gameStarted || !playerRoles.containsKey(player.getName())) return;
            playerStatus.put(player.getName(), PlayerStatus.BED_BROKEN);
        }
    }

    // 玩家死亡时的处理 - 暂时留空，等BedWars事件正常后再实现
    public void onPlayerDeath(Player player) {
        if (!gameStarted || !playerRoles.containsKey(player.getName())) return;

        PlayerStatus status = playerStatus.get(player.getName());
        if (status == PlayerStatus.BED_BROKEN || status == PlayerStatus.DEAD) {
            playerStatus.put(player.getName(), PlayerStatus.DEAD);
            Role role = playerRoles.get(player.getName());

            Bukkit.broadcastMessage(ChatColor.RED + player.getName() + " (" +
                    role.getDisplayName() + ChatColor.RED + ") 已被淘汰!");
        }
    }

    // 获取计分板（如果需要）
    public Scoreboard getScoreboard() {
        return scoreboard;
    }
}