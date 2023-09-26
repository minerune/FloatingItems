package me.seetch.floatingitems.listener;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerFormRespondedEvent;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.form.window.FormWindowCustom;
import cn.nukkit.level.Position;
import me.seetch.floatingitems.FloatingItemsPlugin;
import me.seetch.floatingitems.data.FloatingItem;
import me.seetch.floatingitems.util.StringUtil;

public class EventListener implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        for (FloatingItem item : FloatingItemsPlugin.get().getFloatingItems().values()) {
            item.spawn(event.getPlayer());
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onFormResponse(PlayerFormRespondedEvent event) {
        if (event.getWindow() instanceof FormWindowCustom) {
            Player p = event.getPlayer();
            if (event.getResponse() == null || event.getWindow().wasClosed()) {
                return;
            }
            FormWindowCustom formWindowCustom = (FormWindowCustom) event.getWindow();
            if (formWindowCustom.getTitle().equals("Edit Floating Item")) {
                String id = formWindowCustom.getResponse().getLabelResponse(0);
                String[] rawItem = formWindowCustom.getResponse().getInputResponse(1).split(":");
                float x = StringUtil.getFloat(formWindowCustom.getResponse().getInputResponse(2));
                float y = StringUtil.getFloat(formWindowCustom.getResponse().getInputResponse(3));
                float z = StringUtil.getFloat(formWindowCustom.getResponse().getInputResponse(4));
                String world = formWindowCustom.getResponse().getInputResponse(5);

                FloatingItem find = FloatingItemsPlugin.get().search(id);

                find.setItem(cn.nukkit.item.Item.get(StringUtil.getInteger(rawItem[0]), StringUtil.getInteger(rawItem[1]), StringUtil.getInteger(rawItem[2])));
                find.setPosition(new Position(x, y, z, Server.getInstance().getLevelByName(world)));

                FloatingItemsPlugin.get().update(find);
                p.sendMessage("§aFloating item §7#" + find.getId() + " §aedited!");
                return;
            }
        }
    }
}
