package me.seetch.floatingitem;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.level.Position;
import cn.nukkit.permission.Permission;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.ConfigSection;
import lombok.Getter;
import me.seetch.floatingitem.command.FloatingItemCommand;
import me.seetch.floatingitem.item.FloatingItem;
import me.seetch.floatingitem.util.ItemSerializer;
import me.seetch.floatingitem.util.PositionSerializer;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class FloatingItemPlugin extends PluginBase {

    private final Object floatingItemLock = new Object();

    @Getter
    private Map<String, FloatingItem> floatingItems = new HashMap<>();
    private static final AtomicLong id = new AtomicLong();

    private static FloatingItemPlugin instance;

    private File path;

    @Override
    public void onEnable() {
        instance = this;

        saveResource("floating-items.yml");
        path = new File(getDataFolder(), "floating-items.yml");

        getServer().getCommandMap().register("FloatingItem", new FloatingItemCommand());
        getServer().getPluginManager().addPermission(new Permission("floating-item.use", "Main floating item permission"));

        this.getServer().getPluginManager().registerEvents(new EventListener(), this);

        getServer().getScheduler().scheduleDelayedRepeatingTask(
                this,
                () -> saveFloatingItems(true),
                5 * 60 * 20,
                5 * 60 * 20
        );

        // Respawn floating items every 4 minutes.
        this.getServer().getScheduler().scheduleRepeatingTask(this, () -> {
            for (FloatingItem floatingItem : getFloatingItems().values()) {
                floatingItem.delete();
                floatingItem.spawnForAll();
            }
        }, 20 * 60 * 4);

        reloadFloatingItems();
    }

    @Override
    public void onDisable() {
        saveFloatingItems(false);
    }

    private void saveFloatingItems(boolean async) {
        Config config = new Config(path, Config.YAML);

        ConfigSection floatingItems = new ConfigSection();
        synchronized (floatingItemLock) {
            for (FloatingItem floatingItem : getFloatingItems().values()) {
                ConfigSection hl = new ConfigSection();

                hl.set("item", ItemSerializer.serialize(floatingItem.getItem()));
                hl.set("position", PositionSerializer.serialize(floatingItem.getPosition()));

                floatingItems.set(floatingItem.getId(), hl);
            }
        }

        config.set("list", floatingItems);

        config.save(async);
    }

    @SuppressWarnings("unchecked")
    public void reloadFloatingItems() {
        Config config = new Config(path, Config.YAML);

        Map<String, FloatingItem> map = new HashMap<>();
        ConfigSection list = config.getSection("list");

        if (list != null && !list.isEmpty()) {
            Map<String, ConfigSection> sections = (Map) list.getAllMap();
            for (Map.Entry<String, ConfigSection> entry : sections.entrySet()) {
                String id = entry.getKey();
                ConfigSection section = entry.getValue();

                String item = section.getString("item");
                String position = section.getString("position");

                FloatingItem hologram = new FloatingItem(id, ItemSerializer.deserialize(item), PositionSerializer.deserialize(position));

                map.put(id, hologram);
            }
        }

        synchronized (floatingItemLock) {
            this.floatingItems = map;
        }
    }

    public static FloatingItemPlugin get() {
        return instance;
    }

    public FloatingItem spawn(Item item, Position position, Player player) {
        if (item.getId() == 0) {
            player.sendMessage("§cYou can't spawn floating air!");
            return null;
        }
        FloatingItem floatingItem = new FloatingItem(nextId(), item, position);
        floatingItems.put(floatingItem.getId(), floatingItem.spawn(player));
        return floatingItem;
    }

    public FloatingItem spawnForAll(Item item, Position position) {
        FloatingItem floatingItem = new FloatingItem(nextId(), item, position);
        floatingItems.put(floatingItem.getId(), floatingItem.spawnForAll());
        return floatingItem;
    }

    public void update(FloatingItem floatingItem) {
        floatingItem.update();
        floatingItems.remove(floatingItem.getId());
        floatingItems.put(floatingItem.getId(), floatingItem);
    }

    public void delete(String id) {
        FloatingItem floatingItem = search(id);
        if (floatingItem != null) {
            floatingItem.delete();
            floatingItems.remove(floatingItem.getId());
        }
    }

    public FloatingItem search(String id) {
        for (FloatingItem floatingItem : getFloatingItems().values()) {
            if (floatingItem.getId().contains(id)) {
                return floatingItem;
            }
        }
        return null;
    }

    public static String nextId() {
        return String.valueOf(id.getAndIncrement());
    }
}
