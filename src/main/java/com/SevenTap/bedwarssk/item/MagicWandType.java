package com.SevenTap.bedwarssk.item;

import org.bukkit.Material;

public enum MagicWandType {
    LEVEL1("level1", Material.STONE_HOE),
    LEVEL2("level2", Material.GOLD_HOE),
    LEVEL3("level3", Material.DIAMOND_HOE);

    private final String configKey;
    private final Material material;

    MagicWandType(String configKey, Material material) {
        this.configKey = configKey;
        this.material = material;
    }

    public String getConfigKey() {
        return configKey;
    }

    public Material getMaterial() {
        return material;
    }

    public static MagicWandType fromMaterial(Material material) {
        for (MagicWandType type : values()) {
            if (type.getMaterial() == material) {
                return type;
            }
        }
        return null;
    }

    public static MagicWandType fromConfigKey(String configKey) {
        for (MagicWandType type : values()) {
            if (type.getConfigKey().equalsIgnoreCase(configKey)) {
                return type;
            }
        }
        return null;
    }

    public static MagicWandType fromIdentifier(String identifier) {
        if (identifier == null) {
            return null;
        }
        if (identifier.endsWith("-wand")) {
            identifier = identifier.substring(0, identifier.length() - 5);
        }
        return fromConfigKey(identifier);
    }
}
