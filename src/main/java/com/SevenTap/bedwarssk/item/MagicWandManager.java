package com.SevenTap.bedwarssk.item;

import com.SevenTap.bedwarssk.BedwarsSKPlugin;
import com.SevenTap.bedwarssk.util.ColorUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class MagicWandManager {
    private static final String IDENTIFIER_KEY = "[BedwarsSK 魔法道具]";
    private static final String MARKER_KEY = "/bwsk-magicwand";

    private final FileConfiguration config;
    private final double cooldownSeconds;
    private final String categoryName;
    private final List<String> categoryLore;

    public MagicWandManager(BedwarsSKPlugin plugin) {
        this.config = plugin.getConfig();
        this.cooldownSeconds = config.getDouble("cooldown-seconds", 30.0);
        this.categoryName = ColorUtil.translateColors(config.getString("shop-category.name", "&c&l魔法道具"));
        this.categoryLore = ColorUtil.translateColorsList(config.getStringList("shop-category.lore"));
    }

    public double getCooldownSeconds() {
        return cooldownSeconds;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public List<String> getCategoryLore() {
        return new ArrayList<>(categoryLore);
    }

    public ItemStack createWand(MagicWandType type) {
        String path = "wands." + type.getConfigKey() + ".";
        String displayName = config.getString(path + "display-name", "&a&l魔法之杖");
        List<String> lore = new ArrayList<>();
        lore.addAll(config.getStringList(path + "lore.identifier"));
        lore.addAll(config.getStringList(path + "lore.marker"));
        List<String> formattedLore = ColorUtil.translateColorsList(lore);

        ItemStack item = new ItemStack(type.getMaterial());
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ColorUtil.translateColors(displayName));
            meta.setLore(formattedLore);
            meta.addEnchant(Enchantment.DURABILITY, 1, true);
            item.setItemMeta(meta);
        }
        return item;
    }

    public boolean isMagicWand(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) {
            return false;
        }
        MagicWandType type = MagicWandType.fromMaterial(item.getType());
        if (type == null) {
            return false;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return false;
        }

        String expectedName = getWandDisplayName(type);
        String rawConfigName = config.getString("wands." + type.getConfigKey() + ".display-name", expectedName);
        if (meta.hasDisplayName()) {
            String displayName = meta.getDisplayName();
            if (expectedName.equals(displayName) || rawConfigName.equals(displayName)) {
                return true;
            }
            if (stripColor(expectedName).equals(stripColor(displayName))) {
                return true;
            }
        }

        if (!meta.hasLore()) {
            return false;
        }
        List<String> lore = meta.getLore();
        if (lore == null || lore.isEmpty()) {
            return false;
        }

        String identifier = config.getString("wands." + type.getConfigKey() + ".lore.identifier.0", IDENTIFIER_KEY);
        String marker = config.getString("wands." + type.getConfigKey() + ".lore.marker.0", MARKER_KEY);
        String expectedIdentifier = ColorUtil.translateColors(identifier);
        String expectedMarker = ColorUtil.translateColors(marker);
        boolean hasIdentifier = false;
        boolean hasMarker = false;
        for (String line : lore) {
            if (line == null) {
                continue;
            }
            if (line.equals(expectedIdentifier) || line.equals(identifier) || stripColor(line).equals(stripColor(expectedIdentifier))) {
                hasIdentifier = true;
            }
            if (line.equals(expectedMarker) || line.equals(marker) || stripColor(line).equals(stripColor(expectedMarker))) {
                hasMarker = true;
            }
        }
        return hasIdentifier && hasMarker;
    }

    private String stripColor(String input) {
        return ChatColor.stripColor(input == null ? "" : input);
    }

    public MagicWandType getMagicWandType(ItemStack item) {
        if (item == null) {
            return null;
        }
        return MagicWandType.fromMaterial(item.getType());
    }

    public String getSoundName(String path, String defaultName) {
        return config.getString("messages.sounds." + path, defaultName);
    }

    public int getWandCost(MagicWandType type) {
        return config.getInt("wands." + type.getConfigKey() + ".cost", 0);
    }

    public String getWandDisplayName(MagicWandType type) {
        return ColorUtil.translateColors(config.getString("wands." + type.getConfigKey() + ".display-name", "&a&l魔法之杖"));
    }

    public int getCategorySlot() {
        return config.getInt("shop-category.slot", 8);
    }

    public Material getCategoryIcon() {
        return Material.matchMaterial(config.getString("shop-category.icon", "REDSTONE_TORCH_ON"));
    }
}
