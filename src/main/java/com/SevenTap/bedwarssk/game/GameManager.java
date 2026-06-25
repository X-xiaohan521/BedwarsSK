package com.SevenTap.bedwarssk.game;

import com.SevenTap.bedwarssk.PlayerStatus;
import com.SevenTap.bedwarssk.PublicRolesAfterDeath;
import com.SevenTap.bedwarssk.Role;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import org.bukkit.scoreboard.*;

// import org.bukkit.scheduler.BukkitRunnable;
// import org.bukkit.scheduler.BukkitTask;

import com.andrei1058.bedwars.api.arena.GameState;
import com.andrei1058.bedwars.api.arena.IArena;
import com.andrei1058.bedwars.api.arena.team.TeamColor;

import java.util.*;


public class GameManager {
    // 游戏相关超参数
    private int playerCount = 8; // 默认8人
    private boolean isEmperorShown = true; // 默认显示主公
    private boolean gameStarted = false;
    private boolean identityGame = false;
    private IArena arena;   // 起床地图
    private PublicRolesAfterDeath publicRolesAfterDeath = PublicRolesAfterDeath.FORCE_NOT_PUBLIC;

    // 玩家角色和状态
    private final Map<String, Role> playerRoles = new HashMap<>();
    private final Map<String, PlayerStatus> playerStatus = new HashMap<>();
    
    // 主公名字
    private String emperorPlayerOriginalName;
    private String emperorPlayerName;

    // 计分板（预留）
    private Scoreboard scoreboard;

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

    public PlayerStatus getPlayerStatus(Player player) {
        return playerStatus.getOrDefault(player.getName(), PlayerStatus.ALIVE);
    }

    public void setEmperorShown(boolean isShown) {
        this.isEmperorShown = isShown;
    }

    public boolean isEmperorShown() {
        return isEmperorShown;
    }

    public boolean isGameStarted() {
        return gameStarted;
    }

    public void setPublicRolesAfterDeath(PublicRolesAfterDeath publicRolesAfterDeath) {
        this.publicRolesAfterDeath = publicRolesAfterDeath;
    }

    public PublicRolesAfterDeath getPublicRolesAfterDeath() {
        return publicRolesAfterDeath;
    }

    public boolean isIdentityGame() {
        return identityGame;
    }

    public void setIdentityGame(boolean identityGame) {
        this.identityGame = identityGame;
    }

    public IArena getArena() {
        return arena;
    }

    public void assignRoles(List<Player> players) {
        if (players.size() != playerCount) {
            Bukkit.broadcastMessage(ChatColor.RED + "错误：玩家数量不匹配!");
            identityGame = false;
            return;
        }

        // 重置之前的分配
        playerRoles.clear();
        playerStatus.clear();
        emperorPlayerOriginalName = null;
        emperorPlayerName = null;

        // 准备身份列表
        List<Role> roles = Role.generateRoles(playerCount);
        Collections.shuffle(roles);
        Collections.shuffle(players);

        // 分配身份
        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            Role role = roles.get(i);
            playerRoles.put(player.getName(), role);
            playerStatus.put(player.getName(), PlayerStatus.ALIVE);

            // 如果是主公，记录下来
            if (role == Role.EMPEROR) {
                emperorPlayerOriginalName = player.getName();
            }
        }

        identityGame = true;
        Bukkit.broadcastMessage(ChatColor.GREEN + "身份分配完成！");
    }

    private void sendAssignedRoles() {
        for (Map.Entry<String, Role> entry : playerRoles.entrySet()) {
            Player player = Bukkit.getPlayer(entry.getKey());
            if (player != null) {
                player.sendMessage(ChatColor.GOLD + "=== 身份分配结果 ===");
                player.sendMessage(ChatColor.YELLOW + "你的身份是: " + entry.getValue().getDisplayName());
                player.sendMessage(ChatColor.YELLOW + "胜利条件: " + entry.getValue().getWinCondition());
                player.sendMessage(ChatColor.GRAY + "输入 /bwsk role 可再次查看身份");
            }
        }
    }

    public void showEmperor() {
        if (emperorPlayerOriginalName == null || !isEmperorShown) return;

        Player emperor = Bukkit.getPlayer(emperorPlayerOriginalName);
        if (emperor == null) return;

        // 设置主公名称前缀
        ChatColor emperorTeamColor = TeamColor.getChatColor(arena.getTeam(emperor).getColor().toString());
        emperorPlayerName = ChatColor.RED + "[主公] " + emperorTeamColor + emperorPlayerOriginalName;
        emperor.setDisplayName(emperorPlayerName);
        emperor.setPlayerListName(emperorPlayerName);

        // 广播主公身份
        Bukkit.broadcastMessage(ChatColor.GOLD + "=================================");
        Bukkit.broadcastMessage(ChatColor.RED + "主公是: " + emperorTeamColor + emperorPlayerOriginalName);
        Bukkit.broadcastMessage(ChatColor.YELLOW + "主公已亮明身份!");
        Bukkit.broadcastMessage(ChatColor.GOLD + "=================================");
    }

    public void hideEmperor() throws NullPointerException {
        // 移除 null check，故意抛出 NPE，方便调试
        Player emperor = Bukkit.getPlayer(emperorPlayerOriginalName);
        emperor.setDisplayName(emperorPlayerOriginalName);
        emperor.setPlayerListName(emperorPlayerOriginalName);
    }

    public Role getPlayerRole(Player player) {
        if (player == null) return null;
        return playerRoles.get(player.getName());
    }

    public Map<String, Role> getAllRoles() {
        return new HashMap<>(playerRoles);
    }

    public Set<String> getAssignedPlayers() {
        return playerRoles.keySet();
    }

    public void startGame(IArena arena) {
        // 设置游戏开始
        gameStarted = true;
        this.arena = arena;

        // 广播游戏开始消息
        Bukkit.broadcastMessage(ChatColor.GOLD + "=================================");
        Bukkit.broadcastMessage(ChatColor.GREEN + "身份起床战争正式开始!");
        Bukkit.broadcastMessage(ChatColor.YELLOW + "祝各位游戏愉快!");
        Bukkit.broadcastMessage(ChatColor.GOLD + "=================================");
        
        // 显示主公身份
        if (isEmperorShown()) {
            showEmperor();
        }

        // 发送身份
        sendAssignedRoles();
    }

    public void resetGame() {
        arena.changeStatus(GameState.restarting);

        playerRoles.clear();
        playerStatus.clear();
        gameStarted = false;
        identityGame = false;
        arena = null;

        hideEmperor();

        Bukkit.broadcastMessage(ChatColor.GREEN + "游戏已重置!");
    }

    public void onBedDestroyed(List<Player> victimPlayers) {
        for (Player player : victimPlayers) {
            if (!gameStarted || !playerRoles.containsKey(player.getName())) return;
            playerStatus.put(player.getName(), PlayerStatus.BED_BROKEN);
        }
    }

    public void onPlayerDeath(Player player) {
        if (!gameStarted || !playerRoles.containsKey(player.getName())) return;

        PlayerStatus status = playerStatus.get(player.getName());
        if (status == PlayerStatus.BED_BROKEN) {
            playerStatus.put(player.getName(), PlayerStatus.FINAL_DEAD);
            player.sendMessage(ChatColor.YELLOW + "你已被淘汰！可以输入 " + ChatColor.AQUA + "/bwsk roleall" + ChatColor.YELLOW + " 查看所有玩家身份。");

            if (publicRolesAfterDeath.equals(PublicRolesAfterDeath.FORCE_PUBLIC)) {
                player.sendMessage(ChatColor.YELLOW + "你也可以输入 " + ChatColor.AQUA + "/bwsk publicrole" + ChatColor.YELLOW + " 再次亮明自己的身份。");
                // 强制公开玩家身份
                Role role = playerRoles.get(player.getName());
                Bukkit.broadcastMessage(ChatColor.RED + player.getName() + " (" + role.getColor() +
                        role.getDisplayName() + ChatColor.RED + ") 已被淘汰!");
            } else if (publicRolesAfterDeath.equals(PublicRolesAfterDeath.OPTIONAL)) {
                player.sendMessage(ChatColor.YELLOW + "你也可以输入 " + ChatColor.AQUA + "/bwsk publicrole" + ChatColor.YELLOW + " 主动亮明自己的身份。");
            }

            checkGameEnd(player);
        }
    }

    // 获取计分板（如果需要）
    @SuppressWarnings("unused")
    public Scoreboard getScoreboard() {
        return scoreboard;
    }

    // 游戏结束检测
    private void checkGameEnd(Player finalDeadPlayer) {
        boolean isEmperorWin = false;
        boolean isTraitorWin = false;
        boolean isRebelWin = false;

        // 如果所有反贼和内奸均最终死亡，则判定主公阵营胜利
        for (Map.Entry<String, Role> entry : playerRoles.entrySet()) {
            if (entry.getValue().equals(Role.REBEL) || entry.getValue().equals(Role.TRAITOR)) {
                if (playerStatus.get(entry.getKey()).equals(PlayerStatus.FINAL_DEAD)) {
                    isEmperorWin = true;
                } else {
                    isEmperorWin = false;
                    break;
                }
            }
        }

        // 如果被最终击杀的是主公，判定：场上是否只有且仅有内奸存活，如果是，则内奸赢；反之，则反贼赢
        if (playerRoles.get(finalDeadPlayer.getName()).equals(Role.EMPEROR)) {
            for (Map.Entry<String, Role> entry : playerRoles.entrySet()) {
                if (!entry.getValue().equals(Role.TRAITOR)) {
                    if (!playerStatus.get(entry.getKey()).equals(PlayerStatus.FINAL_DEAD)) {
                        isTraitorWin = false;
                        isRebelWin = true;
                        break;
                    } else {
                        isTraitorWin = true;
                        //noinspection DataFlowIssue
                        isRebelWin = false;
                    }
                }
            }
        }
        
        // 执行游戏结束事件
        if (isEmperorWin) {
            announceVictory(Role.EMPEROR);
            resetGame();
        } else if (isTraitorWin) {
            announceVictory(Role.TRAITOR);
            resetGame();
        } else if (isRebelWin) {
            announceVictory(Role.REBEL);
            resetGame();
        }
    }

    private void announceVictory(Role winningRole) {
        gameStarted = false;
        
        Bukkit.broadcastMessage(ChatColor.GOLD + "=================================");
        Bukkit.broadcastMessage(ChatColor.GOLD + "游戏结束!");
        
        switch (winningRole) {
            case EMPEROR:
                Bukkit.broadcastMessage(ChatColor.RED + "主公阵营胜利!");
                // 显示所有忠臣
                for (Map.Entry<String, Role> entry : playerRoles.entrySet()) {
                    if (entry.getValue() == Role.EMPEROR) {
                        Bukkit.broadcastMessage(Role.EMPEROR.getColor() + "主公: " + entry.getKey());
                    } else if (entry.getValue() == Role.LOYALIST) {
                        Bukkit.broadcastMessage(Role.LOYALIST.getColor() + "忠臣: " + entry.getKey());
                    }
                }
                break;
            case REBEL:
                Bukkit.broadcastMessage(ChatColor.GREEN + "反贼阵营胜利!");
                // 显示所有反贼
                for (Map.Entry<String, Role> entry : playerRoles.entrySet()) {
                    if (entry.getValue() == Role.REBEL) {
                        Bukkit.broadcastMessage(ChatColor.GREEN + "反贼: " + entry.getKey());
                    }
                }
                break;
            case TRAITOR:
                Bukkit.broadcastMessage(ChatColor.DARK_PURPLE + "内奸胜利!");
                // 显示内奸
                for (Map.Entry<String, Role> entry : playerRoles.entrySet()) {
                    if (entry.getValue() == Role.TRAITOR) {
                        Bukkit.broadcastMessage(ChatColor.DARK_PURPLE + "内奸: " + entry.getKey());
                    }
                }
                break;
            default:
                break;
        }
        
        Bukkit.broadcastMessage(ChatColor.GOLD + "=================================");
        
        broadcastAllPlayerRoles();
    }

    public void broadcastAllPlayerRoles() {
        // 显示所有玩家身份
        Bukkit.broadcastMessage(ChatColor.YELLOW + "=== 所有玩家身份 ===");
        for (Map.Entry<String, Role> entry : playerRoles.entrySet()) {
            Bukkit.broadcastMessage(entry.getValue().getColor() + entry.getKey() + 
                ": " + entry.getValue().getDisplayName());
        }
    }
}