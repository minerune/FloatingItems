package me.seetch.floatingitem.util;

import cn.nukkit.item.Item;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import com.google.gson.Gson;

import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ItemSerializer {

    public static String serialize(Item item) {
        Map<String, Object> data = new HashMap<>();
        data.put("id", item.getId());
        data.put("meta", item.getDamage());
        data.put("count", item.getCount());

        CompoundTag compoundTag = item.getNamedTag();
        try {
            if (compoundTag != null)
                data.put("nbt", NBTIO.write(compoundTag, ByteOrder.LITTLE_ENDIAN));
        } catch (Exception ignored) {
            /* ignored */
        }

        return new Gson().toJson(data);
    }

    @SuppressWarnings("unchecked")
    public static Item deserialize(String data) {
        Map<String, Object> dat = (Map<String, Object>) new Gson().fromJson(data, Map.class);
        Item item = Item.get(
                ((Double) dat.get("id")).intValue(),
                ((Double) dat.get("meta")).intValue(),
                ((Double) dat.get("count")).intValue()
        );

        if (dat.containsKey("nbt")) {
            ArrayList<Double> list = (ArrayList<Double>) dat.get("nbt");
            byte[] nbt = new byte[list.size()];

            for (int i = 0; i < nbt.length; i++)
                nbt[i] = (list.get(i)).byteValue();

            try {
                item.setNamedTag(NBTIO.read(nbt, ByteOrder.LITTLE_ENDIAN));
            } catch (Exception ignored) {
                /* ignored */
            }
        }

        return item;
    }
}
