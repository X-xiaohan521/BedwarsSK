package com.SevenTap.bedwarssk.shop;

import com.SevenTap.bedwarssk.BedwarsSKPlugin;
import com.andrei1058.bedwars.api.language.Language;
import com.andrei1058.bedwars.shop.ShopManager;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.lang.reflect.Method;

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
            try {
                Method loadShop = shopManager.getClass().getDeclaredMethod("loadShop");
                loadShop.setAccessible(true);
                loadShop.invoke(shopManager);
                plugin.getLogger().info("Bedwars1058 商店已重新加载。");
            } catch (Exception e) {
                plugin.getLogger().severe("重新加载商店失败，请手动重启服务器以使商店栏目生效。\n" + e);
            }
        }
    }

    private boolean registerShopCategory() {
        YamlConfiguration shopConfig = shopManager.getYml();
        if (shopConfig.contains("magic-category")) {
            plugin.getLogger().info("魔法道具栏已经在 Bedwars1058 商店中注册，跳过自动注册。");
            return false;
        }
        Object magicCategory = config.get("shop.item.magic-category");
        shopConfig.set("magic-category", magicCategory);
        plugin.getLogger().info("魔法道具栏注册成功。");
        return true;
    }

    private boolean registerDefaultLocale() {
        YamlConfiguration localeConfig = defaultLocale.getYml();
        if (localeConfig.contains("shop-items-messages.magic-category")) {
            plugin.getLogger().info("魔法道具本地化已经在 Bedwars1058 本地化文件中注册，跳过自动注册。");
            return false;
        }
        Object magicLocale = config.get("shop.message.magic-category");
        localeConfig.set("shop-items-messages.magic-category", magicLocale);
        plugin.getLogger().info("魔法道具本地化注册成功。");
        return true;
    }
}
