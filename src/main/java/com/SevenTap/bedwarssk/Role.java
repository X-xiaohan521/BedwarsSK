package com.SevenTap.bedwarssk;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;

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

    public static List<Role> generateRoles(int playerCount) {
        List<Role> roles = new ArrayList<>();

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
}