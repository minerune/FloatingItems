package me.seetch.floatingitem.item;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.data.EntityMetadata;
import cn.nukkit.item.Item;
import cn.nukkit.level.Position;
import cn.nukkit.network.protocol.AddItemEntityPacket;
import cn.nukkit.network.protocol.RemoveEntityPacket;
import lombok.Getter;
import lombok.Setter;

import java.util.Random;

public class FloatingItem {

    @Getter
    @Setter
    private String id;
    @Getter
    @Setter
    private Item item;
    @Getter
    @Setter
    private Position position;

    private final long random;

    public FloatingItem(String id, Item item, Position position) {
        this.id = id;
        this.item = item;
        this.position = position;
        random = new Random().nextLong();
    }

    private AddItemEntityPacket getPacket() {
        AddItemEntityPacket addItemEntityPacket = new AddItemEntityPacket();
        addItemEntityPacket.speedX = 0f;
        addItemEntityPacket.speedY = 0f;
        addItemEntityPacket.speedZ = 0f;
        addItemEntityPacket.x = (float) position.getX();
        addItemEntityPacket.y = (float) position.getY();
        addItemEntityPacket.z = (float) position.getZ();
        addItemEntityPacket.entityUniqueId = random;
        addItemEntityPacket.entityRuntimeId = random;
        addItemEntityPacket.item = this.item;
        addItemEntityPacket.metadata = new EntityMetadata()
                .putLong(Entity.DATA_FLAGS, 1L << Entity.DATA_FLAG_IMMOBILE)
                .putLong(Entity.DATA_LEAD_HOLDER_EID, -1)
                .putFloat(Entity.DATA_SCALE, 0f);
        return addItemEntityPacket;
    }

    public FloatingItem spawn(Player player) {
        player.dataPacket(getPacket());
        return this;
    }

    public FloatingItem spawnForAll() {
        position.getLevel().getPlayers().values().forEach((player -> player.dataPacket(getPacket())));
        return this;
    }

    // Yeah, I know it's not cool ^-^
    public void update() {
        delete();
        spawnForAll();
    }

    public void delete() {
        RemoveEntityPacket removeEntityPacket = new RemoveEntityPacket();
        removeEntityPacket.eid = random;
        position.getLevel().getPlayers().values().forEach((player -> player.dataPacket(removeEntityPacket)));
    }
}
