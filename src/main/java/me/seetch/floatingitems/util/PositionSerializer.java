package me.seetch.floatingitems.util;

import cn.nukkit.Server;
import cn.nukkit.level.Position;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.Map;

public class PositionSerializer {

    public static String serialize(Position position) {
        JsonObject data = new JsonObject();
        data.addProperty("x", position.getX());
        data.addProperty("y", position.getY());
        data.addProperty("z", position.getZ());
        data.addProperty("level", position.getLevel().getName());

        return new Gson().toJson(data);
    }

    @SuppressWarnings("unchecked")
    public static Position deserialize(String data) {
        Map<String, Object> dat = (Map<String, Object>) new Gson().fromJson(data, Map.class);

        return new Position(
                (Double) dat.get("x"),
                (Double) dat.get("y"),
                (Double) dat.get("z"),
                Server.getInstance().getLevelByName((String) dat.get("level"))
        );
    }
}
