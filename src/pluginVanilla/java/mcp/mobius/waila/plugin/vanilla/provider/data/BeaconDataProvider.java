package mcp.mobius.waila.plugin.vanilla.provider.data;

import mcp.mobius.waila.api.IData;
import mcp.mobius.waila.api.IDataProvider;
import mcp.mobius.waila.api.IDataWriter;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.IServerAccessor;
import mcp.mobius.waila.api.util.WCodecs;
import mcp.mobius.waila.mixin.BeaconBlockEntityAccess;
import mcp.mobius.waila.plugin.vanilla.config.Options;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import org.jetbrains.annotations.Nullable;

public enum BeaconDataProvider implements IDataProvider<BeaconBlockEntity> {

    INSTANCE;

    public static final IData.Type<Data> DATA = IData.createType(ResourceLocation.withDefaultNamespace("beacon"));
    public static final StreamCodec<RegistryFriendlyByteBuf, Data> DATA_CODEC = StreamCodec.composite(
        WCodecs.nullable(ByteBufCodecs.holderRegistry(Registries.MOB_EFFECT)), Data::primary,
        WCodecs.nullable(ByteBufCodecs.holderRegistry(Registries.MOB_EFFECT)), Data::secondary,
        Data::new);

    @Override
    public void appendData(IDataWriter data, IServerAccessor<BeaconBlockEntity> accessor, IPluginConfig config) {
        if (config.getBoolean(Options.EFFECT_BEACON)) data.add(DATA, res -> {
            var beacon = (BeaconBlockEntity & BeaconBlockEntityAccess) accessor.getTarget();
            res.add(new Data(beacon.wthit_primaryPower(), beacon.wthit_levels() >= 4 ? beacon.wthit_secondaryPower() : null));
        });
    }

    public record Data(
        @Nullable Holder<MobEffect> primary,
        @Nullable Holder<MobEffect> secondary
    ) implements IData {

        @Override
        public Type<? extends IData> type() {
            return DATA;
        }

    }

}
