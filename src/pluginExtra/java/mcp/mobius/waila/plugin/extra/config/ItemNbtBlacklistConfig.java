package mcp.mobius.waila.plugin.extra.config;

import java.lang.reflect.Type;
import java.util.LinkedHashSet;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import mcp.mobius.waila.api.IJsonConfig;
import mcp.mobius.waila.api.IRegistryFilter;
import mcp.mobius.waila.api.WailaConstants;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.Nullable;

public class ItemNbtBlacklistConfig {

    private static final String TAG = WailaConstants.NAMESPACE + ":extra/item_nbt_blacklist";
    private final LinkedHashSet<String> items = new LinkedHashSet<>();

    public static final IJsonConfig<ItemNbtBlacklistConfig> CONFIG = IJsonConfig.of(ItemNbtBlacklistConfig.class)
        .file(WailaConstants.NAMESPACE + "/extra/item_nbt_blacklist")
        .gson(new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(ItemNbtBlacklistConfig.class, new ItemNbtBlacklistConfig.Adapter())
            .create())
        .build();

    @Nullable
    private transient IRegistryFilter<Item> view;

    public static IRegistryFilter<Item> get() {
        var c = CONFIG.get();

        if (c.view == null) {
            c.view = IRegistryFilter.of(Registries.ITEM).parse(c.items).build();
        }

        return c.view;
    }

    public static class Adapter implements JsonSerializer<ItemNbtBlacklistConfig>, JsonDeserializer<ItemNbtBlacklistConfig> {

        @Override
        public JsonElement serialize(ItemNbtBlacklistConfig src, Type typeOfSrc, JsonSerializationContext context) {
            var object = new JsonObject();

            var comments = """
                This config controls what items that its NBT data would not be sent to the server,
                regardless if the global Sync NBT option is enabled or not.
                Put items that could have huge NBT data, for example, container items like backpack here.
                Modders can also tag their items with %s to do the same.
                
                The game needs to be restarted for the changes to apply.

                %s

                The %s tag rule can not be removed"""
                .formatted(TAG, IRegistryFilter.getHeader(), TAG)
                .split("\n");

            var commentArray = new JsonArray();
            for (var line : comments) commentArray.add(line);
            object.add("_comment", commentArray);

            object.add("items", context.serialize(src.items));

            return object;
        }

        @Override
        public ItemNbtBlacklistConfig deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            var object = json.getAsJsonObject();
            var res = new  ItemNbtBlacklistConfig();

            res.items.add(TAG);
            for (var e : object.getAsJsonArray("items")) {
                res.items.add(e.getAsString());
            }

            return res;
        }

    }

}
