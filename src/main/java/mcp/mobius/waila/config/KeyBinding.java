package mcp.mobius.waila.config;

import java.lang.reflect.Type;
import java.util.Map;

import com.google.common.collect.MapMaker;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.Nullable;

public final class KeyBinding {

    private static final Map<InputConstants.Key, KeyBinding> INSTANCES = new MapMaker().weakValues().makeMap();

    public static final KeyBinding UNKNOWN = of(InputConstants.UNKNOWN);

    private final InputConstants.Key key;

    private @Nullable Boolean pressed;
    private @Nullable Boolean wasPressed;
    private boolean held;

    private KeyBinding(InputConstants.Key key) {
        this.key = key;
    }

    public static KeyBinding of(InputConstants.Key key) {
        return INSTANCES.computeIfAbsent(key, KeyBinding::new);
    }

    public boolean isDown() {
        if (pressed == null) {
            pressed = key.getValue() != InputConstants.UNKNOWN.getValue()
                      && InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), key.getValue());

            if (!pressed) {
                held = false;
            } else {
                held = wasPressed == Boolean.TRUE;
            }
        }

        return pressed;
    }

    public boolean isPressed() {
        isDown();
        return pressed == Boolean.TRUE && !held;
    }

    public static void tick() {
        for (var instance : INSTANCES.values()) {
            instance.wasPressed = instance.pressed;
            instance.pressed = null;
        }
    }

    public InputConstants.Key key() {
        return key;
    }

    public static class Adapter implements JsonSerializer<KeyBinding>, JsonDeserializer<KeyBinding> {

        @Override
        public KeyBinding deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return of(InputConstants.getKey(json.getAsString()));
        }

        @Override
        public JsonElement serialize(KeyBinding src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.key.getName());
        }

    }

}
