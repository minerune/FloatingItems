package me.seetch.floatingitems.command;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandEnum;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.form.element.ElementInput;
import cn.nukkit.form.element.ElementLabel;
import cn.nukkit.form.window.FormWindowCustom;
import cn.nukkit.item.Item;
import cn.nukkit.level.Location;
import me.seetch.floatingitems.FloatingItemsPlugin;
import me.seetch.floatingitems.data.FloatingItem;

import java.util.List;

public class FloatingItemCommand extends Command {

    public FloatingItemCommand() {
        super("floatingitem", "Manage your floating items.", "/floatingitem <spawn|list|edit|delete>");
        this.setPermission("floatingitem.use");
        this.commandParameters.clear();
        this.commandParameters.put("1arg", new CommandParameter[]{
                CommandParameter.newEnum("spawn", new CommandEnum("Spawn floating item from your hand"))
        });
        this.commandParameters.put("2arg", new CommandParameter[]{
                CommandParameter.newEnum("list", new CommandEnum("List of floating items"))
        });
        this.commandParameters.put("3arg", new CommandParameter[]{
                CommandParameter.newType("edit", CommandParamType.INT),
        });
        this.commandParameters.put("4arg", new CommandParameter[]{
                CommandParameter.newType("delete", CommandParamType.INT),
        });
    }

    public static void sendEditForm(Player player, FloatingItem i) {
        Item item = i.getItem();
        Location loc = i.getLocation();

        FormWindowCustom form = new FormWindowCustom("Edit Floating Item", List.of(
                new ElementLabel(i.getFloatingItemId()),
                new ElementInput("§7Item:", "Meta:Count", item.getDamage() + ":" + item.getCount()),
                new ElementInput("§7X:", "", String.valueOf(loc.getX())),
                new ElementInput("§7Y:", "", String.valueOf(loc.getY())),
                new ElementInput("§7Z:", "", String.valueOf(loc.getZ())),
                new ElementInput("§7World:", "", String.valueOf(loc.getLevel().getFolderName()))
        ));

        player.showFormWindow(form);
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cUsage command in game!");
            return false;
        }

        if (!testPermission(sender)) {
            return false;
        }

        if (args.length < 1) {
            sender.sendMessage("§eUsage: " + usageMessage);
            return false;
        }

        switch (args[0]) {
            case "spawn" -> {
                if (args.length >= 2) {
                    player.sendMessage("§eUsage: /floatingitem spawn");
                    return false;
                }
                Item itemInHand = player.getInventory().getItemInHand();
                if (itemInHand.getId() == 0) {
                    player.sendMessage("§cYou can't spawn floating air!");
                    return false;
                }
                FloatingItem floatingItem = FloatingItemsPlugin.createAndSave(player.getLocation(), itemInHand);
                sender.sendMessage("§aFloating item §7#" + floatingItem.getFloatingItemId() + " §acreated!");
                return true;
            }
            case "list" -> {
                if (args.length >= 2) {
                    sender.sendMessage("§eUsage: /floatingitem list");
                    return false;
                }
                sender.sendMessage("§aFloating items list:");
                for (FloatingItem floatingItem : FloatingItemsPlugin.getFloatingItems().values()) {
                    sender.sendMessage("§7#" + floatingItem.getFloatingItemId() + " §aLoc: §7" + floatingItem.getLocation().toString() + "§7, §aItem: §7" + floatingItem.getItem().toString());
                }
                return true;
            }
            case "edit" -> {
                FloatingItem floatingItem = FloatingItemsPlugin.findNearEntity(player);
                if (floatingItem == null) {
                    sender.sendMessage("§cFloating item §7#" + args[1] + " §cnot found!");
                    return false;
                }
                sendEditForm((Player) sender, floatingItem);
                return true;
            }
            case "delete" -> {
                FloatingItem floatingItem = FloatingItemsPlugin.findNearEntity(player);
                if (floatingItem == null) {
                    sender.sendMessage("§cFloating item not found near you!");
                    return false;
                }

                FloatingItemsPlugin.delete(floatingItem.getFloatingItemId());
                sender.sendMessage("§aFloating item deleted!");
                return true;
            }
        }
        return true;
    }
}
