package com.SevenTap.bedwarssk;

import com.SevenTap.bedwarssk.command.CommandHandler;
import com.SevenTap.bedwarssk.game.GameListener;
import com.SevenTap.bedwarssk.game.GameManager;
import com.SevenTap.bedwarssk.item.MagicWandManager;
import com.SevenTap.bedwarssk.listener.MagicWandListener;
import com.SevenTap.bedwarssk.message.MessageSender;
import com.SevenTap.bedwarssk.shop.AutoShopRegister;
import com.andrei1058.bedwars.BedWars;
import com.andrei1058.bedwars.api.language.Language;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class BedwarsSKPlugin extends JavaPlugin {

    private GameManager gameManager;
    private MessageSender messageSender;
    private CommandHandler commandHandler;
    private MagicWandManager magicWandManager;
    private AutoShopRegister shopRegister;

    @Override
    public void onEnable() {
        // 检查BedWars1058是否加载
        boolean is1058Loaded = Bukkit.getPluginManager().getPlugin("BedWars1058") != null;
        if (!is1058Loaded) {
            getLogger().warning("BedWars1058未找到！某些功能可能无法正常工作。");
        }

        saveDefaultConfig();

        // Dependency Injection
        gameManager = new GameManager();
        messageSender = new MessageSender(this, gameManager);
        magicWandManager = new MagicWandManager(this);
        commandHandler = new CommandHandler(gameManager, messageSender, magicWandManager);
        shopRegister = new AutoShopRegister(this, BedWars.shop, Language.getDefaultLanguage());

        // 自动配置 1058 商店
        if (is1058Loaded) {
            shopRegister.register();
        }

        // 注册事件监听器
        Bukkit.getPluginManager().registerEvents(new GameListener(this), this);
        Bukkit.getPluginManager().registerEvents(new MagicWandListener(this, gameManager, magicWandManager, messageSender), this);

        // 注册命令处理器
        this.getCommand("bwsk").setExecutor(commandHandler);

        getLogger().info("BedwarsSK插件已启用!");
        getLogger().info("作者: SevenTap & UniMilk");
        getLogger().info("版本: 1.0.0");
    }

    @Override
    public void onDisable() {
        getLogger().info("BedwarsSK插件已禁用!");
    }

    public GameManager getGameManager() {
        return gameManager;
    }

}