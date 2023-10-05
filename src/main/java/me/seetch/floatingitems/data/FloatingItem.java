package me.seetch.floatingitems.data;

import cn.nukkit.entity.item.EntityItem;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Location;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.DoubleTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.nbt.tag.ListTag;

public class FloatingItem extends EntityItem {

    public FloatingItem(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    public static FloatingItem create(String id, Location location, Item item) {
        FloatingItem itemEntity = null;

        CompoundTag itemTag = NBTIO.putItemHelper(item);
        itemTag.setName("Item");

        if (item.getId() != 0 && item.getCount() > 0) {
            itemEntity = new FloatingItem(
                    location.getLevel().getChunk((int) location.getX() >> 4, (int) location.getZ() >> 4, true),
                    new CompoundTag()
                            .putList(new ListTag<DoubleTag>("Pos")
                                    .add(new DoubleTag("", location.getX()))
                                    .add(new DoubleTag("", location.getY()))
                                    .add(new DoubleTag("", location.getZ())))

                            .putList(new ListTag<DoubleTag>("Motion")
                                    .add(new DoubleTag("", 0))
                                    .add(new DoubleTag("", 0))
                                    .add(new DoubleTag("", 0)))

                            .putList(new ListTag<FloatTag>("Rotation")
                                    .add(new FloatTag("0", (float) location.getYaw()))
                                    .add(new FloatTag("1", (float) location.getPitch())))

                            .putShort("Health", 5)
                            .putCompound("Item", itemTag)
                            .putShort("PickupDelay", 999999)
                            .putBoolean("Invulnerable", true)

                            .putString("floatingItemId", id));
            itemEntity.spawnToAll();
        }

        return itemEntity;
    }

    public String getFloatingItemId() {
        return namedTag.getString("floatingItemId");
    }

    @Override
    public boolean attack(EntityDamageEvent source) {
        return (source.getCause() == EntityDamageEvent.DamageCause.CUSTOM) && super.attack(source);
    }

    @Override
    public boolean onUpdate(int currentTick) {
        return isClosed();
    }
}
