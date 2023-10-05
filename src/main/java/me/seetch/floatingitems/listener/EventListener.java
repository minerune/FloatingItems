package me.seetch.floatingitems.listener;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerFormRespondedEvent;
import cn.nukkit.form.window.FormWindowCustom;
import me.seetch.floatingitems.FloatingItemsPlugin;
import me.seetch.floatingitems.data.FloatingItem;
import me.seetch.floatingitems.util.StringUtil;

public class EventListener implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onFormResponse(PlayerFormRespondedEvent event) {
        if (event.getWindow() instanceof FormWindowCustom formWindowCustom) {
            Player p = event.getPlayer();
            if (event.getResponse() == null || event.getWindow().wasClosed()) {
                return;
            }
            if (formWindowCustom.getTitle().equals("Edit Floating Item")) {
                String id = formWindowCustom.getResponse().getLabelResponse(0);
                String[] rawItem = formWindowCustom.getResponse().getInputResponse(1).split(":");
                float x = StringUtil.getFloat(formWindowCustom.getResponse().getInputResponse(2));
                float y = StringUtil.getFloat(formWindowCustom.getResponse().getInputResponse(3));
                float z = StringUtil.getFloat(formWindowCustom.getResponse().getInputResponse(4));
                String world = formWindowCustom.getResponse().getInputResponse(5);

                FloatingItem floatingItem = FloatingItemsPlugin.getFloatingItem(id);

                floatingItem.getItem().setDamage(StringUtil.getInteger(rawItem[0]));
                floatingItem.getItem().setCount(StringUtil.getInteger(rawItem[1]));
                floatingItem.getLocation().setX(x);
                floatingItem.getLocation().setY(y);
                floatingItem.getLocation().setZ(z);
                floatingItem.getLocation().setLevel(Server.getInstance().getLevelByName(world));

                floatingItem.respawnToAll();

                p.sendMessage("§aFloating item §7#" + floatingItem.getFloatingItemId() + " §aedited!");
            }
        }
    }
}
