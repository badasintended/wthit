package mcp.mobius.waila.plugin.extra.config;

import java.lang.reflect.Type;
import java.util.LinkedHashSet;
import java.util.Set;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class ExtraBlacklistConfig {

    public final transient LinkedHashSet<BlockEntityType<?>> blockEntityTypes = new LinkedHashSet<>();
    public final transient LinkedHashSet<EntityType<?>> entityTypes = new LinkedHashSet<>();

    private final LinkedHashSet<ResourceLocation> blockEntityTypeIds = new LinkedHashSet<>();
    private final LinkedHashSet<ResourceLocation> entityTypeIds = new LinkedHashSet<>();

    public static class Adapter implements JsonSerializer<ExtraBlacklistConfig>, JsonDeserializer<ExtraBlacklistConfig> {

        private final ResourceLocation tagId;

        public Adapter(ResourceLocation beTag) {
            this.tagId = beTag;
        }

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

        private <T> void deserialize(JsonObject object, String key, Registry<T> registry, Set<ResourceLocation> rlSet, Set<T> tSet) {
            JsonArray array = object.getAsJsonArray(key);
            for (JsonElement element : array) {
                ResourceLocation rl = new ResourceLocation(element.getAsString());
                rlSet.add(rl);
                registry.getOptional(rl).ifPresent(tSet::add);
            }
        }

        @Override
        public JsonElement serialize(ExtraBlacklistConfig src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject object = new JsonObject();

            JsonArray comment = new JsonArray();

            comment.add("The game needs to be restarted for the changes to apply.");
            comment.add("Along with this file, the server can also use datapack tag to disable the feature for connecting players.");
            comment.add("Block tag            : data/" + tagId.getNamespace() + "/tags/blocks/" + tagId.getPath() + ".json");
            comment.add("Block entity type tag: data/" + tagId.getNamespace() + "/tags/block_entity_type/" + tagId.getPath() + ".json");
            comment.add("Entity type tag      : data/" + tagId.getNamespace() + "/tags/entity_types/extra/" + tagId.getPath() + ".json");
            comment.add("Note that block_entity_type is not plural like blocks and entity_types, this is not an error.");
            object.add("_comment", comment);

            serialize(object, "blockEntityTypes", Registry.BLOCK_ENTITY_TYPE, src.blockEntityTypeIds, src.blockEntityTypes);
            serialize(object, "entityTypes", Registry.ENTITY_TYPE, src.entityTypeIds, src.entityTypes);

            return object;
        }

        @Override
        public ExtraBlacklistConfig deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject object = json.getAsJsonObject();
            ExtraBlacklistConfig res = new ExtraBlacklistConfig();

            deserialize(object, "blockEntityTypes", Registry.BLOCK_ENTITY_TYPE, res.blockEntityTypeIds, res.blockEntityTypes);
            deserialize(object, "entityTypes", Registry.ENTITY_TYPE, res.entityTypeIds, res.entityTypes);

            return res;
        }

    }

}
