package com.SevenTap.bedwarssk;

import com.SevenTap.bedwarssk.command.CommandHandler;
import com.SevenTap.bedwarssk.game.GameListener;
import com.SevenTap.bedwarssk.game.GameManager;
import com.SevenTap.bedwarssk.message.MessageSender;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class BedwarsSKPlugin extends JavaPlugin {

    private GameManager gameManager;
    private MessageSender messageSender;
    private CommandHandler commandHandler;

    @Override
    public void onEnable() {
        // 检查BedWars1058是否加载
        if (Bukkit.getPluginManager().getPlugin("BedWars1058") == null) {
            getLogger().warning("BedWars1058未找到！某些功能可能无法正常工作。");
        }

        // Dependency Injection
        gameManager = new GameManager();
        messageSender = new MessageSender(gameManager);
        commandHandler = new CommandHandler(gameManager, messageSender);

        // 注册事件监听器
        Bukkit.getPluginManager().registerEvents(new GameListener(this), this);

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