package com.SevenTap.bedwarssk;

import org.bukkit.ChatColor;

/**
 * 玩家淘汰后是否公开身份。
 */
public enum PublicRolesAfterDeath {
    FORCE_NOT_PUBLIC(ChatColor.RED + "强制不公开" ),
    OPTIONAL(ChatColor.BLUE + "允许玩家选择"),
    FORCE_PUBLIC(ChatColor.GREEN + "强制公开");

    private final String display;

    PublicRolesAfterDeath(String display) {
        this.display = display;
    }

    public String getDisplay() {
        return display;
    }
}
