package mcp.mobius.waila.plugin.vanilla.config;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.lang.reflect.Type;
import java.util.LinkedHashSet;
import java.util.Set;

public class DefaultRollbackBlacklistConfig {
    public final transient LinkedHashSet<Block> blocks = new LinkedHashSet<>();
    public final transient LinkedHashSet<BlockEntityType<?>> blockEntityTypes = new LinkedHashSet<>();
    public final transient LinkedHashSet<EntityType<?>> entityTypes = new LinkedHashSet<>();

    public final LinkedHashSet<ResourceLocation> blockIds = new LinkedHashSet<>();
    public final LinkedHashSet<ResourceLocation> blockEntityTypeIds = new LinkedHashSet<>();
    public final LinkedHashSet<ResourceLocation> entityTypeIds = new LinkedHashSet<>();

    public static class Adapter implements JsonSerializer<DefaultRollbackBlacklistConfig>, JsonDeserializer<DefaultRollbackBlacklistConfig> {

        private <T> void serialize(JsonObject object, String key, Registry<T> registry, Set<ResourceLocation> rlSet, Set<T> tSet) {
            JsonArray array = new JsonArray();

            for (T t : tSet) {
                rlSet.add(registry.getKey(t));
            }
            for (ResourceLocation rl : rlSet) {
                array.add(rl.toString());
            }

            object.add(key, array);
        }

        @Override
        public JsonElement serialize(DefaultRollbackBlacklistConfig src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject object = new JsonObject();

            serialize(object, "blocks", BuiltInRegistries.BLOCK, src.blockIds, src.blocks);
            serialize(object, "blockEntityTypes", BuiltInRegistries.BLOCK_ENTITY_TYPE, src.blockEntityTypeIds, src.blockEntityTypes);
            serialize(object, "entityTypes", BuiltInRegistries.ENTITY_TYPE, src.entityTypeIds, src.entityTypes);

            return object;
        }

        private <T> void deserialize(JsonObject object, String key, Registry<T> registry, Set<ResourceLocation> rlSet, Set<T> tSet) {
            JsonArray array = object.getAsJsonArray(key);
            for (JsonElement element : array) {
                ResourceLocation rl = new ResourceLocation(element.getAsString());
                rlSet.add(rl);
                registry.getOptional(rl).ifPresent(tSet::add);
            }
        }

        @Override
        public DefaultRollbackBlacklistConfig deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject object = json.getAsJsonObject();
            DefaultRollbackBlacklistConfig res = new DefaultRollbackBlacklistConfig();

            deserialize(object, "blocks", BuiltInRegistries.BLOCK, res.blockIds, res.blocks);
            deserialize(object, "blockEntityTypes", BuiltInRegistries.BLOCK_ENTITY_TYPE, res.blockEntityTypeIds, res.blockEntityTypes);
            deserialize(object, "entityTypes", BuiltInRegistries.ENTITY_TYPE, res.entityTypeIds, res.entityTypes);

            return res;
        }
    }
}
