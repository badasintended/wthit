package mcp.mobius.waila.plugin.extra.config;

import java.lang.reflect.Type;
import java.util.LinkedHashSet;

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
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.jetbrains.annotations.Nullable;

public class ExtraBlacklistConfig {

    public final LinkedHashSet<String> blocks = new LinkedHashSet<>();
    public final LinkedHashSet<String> blockEntityTypes = new LinkedHashSet<>();
    public final LinkedHashSet<String> entityTypes = new LinkedHashSet<>();

    @Nullable
    private transient View view;

    public View getView() {
        if (view == null) view = new View();
        return view;
    }

    public static IJsonConfig.Commenter commenter(ResourceLocation tag) {
        return p -> !p.isEmpty() ? null : """
            Run `/waila reload` to apply changes server-wide.
            Run `/wailac reload` to apply changes to only your client.

            %s

            The #%s tag rule can not be removed"""
            .formatted(IRegistryFilter.getHeader(), tag);
    }

    public class View {

        public final IRegistryFilter<Block> blockFilter;
        public final IRegistryFilter<BlockEntityType<?>> blockEntityFilter;
        public final IRegistryFilter<EntityType<?>> entityFilter;

        public View() {
            blockFilter = IRegistryFilter.of(Registries.BLOCK).parse(blocks).build();
            blockEntityFilter = IRegistryFilter.of(Registries.BLOCK_ENTITY_TYPE).parse(blockEntityTypes).build();
            entityFilter = IRegistryFilter.of(Registries.ENTITY_TYPE).parse(entityTypes).build();
        }

    }

    public static class Adapter implements JsonSerializer<ExtraBlacklistConfig>, JsonDeserializer<ExtraBlacklistConfig> {

        private final String tagRule;

        public Adapter(ResourceLocation tagId) {
            this.tagRule = "#" + tagId.toString();
        }

        @Override
        public JsonElement serialize(ExtraBlacklistConfig src, Type typeOfSrc, JsonSerializationContext context) {
            var object = new JsonObject();

            object.add("blocks", context.serialize(src.blocks));
            object.add("blockEntityTypes", context.serialize(src.blockEntityTypes));
            object.add("entityTypes", context.serialize(src.entityTypes));

            return object;
        }

        @Override
        public ExtraBlacklistConfig deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            var object = json.getAsJsonObject();
            var res = new ExtraBlacklistConfig();

            res.blocks.add(tagRule);
            res.blockEntityTypes.add(tagRule);
            res.entityTypes.add(tagRule);

            deserializeEntries(res.blocks, object.getAsJsonArray("blocks"));
            deserializeEntries(res.blockEntityTypes, object.getAsJsonArray("blockEntityTypes"));
            deserializeEntries(res.entityTypes, object.getAsJsonArray("entityTypes"));

            return res;
        }

        private void deserializeEntries(LinkedHashSet<String> set, JsonArray array) {
            for (var entry : array) {
                set.add(entry.getAsString());
            }
        }

    }

}
