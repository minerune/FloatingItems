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
import cn.nukkit.level.Position;
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

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!(sender instanceof Player)) {
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

        FloatingItemsPlugin api = FloatingItemsPlugin.get();

        switch (args[0]) {
            case "spawn" -> {
                if (args.length >= 2) {
                    sender.sendMessage("§eUsage: /floatingitem spawn");
                    return false;
                }
                Item itemInHand = ((Player) sender).getInventory().getItemInHand();
                if (itemInHand.getId() == 0) {
                    sender.sendMessage("§cYou can't spawn floating air!");
                    return false;
                }
                FloatingItem spawn = api.spawn(itemInHand, ((Player) sender).getPosition(), null);
                sender.sendMessage("§aFloating item §7#" + spawn.getId() + " §acreated!");
                return true;
            }
            case "list" -> {
                if (args.length >= 2) {
                    sender.sendMessage("§eUsage: /floatingitem list");
                    return false;
                }
                sender.sendMessage("§aFloating item list:");
                for (FloatingItem i : api.getFloatingItems().values()) {
                    sender.sendMessage("§a* §7#" + i.getId() + " §aPos: §7" + i.getPosition().toString() + "§7, §aItem: §7" + i.getItem().toString());
                }
                return true;
            }
            case "edit" -> {
                if (args.length < 2) {
                    sender.sendMessage("§eUsage: /floatingitem edit <id>");
                    return false;
                }
                FloatingItem find = api.search(args[1]);
                if (find == null) {
                    sender.sendMessage("§cFloating item §7#" + args[1] + " §cnot found!");
                    return false;
                }
                sendEditForm((Player) sender, find);
                return true;
            }
            case "delete" -> {
                if (args.length < 2) {
                    sender.sendMessage("§eUsage: /floatingitem delete <id>");
                    return false;
                }
                FloatingItem find = api.search(args[1]);
                if (find == null) {
                    sender.sendMessage("§cFloating item §7#" + args[1] + " §cnot found!");
                    return false;
                }
                sender.sendMessage("§aFloating item §7#" + find.getId() + " §adeleted!");
                api.delete(find.getId());
                return true;
            }
        }
        return true;
    }

    public static void sendEditForm(Player player, FloatingItem i) {
        Item item = i.getItem();
        Position pos = i.getPosition();

        FormWindowCustom form = new FormWindowCustom("Edit Floating Item", List.of(
                new ElementLabel(i.getId()),
                new ElementInput("§7Item:", "Id:Meta:Count", item.getId() + ":" + item.getDamage() + ":" + item.getCount()),
                new ElementInput("§7X:", "", String.valueOf(pos.getX())),
                new ElementInput("§7Y:", "", String.valueOf(pos.getY())),
                new ElementInput("§7Z:", "", String.valueOf(pos.getZ())),
                new ElementInput("§7World:", "", String.valueOf(pos.getLevel().getFolderName()))
        ));

        player.showFormWindow(form);
    }
}
