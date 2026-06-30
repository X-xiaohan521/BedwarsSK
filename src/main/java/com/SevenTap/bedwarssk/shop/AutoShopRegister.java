package com.SevenTap.bedwarssk.shop;

import com.SevenTap.bedwarssk.BedwarsSKPlugin;
import com.andrei1058.bedwars.api.language.Language;
import com.andrei1058.bedwars.shop.ShopManager;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class AutoShopRegister {
    private final BedwarsSKPlugin plugin;
    private final FileConfiguration config;
    private final ShopManager shopManager;
    private final Language defaultLocale;

    public AutoShopRegister(BedwarsSKPlugin plugin, ShopManager shopManager, Language defaultLocale) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
        this.shopManager = shopManager;
        this.defaultLocale = defaultLocale;
    }

    public void register() {
        plugin.getLogger().info("尝试在 Bedwars1058 商店中注册魔法道具...");
        registerShopCategory();
        registerDefaultLocale();
    }

    private void registerShopCategory() {
        YamlConfiguration shopConfig = shopManager.getYml();
        if (shopConfig.contains("magic-category")) {
            plugin.getLogger().info("魔法道具栏已经在 Bedwars1058 商店中注册，跳过自动注册。");
            return;
        }
        Object magicCategory = config.get("shop.item.magic-category");
        shopConfig.set("magic-category", magicCategory);
        plugin.getLogger().info("魔法道具栏注册成功。");
    }

    private void registerDefaultLocale() {
        YamlConfiguration localeConfig = defaultLocale.getYml();
        if (localeConfig.contains("shop-items-messages.magic-category")) {
            plugin.getLogger().info("魔法道具本地化已经在 Bedwars1058 本地化文件中注册，跳过自动注册。");
            return;
        }
        Object magicLocale = config.get("shop.message.magic-category");
        localeConfig.set("shop-items-messages.magic-category", magicLocale);
        plugin.getLogger().info("魔法道具本地化注册成功。");
    }
}
