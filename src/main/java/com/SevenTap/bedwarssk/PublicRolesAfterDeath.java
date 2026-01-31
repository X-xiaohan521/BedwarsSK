package com.SevenTap.bedwarssk;

/**
 * 玩家淘汰后是否公开身份。
 */
public enum PublicRolesAfterDeath {
    FORCE_NOT_PUBLIC("强制不公开"),
    OPTIONAL("允许玩家选择"),
    FORCE_PUBLIC("强制公开");

    private final String display;

    PublicRolesAfterDeath(String display) {
        this.display = display;
    }

    public String getDisplay() {
        return display;
    }
}
