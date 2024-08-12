package mcp.mobius.waila.plugin.vanilla.provider.data;

import java.util.ArrayList;
import java.util.List;

import mcp.mobius.waila.api.IData;
import mcp.mobius.waila.api.IDataProvider;
import mcp.mobius.waila.api.IDataWriter;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.IServerAccessor;
import mcp.mobius.waila.mixin.BeehiveBlockEntityAccess;
import mcp.mobius.waila.plugin.vanilla.config.Options;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import org.jetbrains.annotations.Nullable;

public enum BeehiveDataProvider implements IDataProvider<BeehiveBlockEntity> {

    INSTANCE;

    public static final ResourceLocation OCCUPANTS = new ResourceLocation("bee.occupants");


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
                b.readNullable(FriendlyByteBuf::readUtf))));
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
