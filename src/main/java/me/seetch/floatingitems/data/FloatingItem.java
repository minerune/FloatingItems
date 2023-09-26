package me.seetch.floatingitems.data;

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

@Getter
@Setter
public class FloatingItem {

    private String id;
    private Item item;
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
        addItemEntityPacket.item = item;
        addItemEntityPacket.metadata = new EntityMetadata().putLong(Entity.DATA_FLAGS, 1L << Entity.DATA_FLAG_IMMOBILE).putLong(Entity.DATA_LEAD_HOLDER_EID, -1).putFloat(Entity.DATA_SCALE, 0f);
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

    public void update() {
        // Despawn entity
        despawn();
        // Spawn new entity
        spawnForAll();
    }

    public void despawn() {
        RemoveEntityPacket removeEntityPacket = new RemoveEntityPacket();
        removeEntityPacket.eid = random;
        position.getLevel().getPlayers().values().forEach((player -> player.dataPacket(removeEntityPacket)));
    }
}
