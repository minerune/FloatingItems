package me.seetch.floatingitem;

import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerJoinEvent;
import me.seetch.floatingitem.item.FloatingItem;

public class EventListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        for (FloatingItem item : FloatingItemPlugin.get().getFloatingItems().values()) {
            item.spawn(event.getPlayer());
        }
    }
}
