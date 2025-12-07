package com.SevenTap.bedwarssk;

public enum PlayerStatus {
    ALIVE("存活"),
    BED_BROKEN("床被破坏"),
    FINAL_DEAD("最终死亡");

    private final String display;

    PlayerStatus(String display) {
        this.display = display;
    }

    public String getDisplay() {
        return display;
    }
}