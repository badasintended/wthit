package mcp.mobius.waila.config;

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
import mcp.mobius.waila.api.IBlacklistConfig;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class BlacklistConfig implements IBlacklistConfig {

    public static final int VERSION = 0;

    private int configVersion = 0;
    public int[] pluginHash = {0, 0, 0};

    public final transient LinkedHashSet<Block> blocks = new LinkedHashSet<>();
    public final transient LinkedHashSet<BlockEntityType<?>> blockEntityTypes = new LinkedHashSet<>();
    public final transient LinkedHashSet<EntityType<?>> entityTypes = new LinkedHashSet<>();

    public final LinkedHashSet<ResourceLocation> blockIds = new LinkedHashSet<>();
    public final LinkedHashSet<ResourceLocation> blockEntityTypeIds = new LinkedHashSet<>();
    public final LinkedHashSet<ResourceLocation> entityTypeIds = new LinkedHashSet<>();

    public int getConfigVersion() {
        return configVersion;
    }

    public void setConfigVersion(int configVersion) {
        this.configVersion = configVersion;
    }

    @Override
    public boolean contains(Block block) {
        return blocks.contains(block);
    }

    @Override
    public boolean contains(BlockEntity blockEntity) {
        return blockEntityTypes.contains(blockEntity.getType());
    }

    @Override
    public boolean contains(Entity entity) {
        return entityTypes.contains(entity.getType());
    }

    public static class Adapter implements JsonSerializer<BlacklistConfig>, JsonDeserializer<BlacklistConfig> {

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
        public JsonElement serialize(BlacklistConfig src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject object = new JsonObject();

            JsonArray comment = new JsonArray();
            comment.add("NOTE: The server needs to be restarted for the blacklist to apply.");
            comment.add("      If you play on single player, simply close and re-enter the world.");
            object.add("_comment", comment);

            serialize(object, "blocks", Registry.BLOCK, src.blockIds, src.blocks);
            serialize(object, "blockEntityTypes", Registry.BLOCK_ENTITY_TYPE, src.blockEntityTypeIds, src.blockEntityTypes);
            serialize(object, "entityTypes", Registry.ENTITY_TYPE, src.entityTypeIds, src.entityTypes);

            object.addProperty("configVersion", src.configVersion);
            object.add("pluginHash", context.serialize(src.pluginHash));

            return object;
        }

        @Override
        public BlacklistConfig deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject object = json.getAsJsonObject();

            BlacklistConfig res = new BlacklistConfig();

            deserialize(object, "blocks", Registry.BLOCK, res.blockIds, res.blocks);
            deserialize(object, "blockEntityTypes", Registry.BLOCK_ENTITY_TYPE, res.blockEntityTypeIds, res.blockEntityTypes);
            deserialize(object, "entityTypes", Registry.ENTITY_TYPE, res.entityTypeIds, res.entityTypes);

            res.configVersion = object.get("configVersion").getAsInt();
            res.pluginHash = context.deserialize(object.get("pluginHash"), int[].class);

            return res;
        }

    }

}
