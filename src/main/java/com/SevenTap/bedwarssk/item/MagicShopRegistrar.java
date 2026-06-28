package com.SevenTap.bedwarssk.item;

import com.SevenTap.bedwarssk.BedwarsSKPlugin;
import com.andrei1058.bedwars.BedWars;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import java.lang.reflect.Method;
import java.util.List;

public class MagicShopRegistrar {
    private static final int MAX_RETRIES = 8;

    private final BedwarsSKPlugin plugin;
    private final MagicWandManager wandManager;
    private boolean registered;

    public MagicShopRegistrar(BedwarsSKPlugin plugin, MagicWandManager wandManager) {
        this.plugin = plugin;
        this.wandManager = wandManager;
    }

    public void registerIfAvailable() {
        if (registered) {
            return;
        }

        if (Bukkit.getPluginManager().getPlugin("BedWars1058") == null) {
            plugin.getLogger().warning("BedWars1058 未加载，跳过自动注册魔法道具商店内容。");
            return;
        }

        queueRegistration(0);
    }

    private void queueRegistration(final int attempt) {
        if (registered || attempt >= MAX_RETRIES) {
            return;
        }

        Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
            @Override
            public void run() {
                try {
                    registerShopContents(attempt);
                } catch (Exception ex) {
                    plugin.getLogger().warning("自动注册魔法道具商店失败: " + ex.getMessage());
                }
            }
        }, 20L * (attempt + 1));
    }

    /**
     * 通过 BedWars1058 的内部商店管理器向默认商店中注入魔法道具分类与内容项。
     */
    private void registerShopContents(int attempt) throws Exception {
        if (registered) {
            return;
        }

        Object shopManager = BedWars.shop;
        if (shopManager == null) {
            plugin.getLogger().warning("BedWars1058 的商店管理器尚未准备好，稍后重试。");
            queueRegistration(attempt + 1);
            return;
        }

        if (!isShopIndexReady(shopManager)) {
            plugin.getLogger().warning("BedWars1058 的商店索引尚未就绪，稍后重试。");
            queueRegistration(attempt + 1);
            return;
        }

        registerCategory(shopManager);

        FileConfiguration config = plugin.getConfig();
        for (MagicWandType type : MagicWandType.values()) {
            String identifier = config.getString("wands." + type.getConfigKey() + ".1058-identifier", "magic-category.category-content." + type.getConfigKey());
            String materialName = type.getMaterial().name();
            String currencyName = getCurrencyName(type);
            int slot = getSlot(type);
            int cost = wandManager.getWandCost(type);

            registerContentTier(shopManager, identifier, slot, materialName, currencyName, cost);
        }

        if (!isCategoryRegistered(shopManager)) {
            plugin.getLogger().warning("BedWars1058 的商店索引未显示出魔法道具分类，继续重试。");
            queueRegistration(attempt + 1);
            return;
        }

        registered = true;
        plugin.getLogger().info("已自动向 BedWars1058 注册魔法道具商店内容。");
    }

    /**
     * 为魔法道具分类创建一个默认的商店分类入口。
     */
    private void registerCategory(Object shopManager) throws Exception {
        Method addDefaultShopCategory = shopManager.getClass().getDeclaredMethod(
                "addDefaultShopCategory",
                String.class,
                int.class,
                String.class,
                int.class,
                int.class,
                boolean.class
        );
        addDefaultShopCategory.setAccessible(true);
        addDefaultShopCategory.invoke(
                shopManager,
                "magic-category",
                wandManager.getCategorySlot(),
                wandManager.getCategoryIcon().name(),
                1,
                0,
                false
        );
    }

    /**
     * 将每一把魔法之杖作为一个可购买的商店内容项注册到分类下。
     */
    private void registerContentTier(Object shopManager, String contentIdentifier, int slot, String materialName, String currencyName, int cost) throws Exception {
        Method adCategoryContentTier = shopManager.getClass().getDeclaredMethod(
                "adCategoryContentTier",
                String.class,
                String.class,
                int.class,
                String.class,
                String.class,
                int.class,
                int.class,
                boolean.class,
                int.class,
                String.class,
                boolean.class,
                boolean.class
        );
        adCategoryContentTier.setAccessible(true);
        adCategoryContentTier.invoke(
                shopManager,
                "magic-category",
                contentIdentifier,
                slot,
                materialName,
                currencyName,
                cost,
                0,
                false,
                0,
                "",
                false,
                false
        );

        Method addBuyItem = shopManager.getClass().getDeclaredMethod(
                "addBuyItem",
                String.class,
                String.class,
                String.class,
                String.class,
                String.class,
                int.class,
                int.class,
                String.class,
                String.class,
                String.class,
                boolean.class
        );
        addBuyItem.setAccessible(true);
        addBuyItem.invoke(
                shopManager,
                "magic-category",
                contentIdentifier,
                materialName,
                "",
                "",
                cost,
                0,
                currencyName,
                "",
                "",
                false
        );
    }

    private boolean isShopIndexReady(Object shopManager) throws Exception {
        Object shopIndex = getShopIndex(shopManager);
        return shopIndex != null;
    }

    private boolean isCategoryRegistered(Object shopManager) throws Exception {
        Object shopIndex = getShopIndex(shopManager);
        if (shopIndex == null) {
            return false;
        }

        Method getCategoryList = shopIndex.getClass().getMethod("getCategoryList");
        List<?> categories = (List<?>) getCategoryList.invoke(shopIndex);
        if (categories == null) {
            return false;
        }

        for (Object category : categories) {
            Method getName = category.getClass().getMethod("getName");
            Object name = getName.invoke(category);
            if ("magic-category".equals(name)) {
                return true;
            }
        }
        return false;
    }

    private Object getShopIndex(Object shopManager) throws Exception {
        Method getShop = shopManager.getClass().getMethod("getShop");
        return getShop.invoke(null);
    }

    private int getSlot(MagicWandType type) {
        switch (type) {
            case LEVEL1:
                return 19;
            case LEVEL2:
                return 20;
            case LEVEL3:
                return 21;
            default:
                return 19;
        }
    }

    private String getCurrencyName(MagicWandType type) {
        switch (type) {
            case LEVEL1:
                return "iron";
            case LEVEL2:
                return "gold";
            case LEVEL3:
                return "emerald";
            default:
                return "iron";
        }
    }
}
