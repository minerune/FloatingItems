package me.seetch.floatingitems.util;

import cn.nukkit.Server;
import cn.nukkit.level.Location;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.Map;

public class LocationSerializer {

    public static String serialize(Location location) {
        JsonObject data = new JsonObject();
        data.addProperty("x", location.getX());
        data.addProperty("y", location.getY());
        data.addProperty("z", location.getZ());
        data.addProperty("yaw", location.getYaw());
        data.addProperty("pitch", location.getPitch());
        data.addProperty("headYaw", location.getHeadYaw());
        data.addProperty("level", location.getLevel().getName());

        return new Gson().toJson(data);
    }

    @SuppressWarnings("unchecked")
    public static Location deserialize(String data) {
        Map<String, Object> dat = (Map<String, Object>) new Gson().fromJson(data, Map.class);

        return new Location(
                (Double) dat.get("x"),
                (Double) dat.get("y"),
                (Double) dat.get("z"),
                (Double) dat.get("yaw"),
                (Double) dat.get("pitch"),
                (Double) dat.get("headYaw"),
                Server.getInstance().getLevelByName((String) dat.get("level"))
        );
    }
}
