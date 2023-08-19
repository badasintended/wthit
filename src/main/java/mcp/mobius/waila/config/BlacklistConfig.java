package mcp.mobius.waila.config;

import java.lang.reflect.Type;
import java.util.Collection;
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
import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.IBlacklistConfig;
import mcp.mobius.waila.api.IRegistryFilter;
import mcp.mobius.waila.util.Log;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.jetbrains.annotations.Nullable;

public class BlacklistConfig {

    private static final Log LOG = Log.create();
    private static final String BLACKLIST_TAG = "#" + Waila.id("blacklist");

    public static final int VERSION = 0;

    public final LinkedHashSet<String> blocks = new LinkedHashSet<>();
    public final LinkedHashSet<String> blockEntityTypes = new LinkedHashSet<>();
    public final LinkedHashSet<String> entityTypes = new LinkedHashSet<>();

    private int configVersion = 0;
    public int[] pluginHash = {0, 0, 0};

    @Nullable
    private transient View view;

    public int getConfigVersion() {
        return configVersion;
    }

    public void setConfigVersion(int configVersion) {
        this.configVersion = configVersion;
    }

    public View getView() {
        if (view == null) view = new View();
        return view;
    }

    public class View implements IBlacklistConfig {

        public final IRegistryFilter<Block> blockFilter;
        public final IRegistryFilter<BlockEntityType<?>> blockEntityFilter;
        public final IRegistryFilter<EntityType<?>> entityFilter;

        private Collection<Block> syncedBlockFilter = Set.of();
        private Collection<BlockEntityType<?>> syncedBlockEntityFilter = Set.of();
        private Collection<EntityType<?>> syncedEntityFilter = Set.of();

        private View() {
            blockFilter = IRegistryFilter.of(Registries.BLOCK).parse(blocks).build();
            blockEntityFilter = IRegistryFilter.of(Registries.BLOCK_ENTITY_TYPE).parse(blockEntityTypes).build();
            entityFilter = IRegistryFilter.of(Registries.ENTITY_TYPE).parse(entityTypes).build();
        }

        public void sync(Set<String> blockRules, Set<String> blockEntityRules, Set<String> entityRules) {
            syncedBlockFilter = sync(Registries.BLOCK, blocks, blockRules);
            syncedBlockEntityFilter = sync(Registries.BLOCK_ENTITY_TYPE, blockEntityTypes, blockEntityRules);
            syncedEntityFilter = sync(Registries.ENTITY_TYPE, entityTypes, entityRules);
        }

        private static <T> Collection<T> sync(ResourceKey<? extends Registry<T>> registryKey, Set<String> localRules, Set<String> syncedRules) {
            LOG.debug("Syncing blacklist {}", registryKey.location());

            return IRegistryFilter.of(registryKey)
                .parse(syncedRules.stream()
                    .filter(it -> !localRules.contains(it))
                    .toArray(String[]::new))
                .build()
                .getMatches();
        }

        @Override
        public boolean contains(Block block) {
            return blockFilter.matches(block) || syncedBlockFilter.contains(block);
        }

        @Override
        public boolean contains(BlockEntity blockEntity) {
            var type = blockEntity.getType();
            return blockEntityFilter.matches(type) || syncedBlockEntityFilter.contains(type);
        }

        @Override
        public boolean contains(Entity entity) {
            var type = entity.getType();
            return entityFilter.matches(type) || syncedEntityFilter.contains(type);
        }

    }

    public static class Adapter implements JsonSerializer<BlacklistConfig>, JsonDeserializer<BlacklistConfig> {

        @Override
        public JsonElement serialize(BlacklistConfig src, Type typeOfSrc, JsonSerializationContext context) {
            var object = new JsonObject();

            var comments = """
                On the SERVER, changes will be applied after the server is restarted
                On the CLIENT, changes will be applied after player quit and rejoin a world
                                
                Rule Operators:
                @namespace - Filter objects based on their namespace location
                #tag       - Filter objects based on data pack tags
                /regex/    - Filter objects based on regular expression
                default    - Filter objects with specific ID
                                
                The %s tag rule can not be removed"""
                .formatted(BLACKLIST_TAG)
                .split("\n");

            var commentArray = new JsonArray();
            for (var line : comments) commentArray.add(line);
            object.add("_comment", commentArray);

            object.add("blocks", context.serialize(src.blocks));
            object.add("blockEntityTypes", context.serialize(src.blockEntityTypes));
            object.add("entityTypes", context.serialize(src.entityTypes));

            object.addProperty("configVersion", src.configVersion);
            object.add("pluginHash", context.serialize(src.pluginHash));

            return object;
        }

        @Override
        public BlacklistConfig deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            var object = json.getAsJsonObject();
            var res = new BlacklistConfig();

            res.blocks.add(BLACKLIST_TAG);
            res.blockEntityTypes.add(BLACKLIST_TAG);
            res.entityTypes.add(BLACKLIST_TAG);

            deserializeEntries(res.blocks, object.getAsJsonArray("blocks"));
            deserializeEntries(res.blockEntityTypes, object.getAsJsonArray("blockEntityTypes"));
            deserializeEntries(res.entityTypes, object.getAsJsonArray("entityTypes"));

            res.configVersion = object.get("configVersion").getAsInt();
            res.pluginHash = context.deserialize(object.get("pluginHash"), int[].class);

            return res;
        }

        private void deserializeEntries(LinkedHashSet<String> set, JsonArray array) {
            for (var entry : array) {
                set.add(entry.getAsString());
            }
        }

    }

}
