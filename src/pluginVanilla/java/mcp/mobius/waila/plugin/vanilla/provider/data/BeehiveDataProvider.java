package mcp.mobius.waila.plugin.vanilla.provider.data;

import java.util.ArrayList;
import java.util.List;

import mcp.mobius.waila.api.IData;
import mcp.mobius.waila.api.IDataProvider;
import mcp.mobius.waila.api.IDataWriter;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.IServerAccessor;
import mcp.mobius.waila.api.util.WCodecs;
import mcp.mobius.waila.mixin.BeehiveBlockEntityAccess;
import mcp.mobius.waila.plugin.vanilla.config.Options;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import org.jetbrains.annotations.Nullable;

public enum BeehiveDataProvider implements IDataProvider<BeehiveBlockEntity> {

    INSTANCE;

    public static final IData.Type<OccupantsData> OCCUPANTS = IData.createType(ResourceLocation.withDefaultNamespace("bee.occupants"));
    public static final StreamCodec<RegistryFriendlyByteBuf, OccupantsData> OCCUPANTS_CODEC = StreamCodec.composite(
        StreamCodec.composite(
            ByteBufCodecs.registry(Registries.ENTITY_TYPE), OccupantsData.Occupant::entityType,
                WCodecs.nullable(ByteBufCodecs.STRING_UTF8), OccupantsData.Occupant::customName,
            OccupantsData.Occupant::new).apply(ByteBufCodecs.list()), OccupantsData::occupants,
        OccupantsData::new);

    @Override
    public void appendData(IDataWriter data, IServerAccessor<BeehiveBlockEntity> accessor, IPluginConfig config) {
        if (config.getBoolean(Options.BEE_HIVE_OCCUPANTS)) {
            var stored = ((BeehiveBlockEntityAccess) accessor.getTarget()).wthit_stored();
            if (!stored.isEmpty()) {
                var occupants = new ArrayList<OccupantsData.Occupant>(stored.size());

                for (var beeData : stored) {
                    var beeNbt = beeData.wthit_occupant().entityData().getUnsafe();
                    var entityTypeId = beeNbt.getString("id").orElse(null);
                    if (entityTypeId == null) continue;

                    var entityType = EntityType.byString(entityTypeId).orElse(null);
                    if (entityType == null) continue;

                    var customName = beeNbt.getString("CustomName").orElse(null);

                    occupants.add(new OccupantsData.Occupant(entityType, customName));
                }

                if (!occupants.isEmpty()) data.addImmediate(new OccupantsData(occupants));
            }
        }
    }

    public record OccupantsData(List<Occupant> occupants) implements IData {

        public record Occupant(EntityType<?> entityType, @Nullable String customName) {}

        @Override
        public Type<? extends IData> type() {
            return OCCUPANTS;
        }

    }

}
