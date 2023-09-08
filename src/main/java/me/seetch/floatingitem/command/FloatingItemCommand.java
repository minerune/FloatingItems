package me.seetch.floatingitem.command;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.item.Item;
import cn.nukkit.level.Position;
import me.seetch.floatingitem.FloatingItemPlugin;
import me.seetch.floatingitem.item.FloatingItem;
import org.jetbrains.annotations.NotNull;
import ru.hype.form.forms.elements.CustomForm;

public class FloatingItemCommand extends Command {

    public FloatingItemCommand() {
        super("fit", "", "/fit <spawn|list|edit|delete>");
        this.setPermission("floating-item.use");
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

        FloatingItemPlugin api = FloatingItemPlugin.get();

        switch (args[0]) {
            case "spawn" -> {
                if (args.length >= 2) {
                    sender.sendMessage("§eUsage: /fit spawn");
                    return false;
                }
                Item itemInHand = ((Player) sender).getInventory().getItemInHand();
                if (itemInHand.getId() == 0) {
                    sender.sendMessage("§cYou can't spawn floating air!");
                    return false;
                }
                FloatingItem spawn = api.spawnForAll(itemInHand, sender.getPosition());
                sender.sendMessage("§aFloating item created! §7[" + spawn.getId() + "]");
                return true;
            }
            case "list" -> {
                if (args.length >= 2) {
                    sender.sendMessage("§eUsage: /fit list");
                    return false;
                }
                sender.sendMessage("§aFloating item list:");
                for (FloatingItem i : api.getFloatingItems().values()) {
                    sender.sendMessage("§a* §7[" + i.getId() + "] §aPos: §7"+i.getPosition().toString() +"§7, §aItem: §7" + i.getItem().toString());
                }
                return true;
            }
            case "edit" -> {
                if (args.length < 2) {
                    sender.sendMessage("§eUsage: /fit edit <id>");
                    return false;
                }
                FloatingItem find = api.search(args[1]);
                if (find == null) {
                    sender.sendMessage("§cFloating item not found!");
                    return false;
                }
                sendEditForm((Player) sender, find);
                return true;
            }
            case "delete" -> {
                if (args.length < 2) {
                    sender.sendMessage("§eUsage: /fit delete <id>");
                    return false;
                }
                FloatingItem find = api.search(args[1]);
                if (find == null) {
                    sender.sendMessage("§cFloating item not found!");
                    return false;
                }
                sender.sendMessage("§aFloating item deleted! §7[" + find.getId() + "]");
                api.delete(find.getId());
                return true;
            }
        }
        return true;
    }

    public static void sendEditForm(Player player, FloatingItem floatingItem) {
        CustomForm registerForm = getForm(floatingItem);

        registerForm.send(player, (target, form, data) -> {
            if (data != null) {
                String id = data.getString(0);
                String[] rawItem = data.getString(1).split(":");
                float x = Float.parseFloat(data.getString(2));
                float y = Float.parseFloat(data.getString(3));
                float z = Float.parseFloat(data.getString(4));
                String world = data.getString(5);

                FloatingItem find = FloatingItemPlugin.get().search(id);

                find.setItem(cn.nukkit.item.Item.get(Integer.parseInt(rawItem[0]), Integer.parseInt(rawItem[1]), Integer.parseInt(rawItem[2])));
                find.setPosition(new Position(x, y, z, Server.getInstance().getLevelByName(world)));

                FloatingItemPlugin.get().update(find);
                player.sendMessage("§aFloating item edited! §7[" + find.getId() + "]");
                return;
            }
        });
    }

    @NotNull
    private static CustomForm getForm(FloatingItem floatingItem) {
        CustomForm registerForm = new CustomForm("Edit Floating Item");

        Item i = floatingItem.getItem();
        Position pos = floatingItem.getPosition();

        registerForm.addLabel(floatingItem.getId());
        registerForm.addInput("§7Item:", "Id:Meta:Count", i.getId() + ":" + i.getDamage() + ":" + i.getCount());
        registerForm.addInput("§7X:", "", String.valueOf(pos.getX()));
        registerForm.addInput("§7Y:", "", String.valueOf(pos.getY()));
        registerForm.addInput("§7Z:", "", String.valueOf(pos.getZ()));
        registerForm.addInput("§7World", "", String.valueOf(pos.getLevel().getFolderName()));
        return registerForm;
    }
}
