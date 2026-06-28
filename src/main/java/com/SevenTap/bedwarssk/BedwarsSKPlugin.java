package com.SevenTap.bedwarssk;

import com.SevenTap.bedwarssk.command.CommandHandler;
import com.SevenTap.bedwarssk.game.GameListener;
import com.SevenTap.bedwarssk.game.GameManager;
import com.SevenTap.bedwarssk.item.MagicShopRegistrar;
import com.SevenTap.bedwarssk.item.MagicWandManager;
import com.SevenTap.bedwarssk.listener.MagicWandListener;
import com.SevenTap.bedwarssk.message.MessageSender;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class BedwarsSKPlugin extends JavaPlugin {

    private GameManager gameManager;
    private MessageSender messageSender;
    private CommandHandler commandHandler;
    private MagicWandManager magicWandManager;
    private MagicShopRegistrar magicShopRegistrar;

    @Override
    public void onEnable() {
        // 检查BedWars1058是否加载
        if (Bukkit.getPluginManager().getPlugin("BedWars1058") == null) {
            getLogger().warning("BedWars1058未找到！某些功能可能无法正常工作。");
        }

        saveDefaultConfig();

        // Dependency Injection
        gameManager = new GameManager();
        messageSender = new MessageSender(this, gameManager);
        magicWandManager = new MagicWandManager(this);
        magicShopRegistrar = new MagicShopRegistrar(this, magicWandManager);
        commandHandler = new CommandHandler(gameManager, messageSender, magicWandManager);

        // 注册事件监听器
        Bukkit.getPluginManager().registerEvents(new GameListener(this), this);
        Bukkit.getPluginManager().registerEvents(new MagicWandListener(this, gameManager, magicWandManager, messageSender, magicShopRegistrar), this);

        // 注册命令处理器
        this.getCommand("bwsk").setExecutor(commandHandler);

        // 自动向 BedWars1058 注册商店内容，避免服主手工维护 shop.yml
        magicShopRegistrar.registerIfAvailable();

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