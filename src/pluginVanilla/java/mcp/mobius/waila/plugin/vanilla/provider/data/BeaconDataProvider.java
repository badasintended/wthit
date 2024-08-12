package mcp.mobius.waila.plugin.vanilla.provider.data;

import mcp.mobius.waila.api.IData;
import mcp.mobius.waila.api.IDataProvider;
import mcp.mobius.waila.api.IDataWriter;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.IServerAccessor;
import mcp.mobius.waila.mixin.BeaconBlockEntityAccess;
import mcp.mobius.waila.plugin.vanilla.config.Options;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import org.jetbrains.annotations.Nullable;

public enum BeaconDataProvider implements IDataProvider<BeaconBlockEntity> {

    INSTANCE;

    public static final ResourceLocation DATA = new ResourceLocation("beacon");

    @Override
    public void appendData(IDataWriter data, IServerAccessor<BeaconBlockEntity> accessor, IPluginConfig config) {
        if (config.getBoolean(Options.EFFECT_BEACON)) data.add(Data.class, res -> {
            var beacon = (BeaconBlockEntity & BeaconBlockEntityAccess) accessor.getTarget();
            res.add(new Data(beacon.wthit_primaryPower(), beacon.wthit_levels() >= 4 ? beacon.wthit_secondaryPower() : null));
        });
    }

    public record Data(
        @Nullable MobEffect primary,
        @Nullable MobEffect secondary
    ) implements IData {

        public Data(FriendlyByteBuf buf) {
            this(
                buf.readNullable(b -> b.readById(BuiltInRegistries.MOB_EFFECT)),
                buf.readNullable(b -> b.readById(BuiltInRegistries.MOB_EFFECT)));
        }

        @Override
        public void write(FriendlyByteBuf buf) {
            buf.writeNullable(primary, (b, m) -> b.writeId(BuiltInRegistries.MOB_EFFECT, m));
            buf.writeNullable(secondary, (b, m) -> b.writeId(BuiltInRegistries.MOB_EFFECT, m));
        }

    }

}
