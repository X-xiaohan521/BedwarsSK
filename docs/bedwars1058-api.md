# BedWars1058 API Reference (offline)

This document is reconstructed from `bedwars-plugin-22.3.4.jar` using `javap` and contains the key public BedWars1058 API interfaces and classes relevant to shop, arena, and event handling.

## Main API entry point

### `com.andrei1058.bedwars.api.BedWars`
The main BedWars API interface exposed by the plugin.

Public methods:

```java
com.andrei1058.bedwars.api.BedWars$IStats getStatsUtil();
com.andrei1058.bedwars.api.BedWars$AFKUtil getAFKUtil();
com.andrei1058.bedwars.api.BedWars$ArenaUtil getArenaUtil();
com.andrei1058.bedwars.api.BedWars$Configs getConfigs();
com.andrei1058.bedwars.api.BedWars$ShopUtil getShopUtil();
com.andrei1058.bedwars.api.BedWars$TeamUpgradesUtil getTeamUpgradesUtil();
com.andrei1058.bedwars.api.levels.Level getLevelsUtil();
com.andrei1058.bedwars.api.party.Party getPartyUtil();
com.andrei1058.bedwars.api.server.ISetupSession getSetupSession(java.util.UUID);
boolean isInSetupSession(java.util.UUID);
com.andrei1058.bedwars.api.server.ServerType getServerType();
java.lang.String getLangIso(org.bukkit.entity.Player);
com.andrei1058.bedwars.api.command.ParentCommand getBedWarsCommand();
com.andrei1058.bedwars.api.server.RestoreAdapter getRestoreAdapter();
void setRestoreAdapter(com.andrei1058.bedwars.api.server.RestoreAdapter) throws java.lang.IllegalAccessError;
void setPartyAdapter(com.andrei1058.bedwars.api.party.Party);
com.andrei1058.bedwars.api.server.VersionSupport getVersionSupport();
com.andrei1058.bedwars.api.language.Language getDefaultLang();
java.lang.String getLobbyWorld();
java.lang.String getForCurrentVersion(java.lang.String, java.lang.String, java.lang.String);
void setLevelAdapter(com.andrei1058.bedwars.api.levels.Level);
boolean isAutoScale();
com.andrei1058.bedwars.api.language.Language getLanguageByIso(java.lang.String);
com.andrei1058.bedwars.api.language.Language getPlayerLanguage(org.bukkit.entity.Player);
java.io.File getAddonsPath();
com.andrei1058.bedwars.api.BedWars$ScoreboardUtil getScoreboardUtil();
boolean isShuttingDown();
```

## Utility classes

### `com.andrei1058.bedwars.api.BedWars$ShopUtil`
Used for currency and shop-related calculations.

```java
int calculateMoney(org.bukkit.entity.Player, org.bukkit.Material);
org.bukkit.Material getCurrency(java.lang.String);
org.bukkit.ChatColor getCurrencyColor(org.bukkit.Material);
java.lang.String getCurrencyMsgPath(com.andrei1058.bedwars.api.arena.shop.IContentTier);
java.lang.String getRomanNumber(int);
void takeMoney(org.bukkit.entity.Player, org.bukkit.Material, int);
```

### `com.andrei1058.bedwars.api.BedWars$ArenaUtil`
Used to query and manage arenas.

```java
boolean canAutoScale(java.lang.String);
void addToEnableQueue(com.andrei1058.bedwars.api.arena.IArena);
void removeFromEnableQueue(com.andrei1058.bedwars.api.arena.IArena);
boolean isPlaying(org.bukkit.entity.Player);
boolean isSpectating(org.bukkit.entity.Player);
void loadArena(java.lang.String, org.bukkit.entity.Player);
void setGamesBeforeRestart(int);
int getGamesBeforeRestart();
com.andrei1058.bedwars.api.arena.IArena getArenaByPlayer(org.bukkit.entity.Player);
void setArenaByPlayer(org.bukkit.entity.Player, com.andrei1058.bedwars.api.arena.IArena);
void removeArenaByPlayer(org.bukkit.entity.Player, com.andrei1058.bedwars.api.arena.IArena);
com.andrei1058.bedwars.api.arena.IArena getArenaByName(java.lang.String);
com.andrei1058.bedwars.api.arena.IArena getArenaByIdentifier(java.lang.String);
void setArenaByName(com.andrei1058.bedwars.api.arena.IArena);
void removeArenaByName(java.lang.String);
java.util.LinkedList<com.andrei1058.bedwars.api.arena.IArena> getArenas();
boolean vipJoin(org.bukkit.entity.Player);
int getPlayers(java.lang.String);
boolean joinRandomArena(org.bukkit.entity.Player);
boolean joinRandomFromGroup(org.bukkit.entity.Player, java.lang.String);
java.util.LinkedList<com.andrei1058.bedwars.api.arena.IArena> getEnableQueue();
void sendLobbyCommandItems(org.bukkit.entity.Player);
```

## Arena interface

### `com.andrei1058.bedwars.api.arena.IArena`
Represents a BedWars arena and provides game state, team, and player access.

```java
boolean isSpectator(org.bukkit.entity.Player);
boolean isSpectator(java.util.UUID);
boolean isReSpawning(java.util.UUID);
java.lang.String getArenaName();
void init(org.bukkit.World);
com.andrei1058.bedwars.api.configuration.ConfigManager getConfig();
boolean isPlayer(org.bukkit.entity.Player);
java.util.List<org.bukkit.entity.Player> getSpectators();
com.andrei1058.bedwars.api.arena.team.ITeam getTeam(org.bukkit.entity.Player);
com.andrei1058.bedwars.api.arena.team.ITeam getExTeam(java.util.UUID);
java.lang.String getDisplayName();
void setWorldName(java.lang.String);
com.andrei1058.bedwars.api.arena.GameState getStatus();
java.util.List<org.bukkit.entity.Player> getPlayers();
int getMaxPlayers();
java.lang.String getGroup();
int getMaxInTeam();
java.util.concurrent.ConcurrentHashMap<org.bukkit.entity.Player, java.lang.Integer> getRespawnSessions();
void updateSpectatorCollideRule(org.bukkit.entity.Player, boolean);
void updateNextEvent();
boolean addPlayer(org.bukkit.entity.Player, boolean);
boolean addSpectator(org.bukkit.entity.Player, boolean, org.bukkit.Location);
void removePlayer(org.bukkit.entity.Player, boolean);
void removeSpectator(org.bukkit.entity.Player, boolean);
boolean reJoin(org.bukkit.entity.Player);
void disable();
void restart();
org.bukkit.World getWorld();
java.lang.String getDisplayStatus(com.andrei1058.bedwars.api.language.Language);
java.lang.String getDisplayGroup(org.bukkit.entity.Player);
java.lang.String getDisplayGroup(com.andrei1058.bedwars.api.language.Language);
java.util.List<com.andrei1058.bedwars.api.arena.team.ITeam> getTeams();
void addPlacedBlock(org.bukkit.block.Block);
java.util.Map<java.util.UUID, java.lang.Long> getFireballCooldowns();
void removePlacedBlock(org.bukkit.block.Block);
boolean isBlockPlaced(org.bukkit.block.Block);
int getPlayerKills(org.bukkit.entity.Player, boolean);
int getPlayerBedsDestroyed(org.bukkit.entity.Player);
java.util.List<org.bukkit.block.Block> getSigns();
int getIslandRadius();
void setGroup(java.lang.String);
void setStatus(com.andrei1058.bedwars.api.arena.GameState);
void changeStatus(com.andrei1058.bedwars.api.arena.GameState);
public default boolean isRespawning(org.bukkit.entity.Player);
void addSign(org.bukkit.Location);
void refreshSigns();
void addPlayerKill(org.bukkit.entity.Player, boolean, org.bukkit.entity.Player);
void addPlayerBedDestroyed(org.bukkit.entity.Player);
com.andrei1058.bedwars.api.arena.team.ITeam getPlayerTeam(java.lang.String);
void checkWinner();
void addPlayerDeath(org.bukkit.entity.Player);
void setNextEvent(com.andrei1058.bedwars.api.arena.NextEvent);
com.andrei1058.bedwars.api.arena.NextEvent getNextEvent();
void sendPreGameCommandItems(org.bukkit.entity.Player);
void sendSpectatorCommandItems(org.bukkit.entity.Player);
com.andrei1058.bedwars.api.arena.team.ITeam getTeam(java.lang.String);
com.andrei1058.bedwars.api.tasks.StartingTask getStartingTask();
com.andrei1058.bedwars.api.tasks.PlayingTask getPlayingTask();
com.andrei1058.bedwars.api.tasks.RestartingTask getRestartingTask();
java.util.List<com.andrei1058.bedwars.api.arena.generator.IGenerator> getOreGenerators();
java.util.List<java.lang.String> getNextEvents();
int getPlayerDeaths(org.bukkit.entity.Player, boolean);
void sendDiamondsUpgradeMessages();
void sendEmeraldsUpgradeMessages();
java.util.LinkedList<org.bukkit.util.Vector> getPlaced();
void destroyData();
int getUpgradeDiamondsCount();
int getUpgradeEmeraldsCount();
java.util.List<com.andrei1058.bedwars.api.region.Region> getRegionsList();
java.util.concurrent.ConcurrentHashMap<org.bukkit.entity.Player, java.lang.Integer> getShowTime();
void setAllowSpectate(boolean);
boolean isAllowSpectate();
java.lang.String getWorldName();
int getRenderDistance();
boolean startReSpawnSession(org.bukkit.entity.Player, int);
boolean isReSpawning(org.bukkit.entity.Player);
org.bukkit.Location getReSpawnLocation();
org.bukkit.Location getSpectatorLocation();
org.bukkit.Location getWaitingLocation();
boolean isProtected(org.bukkit.Location);
void abandonGame(org.bukkit.entity.Player);
int getYKillHeight();
java.time.Instant getStartTime();
com.andrei1058.bedwars.api.arena.team.ITeamAssigner getTeamAssigner();
void setTeamAssigner(com.andrei1058.bedwars.api.arena.team.ITeamAssigner);
java.util.List<org.bukkit.entity.Player> getLeavingPlayers();
```

## Shop interfaces

### `com.andrei1058.bedwars.api.arena.shop.ICategoryContent`
Represents a category item in the BedWars shop.

```java
int getSlot();
org.bukkit.inventory.ItemStack getItemStack(org.bukkit.entity.Player);
boolean hasQuick(org.bukkit.entity.Player);
boolean isPermanent();
boolean isDowngradable();
java.lang.String getIdentifier();
java.util.List<com.andrei1058.bedwars.api.arena.shop.IContentTier> getContentTiers();
```

### `com.andrei1058.bedwars.api.arena.shop.IContentTier`
Represents a tier of purchase options within a shop category.

```java
int getPrice();
org.bukkit.Material getCurrency();
void setCurrency(org.bukkit.Material);
void setPrice(int);
void setItemStack(org.bukkit.inventory.ItemStack);
void setBuyItemsList(java.util.List<com.andrei1058.bedwars.api.arena.shop.IBuyItem>);
org.bukkit.inventory.ItemStack getItemStack();
int getValue();
java.util.List<com.andrei1058.bedwars.api.arena.shop.IBuyItem> getBuyItemsList();
```

### `com.andrei1058.bedwars.api.arena.shop.IBuyItem`
Represents an individual buyable item in a shop tier.

```java
boolean isLoaded();
void give(org.bukkit.entity.Player, com.andrei1058.bedwars.api.arena.IArena);
java.lang.String getUpgradeIdentifier();
org.bukkit.inventory.ItemStack getItemStack();
void setItemStack(org.bukkit.inventory.ItemStack);
boolean isAutoEquip();
void setAutoEquip(boolean);
boolean isPermanent();
void setPermanent(boolean);
```

## Team interface

### `com.andrei1058.bedwars.api.arena.team.ITeam`
Represents a team in an arena.

```java
com.andrei1058.bedwars.api.arena.team.TeamColor getColor();
java.lang.String getName();
java.lang.String getDisplayName(com.andrei1058.bedwars.api.language.Language);
boolean isMember(org.bukkit.entity.Player);
com.andrei1058.bedwars.api.arena.IArena getArena();
java.util.List<org.bukkit.entity.Player> getMembers();
void defaultSword(org.bukkit.entity.Player, boolean);
org.bukkit.Location getBed();
java.util.concurrent.ConcurrentHashMap<java.lang.String, java.lang.Integer> getTeamUpgradeTiers();
java.util.List<com.andrei1058.bedwars.api.arena.team.TeamEnchant> getBowsEnchantments();
java.util.List<com.andrei1058.bedwars.api.arena.team.TeamEnchant> getSwordsEnchantments();
java.util.List<com.andrei1058.bedwars.api.arena.team.TeamEnchant> getArmorsEnchantments();
int getSize();
void addPlayers(org.bukkit.entity.Player...);
void firstSpawn(org.bukkit.entity.Player);
void spawnNPCs();
void reJoin(org.bukkit.entity.Player);
void reJoin(org.bukkit.entity.Player, int);
void sendDefaultInventory(org.bukkit.entity.Player, boolean);
void respawnMember(org.bukkit.entity.Player);
void sendArmor(org.bukkit.entity.Player);
void addTeamEffect(org.bukkit.potion.PotionEffectType, int, int);
void addBaseEffect(org.bukkit.potion.PotionEffectType, int, int);
java.util.List<org.bukkit.potion.PotionEffect> getBaseEffects();
void addBowEnchantment(org.bukkit.enchantments.Enchantment, int);
void addSwordEnchantment(org.bukkit.enchantments.Enchantment, int);
void addArmorEnchantment(org.bukkit.enchantments.Enchantment, int);
boolean wasMember(java.util.UUID);
boolean isBedDestroyed();
org.bukkit.Location getSpawn();
org.bukkit.Location getShop();
org.bukkit.Location getTeamUpgrades();
void setBedDestroyed(boolean);
com.andrei1058.bedwars.api.arena.generator.IGenerator getIronGenerator();
com.andrei1058.bedwars.api.arena.generator.IGenerator getGoldGenerator();
com.andrei1058.bedwars.api.arena.generator.IGenerator getEmeraldGenerator();
void setEmeraldGenerator(com.andrei1058.bedwars.api.arena.generator.IGenerator);
java.util.List<com.andrei1058.bedwars.api.arena.generator.IGenerator> getGenerators();
int getDragons();
void setDragons(int);
java.util.List<org.bukkit.entity.Player> getMembersCache();
void destroyData();
void destroyBedHolo(org.bukkit.entity.Player);
java.util.LinkedList<com.andrei1058.bedwars.api.upgrades.EnemyBaseEnterTrap> getActiveTraps();
org.bukkit.util.Vector getKillDropsLocation();
void setKillDropsLocation(org.bukkit.util.Vector);
```

## Shop events

### `com.andrei1058.bedwars.api.events.shop.ShopOpenEvent`
Fired when a player opens the BedWars shop.

```java
public ShopOpenEvent(org.bukkit.entity.Player);
public ShopOpenEvent(org.bukkit.entity.Player, com.andrei1058.bedwars.api.arena.IArena);
com.andrei1058.bedwars.api.arena.IArena getArena();
org.bukkit.entity.Player getPlayer();
void setCancelled(boolean);
boolean isCancelled();
org.bukkit.event.HandlerList getHandlers();
static org.bukkit.event.HandlerList getHandlerList();
```

### `com.andrei1058.bedwars.api.events.shop.ShopBuyEvent`
Fired when a player buys an item from the BedWars shop.

```java
public ShopBuyEvent(org.bukkit.entity.Player, com.andrei1058.bedwars.api.arena.shop.ICategoryContent);
public ShopBuyEvent(org.bukkit.entity.Player, com.andrei1058.bedwars.api.arena.IArena, com.andrei1058.bedwars.api.arena.shop.ICategoryContent);
com.andrei1058.bedwars.api.arena.IArena getArena();
org.bukkit.entity.Player getBuyer();
com.andrei1058.bedwars.api.arena.shop.ICategoryContent getCategoryContent();
org.bukkit.event.HandlerList getHandlers();
static org.bukkit.event.HandlerList getHandlerList();
boolean isCancelled();
void setCancelled(boolean);
```

## Notes

- The API file was reconstructed from the compiled JAR `libs/bedwars-plugin-22.3.4.jar`.
- `ShopBuyEvent` is the main hook for intercepting shop purchases and customizing behavior.
- `ICategoryContent.getContentTiers()` returns available `IContentTier` purchase options.
- `IContentTier.getBuyItemsList()` returns one or more `IBuyItem` objects that can be granted by the shop.
- The `BedWars$ShopUtil` helper supports currency conversion, display formatting, and payment checks.
- For compatibility with BedWars normal mode vs identity mode, `ShopBuyEvent` can be used to cancel or replace purchases.
