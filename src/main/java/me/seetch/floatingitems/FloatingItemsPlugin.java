package me.seetch.floatingitems;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.item.Item;
import cn.nukkit.level.Location;
import cn.nukkit.permission.Permission;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.ConfigSection;
import me.seetch.floatingitems.command.FloatingItemCommand;
import me.seetch.floatingitems.data.FloatingItem;
import me.seetch.floatingitems.listener.EventListener;
import me.seetch.floatingitems.util.ItemSerializer;
import me.seetch.floatingitems.util.LocationSerializer;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class FloatingItemsPlugin extends PluginBase {

    private final Object floatingItemLock = new Object();

    protected static final AtomicInteger count = new AtomicInteger(0);
    protected static Map<String, FloatingItem> floatingItems = new HashMap<>();

    private File path;

    @Override
    public void onEnable() {
        path = new File(getDataFolder(), "floatingitems.yml");

        getServer().getCommandMap().register(getName(), new FloatingItemCommand());
        getServer().getPluginManager().addPermission(new Permission("floatingitem.use", "Main floating item permission"));

        this.getServer().getPluginManager().registerEvents(new EventListener(), this);

        reloadFloatingItems();
    }

    @Override
    public void onDisable() {
        saveFloatingItems(false);
    }

    public static Map<String, FloatingItem> getFloatingItems() {
        return floatingItems;
    }

    private void saveFloatingItems(boolean async) {
        Config config = new Config(path, Config.YAML);

        ConfigSection configSection = new ConfigSection();
        synchronized (floatingItemLock) {
            for (FloatingItem floatingItem : floatingItems.values()) {
                ConfigSection hl = new ConfigSection();

                hl.set("item", ItemSerializer.serialize(floatingItem.getItem()));
                hl.set("location", LocationSerializer.serialize(floatingItem.getLocation()));

                configSection.set(floatingItem.getFloatingItemId(), hl);
            }
        }

        config.set("list", configSection);

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

                count.set(Integer.parseInt(id));

                String item = section.getString("item");
                String location = section.getString("location");

                map.put(id, FloatingItem.create(id, LocationSerializer.deserialize(location), ItemSerializer.deserialize(item)));
            }
        }

        synchronized (floatingItemLock) {
            floatingItems = map;
        }
    }

    public static String getNextId() {
        return String.valueOf(count.incrementAndGet());
    }

    public static FloatingItem create(Location location, Item item) {
        FloatingItem floatingItem = FloatingItem.create(getNextId(), location, item);
        floatingItem.spawnToAll();
        return floatingItem;
    }

    public static FloatingItem create(Location location, Item item, Player player) {
        FloatingItem floatingItem = FloatingItem.create(getNextId(), location, item);
        floatingItem.spawnTo(player);
        return floatingItem;
    }

    public static FloatingItem createAndSave(Location location, Item item) {
        FloatingItem floatingItem = FloatingItem.create(getNextId(), location, item);
        floatingItem.spawnToAll();
        floatingItems.put(floatingItem.getFloatingItemId(), floatingItem);
        return floatingItem;
    }

    public static FloatingItem createAndSave(Location location, Item item, Player player) {
        FloatingItem floatingItem = FloatingItem.create(getNextId(), location, item);
        floatingItem.spawnTo(player);
        floatingItems.put(floatingItem.getFloatingItemId(), floatingItem);
        return floatingItem;
    }

    public static void delete(String id) {
        getFloatingItem(id).close();
        floatingItems.remove(id);
    }

    public static FloatingItem findNearEntity(Player center) {
        double distance = Long.MAX_VALUE;
        Entity near = null;

        for (Entity find : center.getLevel().getEntities()) {
            if (find instanceof FloatingItem) {
                double dist = center.distanceSquared(find);

                if (near == null || dist < distance) {
                    distance = dist;
                    near = find;
                }
            }
        }

        return (FloatingItem) near;
    }

    public static FloatingItem getFloatingItem(String id) {
        return floatingItems.get(id);
    }
}
