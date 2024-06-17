package mcp.mobius.waila.plugin.vanilla.provider;

import java.util.ArrayList;
import java.util.List;

import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;
import mcp.mobius.waila.api.IBlockAccessor;
import mcp.mobius.waila.api.IBlockComponentProvider;
import mcp.mobius.waila.api.IData;
import mcp.mobius.waila.api.IDataProvider;
import mcp.mobius.waila.api.IDataWriter;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.IServerAccessor;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.api.component.PairComponent;
import mcp.mobius.waila.buildconst.Tl;
import mcp.mobius.waila.mixin.BeehiveBlockEntityAccess;
import mcp.mobius.waila.plugin.vanilla.config.Options;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import org.jetbrains.annotations.Nullable;

public enum BeehiveProvider implements IBlockComponentProvider, IDataProvider<BeehiveBlockEntity> {

    INSTANCE;

    public static final ResourceLocation OCCUPANTS_DATA = new ResourceLocation("bee.occupants");

    @Override
    public void appendBody(ITooltip tooltip, IBlockAccessor accessor, IPluginConfig config) {
        var occupants = accessor.getData().get(OccupantsData.class);
        if (occupants != null && config.getBoolean(Options.BEE_HIVE_OCCUPANTS)) {
            var names = new Object2IntLinkedOpenHashMap<String>(occupants.occupants.size());

            for (var occupant : occupants.occupants) {
                Component component = null;
                if (occupant.customName != null) component = Component.Serializer.fromJson(occupant.customName);
                if (component == null) component = occupant.entityType.getDescription();

                var name = component.getString();
                names.put(name, names.getOrDefault(name, 0) + 1);
            }

            for (var entry : names.object2IntEntrySet()) {
                var name = entry.getKey();
                var count = entry.getIntValue();
                if (count > 1) tooltip.addLine(Component.literal(count + " " + name));
                else tooltip.addLine(Component.literal(name));
            }
        }

        if (config.getBoolean(Options.BEE_HIVE_HONEY_LEVEL)) {
            var state = accessor.getBlockState();
            tooltip.addLine(new PairComponent(
                Component.translatable(Tl.Tooltip.HONEY_LEVEL),
                Component.literal(state.getValue(BeehiveBlock.HONEY_LEVEL).toString())));
        }
    }

    @Override
    public void appendData(IDataWriter data, IServerAccessor<BeehiveBlockEntity> accessor, IPluginConfig config) {
        if (config.getBoolean(Options.BEE_HIVE_OCCUPANTS)) {
            var stored = ((BeehiveBlockEntityAccess) accessor.getTarget()).wthit_stored();
            if (!stored.isEmpty()) {
                var occupants = new ArrayList<OccupantsData.Occupant>(stored.size());

                for (var beeData : stored) {
                    var beeNbt = beeData.wthit_entityData();

                    var entityType = EntityType.by(beeNbt);
                    if (entityType.isEmpty()) continue;

                    var customName = beeNbt.contains("CustomName", Tag.TAG_STRING)
                        ? beeNbt.getString("CustomName")
                        : null;

                    occupants.add(new OccupantsData.Occupant(entityType.get(), customName));
                }

                if (!occupants.isEmpty()) data.addImmediate(new OccupantsData(occupants));
            }
        }
    }

    public record OccupantsData(List<Occupant> occupants) implements IData {

        public OccupantsData(FriendlyByteBuf buf) {
            this(buf.readList(b -> new Occupant(
                b.readById(BuiltInRegistries.ENTITY_TYPE),
                b.readUtf())));
        }

        @Override
        public void write(FriendlyByteBuf buf) {
            buf.writeCollection(occupants, (b, occupant) -> {
                b.writeId(BuiltInRegistries.ENTITY_TYPE, occupant.entityType);
                b.writeNullable(occupant.customName, FriendlyByteBuf::writeUtf);
            });
        }

        public record Occupant(EntityType<?> entityType, @Nullable String customName) {

        }

    }

}
