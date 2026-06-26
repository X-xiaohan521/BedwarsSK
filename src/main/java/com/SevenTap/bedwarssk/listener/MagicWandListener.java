package com.SevenTap.bedwarssk.listener;

import com.SevenTap.bedwarssk.BedwarsSKPlugin;
import com.SevenTap.bedwarssk.Role;
import com.SevenTap.bedwarssk.game.GameManager;
import com.SevenTap.bedwarssk.item.MagicWandManager;
import com.SevenTap.bedwarssk.item.MagicWandType;
import com.SevenTap.bedwarssk.message.MessageSender;
import com.andrei1058.bedwars.api.arena.IArena;
import com.andrei1058.bedwars.api.arena.shop.ICategoryContent;
import com.andrei1058.bedwars.api.events.shop.ShopBuyEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.*;

public class MagicWandListener implements Listener {
    private final BedwarsSKPlugin plugin;
    private final GameManager gameManager;
    private final MagicWandManager wandManager;
    private final MessageSender messageSender;
    private final Map<String, Long> cooldownMap = new HashMap<>();

    public MagicWandListener(BedwarsSKPlugin plugin, GameManager gameManager, MagicWandManager wandManager, MessageSender messageSender) {
        this.plugin = plugin;
        this.gameManager = gameManager;
        this.wandManager = wandManager;
        this.messageSender = messageSender;
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        // 这个方法重点测试攻击玩家
        if (!(event.getDamager() instanceof Player)) {
            return;
        }
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player attacker = (Player) event.getDamager();
        Player victim = (Player) event.getEntity();
        ItemStack handItem = attacker.getItemInHand();

        if (!wandManager.isMagicWand(handItem)) {
            return;
        }

        if (!gameManager.isGameStarted()) {
            attacker.sendMessage(ChatColor.RED + "当前游戏未开始，魔法道具无法生效。" );
            return;
        }

        Role attackerRole = gameManager.getPlayerRole(attacker);
        Role victimRole = gameManager.getPlayerRole(victim);
        if (attackerRole == null || victimRole == null) {
            attacker.sendMessage(messageSender.getMessage("invalid.no-role"));
            return;
        }

        IArena arena = gameManager.getArena();
        if (arena == null) {
            attacker.sendMessage(ChatColor.RED + "当前场地信息不可用，魔法道具无法生效。" );
            return;
        }

        if (arena.getTeam(attacker) == null || arena.getTeam(victim) == null) {
            attacker.sendMessage(ChatColor.RED + "目标不是有效的参战玩家，魔法道具无法生效。" );
            return;
        }

        if (arena.getTeam(attacker).equals(arena.getTeam(victim))) {
            attacker.sendMessage(messageSender.getMessage("invalid.same-team"));
            return;
        }

        // 处理冷却时间逻辑
        String pairKey = attacker.getUniqueId().toString() + "_" + victim.getUniqueId().toString();
        long now = System.currentTimeMillis();
        long cooldownMillis = (long) (wandManager.getCooldownSeconds() * 1000);
        Long lastUse = cooldownMap.get(pairKey);
        if (lastUse != null && now - lastUse < cooldownMillis) {
            double remainSeconds = (cooldownMillis - (now - lastUse)) / 1000.0;
            attacker.sendMessage(messageSender.getMessage("cooldown.attacker", "seconds", String.format(Locale.US, "%.1f", remainSeconds)));
            playSound(attacker, wandManager.getSoundName("cooldown-failed", "ENDERMAN_TELEPORT"));
            return;
        }

        MagicWandType wandType = wandManager.getMagicWandType(handItem);
        if (wandType == null) {
            return;
        }

        executeWandEffect(attacker, victim, attackerRole, victimRole, wandType);
        cooldownMap.put(pairKey, now);
        consumeItem(attacker, handItem);
    }

    @EventHandler
    public void onShopBuy(ShopBuyEvent event) {
        ICategoryContent content = event.getCategoryContent();
        if (content == null) {
            return;
        }

        String identifier = content.getIdentifier();
        MagicWandType wandType = MagicWandType.fromIdentifier(identifier);
        if (wandType == null) {
            return;
        }

        if (!gameManager.isGameStarted() || !gameManager.isIdentityGame()) {
            event.setCancelled(true);   // 这里这个接口调用有可能有问题，导致购买事件没能被拦截，玩家还是买到了锄头
            Player buyer = event.getBuyer();
            if (buyer != null) {
                buyer.sendMessage(ChatColor.RED + "当前不是身份起床对局，魔法道具不可购买。" );
            }
            return;
        }

        Player buyer = event.getBuyer();
        if (buyer != null) {
            // 不能直接从购买事件中拦截物品，返还魔法物品吗？为什么要购买之后扫描背包，感觉性能有点差，鲁棒性也不好
            Bukkit.getScheduler().runTask(plugin, () -> replacePurchasedWand(buyer, wandType));
        }
    }

    private void replacePurchasedWand(Player player, MagicWandType type) {
        if (player == null || type == null) {
            return;
        }

        // 扫描背包替换
        PlayerInventory inventory = player.getInventory();
        for (int slot = 0; slot < inventory.getSize(); slot++) {
            ItemStack item = inventory.getItem(slot);
            if (item == null || item.getType() != type.getMaterial()) {
                continue;
            }
            if (wandManager.isMagicWand(item)) {
                continue;
            }

            if (item.getAmount() > 1) {
                item.setAmount(item.getAmount() - 1);   // 这是何意味？为什么是只扣除一个？变相允许了玩家拥有原始锄头？
                inventory.setItem(slot, item);
                addOrDrop(player, wandManager.createWand(type));
            } else {
                inventory.setItem(slot, wandManager.createWand(type));
            }
            return;
        }

        // 扫描手中物品替换
        ItemStack handItem = player.getItemInHand();
        if (handItem != null && handItem.getType() == type.getMaterial() && !wandManager.isMagicWand(handItem)) {   // 这里最后一个判断条件是不是写反了？
            if (handItem.getAmount() > 1) {
                handItem.setAmount(handItem.getAmount() - 1);
                player.setItemInHand(handItem);
                addOrDrop(player, wandManager.createWand(type));
            } else {
                player.setItemInHand(wandManager.createWand(type));
            }
        }
    }

    private void addOrDrop(Player player, ItemStack item) {
        Map<Integer, ItemStack> overflow = player.getInventory().addItem(item);
        if (!overflow.isEmpty()) {
            for (ItemStack overflowItem : overflow.values()) {
                player.getWorld().dropItemNaturally(player.getLocation(), overflowItem);
            }
        }
    }

    private void executeWandEffect(Player attacker, Player victim, Role attackerRole, Role victimRole, MagicWandType wandType) {
        switch (wandType) {
            case LEVEL1:
                Role roleToBeExcluded = generateLevel1Role(attackerRole, victimRole);
                attacker.sendMessage(messageSender.getMessage(
                        "use.level1.attacker",
                        "victim", victim.getName(),
                        "role", roleToBeExcluded.getDisplayName())
                );
                victim.sendMessage(messageSender.getMessage(
                        "use.level1.victim",
                        "attacker", attacker.getName())
                );
                playSound(attacker, wandManager.getSoundName("use.level1.attacker-sound", "ORB_PICKUP"));
                playSound(victim, wandManager.getSoundName("use.level1.victim-sound", "CLICK"));
                break;
            case LEVEL2:
                List<Role> rolesToBeExcluded = generateLevel2Roles(victimRole);
                attacker.sendMessage(messageSender.getMessage(
                        "use.level2.attacker",
                        "victim", victim.getName(),
                        "role1", rolesToBeExcluded.get(0).getDisplayName(),
                        "role2", rolesToBeExcluded.get(1).getDisplayName())
                );
                victim.sendMessage(messageSender.getMessage(
                        "use.level2.victim",
                        "attacker", attacker.getName())
                );
                playSound(attacker, wandManager.getSoundName("use.level2.attacker-sound", "LEVEL_UP"));
                playSound(victim, wandManager.getSoundName("use.level2.victim-sound", "CLICK"));
                break;
            case LEVEL3:
                attacker.sendMessage(messageSender.getMessage(
                        "use.level3.attacker",
                        "victim", victim.getName(),
                        "role", victimRole.getDisplayName())
                );
                victim.sendMessage(messageSender.getMessage(
                        "use.level3.victim",
                        "attacker", attacker.getName())
                );
                playSound(attacker, wandManager.getSoundName("use.level3.attacker-sound", "ANVIL_LAND"));
                playSound(victim, wandManager.getSoundName("use.level3.victim-sound", "CLICK"));
                break;
            default:
                break;
        }
    }

    /**
     * 生成一级魔杖排除的身份。从总共4种身份中，排除魔杖使用者与受击者的身份，随机获取剩余两种或三种（当两者身份相同时）身份中的一种。
     * @param attackerRole 魔杖使用者的身份
     * @param victimRole 受击者的身份
     * @return 魔杖帮助其使用者随机排除的受击者身份
     */
    private Role generateLevel1Role(Role attackerRole, Role victimRole) {
        List<Role> roles = new ArrayList<>(Arrays.asList(Role.values()));
        roles.remove(attackerRole);
        roles.remove(victimRole);
        Collections.shuffle(roles);
        return roles.get(0);
    }

    /**
     * 生成二级魔杖排除的身份。从总共4种身份中，排除被攻击者的身份，随机获取剩余三种身份中的两种。
     * @param victimRole 受击者的身份
     * @return 魔杖帮助其使用者随机排除的受击者身份列表
     */
    private List<Role> generateLevel2Roles(Role victimRole) {
        List<Role> roles = new ArrayList<>(Arrays.asList(Role.values()));
        roles.remove(victimRole);
        Collections.shuffle(roles);
        return roles.subList(0, 2);
    }

    private void consumeItem(Player attacker, ItemStack item) {
        if (item.getAmount() > 1) {
            item.setAmount(item.getAmount() - 1);   // 还是这个逻辑，魔杖允许堆叠吗？
        } else {
            attacker.setItemInHand(null);
        }
    }

    private void playSound(Player player, String soundName) {
        if (soundName == null || soundName.isEmpty()) {
            return;
        }
        try {
            Sound sound = Sound.valueOf(soundName);
            player.playSound(player.getLocation(), sound, 1.0f, 1.0f);
        } catch (IllegalArgumentException ignored) {
            // 无效音效名时不播放
        }
    }
}
