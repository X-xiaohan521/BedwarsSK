package com.SevenTap.bedwarssk.item;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

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

    public static MagicWandType from1058Identifier(String identifier, FileConfiguration config) {
        if (identifier == null) {
            return null;
        }
        for (MagicWandType type : values()) {
            String id58 = config.getString("wands." + type.configKey + ".1058-identifier");
            if (id58.equals(identifier)) {
                return type;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "MagicWandType{" +
                "configKey='" + configKey + '\'' +
                ", material=" + material +
                '}';
    }
}
