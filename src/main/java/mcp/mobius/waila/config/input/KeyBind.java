package mcp.mobius.waila.config.input;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Objects;

import com.google.common.collect.MapMaker;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.mojang.blaze3d.platform.InputConstants;

public final class KeyBind {

    private static final Map<InputConstants.Key, KeyBind> INSTANCES = new MapMaker().weakValues().makeMap();

    public static final KeyBind UNKNOWN = of(InputConstants.UNKNOWN);

    private final InputConstants.Key key;

    private boolean pendingPressed;
    private boolean pressed;
    private boolean wasPressed;

    private KeyBind(InputConstants.Key key) {
        this.key = key;
    }

    public static KeyBind of(InputConstants.Key key) {
        return INSTANCES.computeIfAbsent(key, KeyBind::new);
    }

    public static void set(InputConstants.Key key, boolean pressed) {
        var bind = INSTANCES.get(key);
        if (bind == null) return;

        bind.pendingPressed = pressed;
    }

    public static void tick() {
        for (var instance : INSTANCES.values()) {
            instance.wasPressed = instance.pressed;
            instance.pressed = instance.pendingPressed;
        }
    }

    public boolean isDown() {
        return pressed;
    }

    public boolean isPressed() {
        return pressed && !wasPressed;
    }

    public InputConstants.Key key() {
        return key;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        var bind = (KeyBind) o;
        return Objects.equals(key, bind.key);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(key);
    }

    @Override
    public String toString() {
        return key.getName();
    }

    public static class Adapter implements JsonSerializer<KeyBind>, JsonDeserializer<KeyBind> {

        @Override
        public KeyBind deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            try {
                return of(InputConstants.getKey(json.getAsString()));
            } catch (IllegalArgumentException e) {
                return UNKNOWN;
            }
        }

        @Override
        public JsonElement serialize(KeyBind src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.key.getName());
        }

    }

}
