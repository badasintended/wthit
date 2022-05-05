package mcp.mobius.waila.bukkit;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import org.bukkit.NamespacedKey;

public class NamespacedKeySerializer implements JsonSerializer<NamespacedKey>, JsonDeserializer<NamespacedKey> {

    @Override
    public NamespacedKey deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return NamespacedKey.fromString(json.getAsString());
    }

    @Override
    public JsonElement serialize(NamespacedKey src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src.toString());
    }

}
