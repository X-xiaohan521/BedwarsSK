package com.SevenTap.bedwarssk.shop;

import com.SevenTap.bedwarssk.BedwarsSKPlugin;
import com.andrei1058.bedwars.api.language.Language;
import com.andrei1058.bedwars.shop.ShopManager;
import org.bukkit.Bukkit;
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
        boolean isScRegistered = registerShopCategory();
        boolean isLocaleRegistered = registerDefaultLocale();
        if (isScRegistered || isLocaleRegistered) {
            plugin.getLogger().warning("魔法道具注册成功，等待服务器启动完毕后自动重启...");
            Bukkit.getScheduler().runTaskLater(plugin, Bukkit.getServer()::reload, 3 * 20L);   // 等待服务器加载完成后，再延迟 3 秒重启
        }
    }

    private boolean registerShopCategory() {
        YamlConfiguration shopConfig = shopManager.getYml();
        if (shopConfig.contains("magic-category")) {
            plugin.getLogger().info("魔法道具栏已经在 Bedwars1058 商店中注册，跳过自动注册。");
            return false;
        }
        Object magicCategory = config.get("shop.item.magic-category");
        shopManager.set("magic-category", magicCategory);
        return true;
    }

    private boolean registerDefaultLocale() {
        YamlConfiguration localeConfig = defaultLocale.getYml();
        if (localeConfig.contains("shop-items-messages.magic-category")) {
            plugin.getLogger().info("魔法道具本地化已经在 Bedwars1058 本地化文件中注册，跳过自动注册。");
            return false;
        }
        Object magicLocale = config.get("shop.message.magic-category");
        defaultLocale.set("shop-items-messages.magic-category", magicLocale);
        return true;
    }
}
