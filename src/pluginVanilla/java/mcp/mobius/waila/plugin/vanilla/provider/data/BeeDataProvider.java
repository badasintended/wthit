package mcp.mobius.waila.plugin.vanilla.provider.data;

import mcp.mobius.waila.api.IData;
import mcp.mobius.waila.api.IDataProvider;
import mcp.mobius.waila.api.IDataWriter;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.IServerAccessor;
import mcp.mobius.waila.plugin.vanilla.config.Options;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Bee;

public enum BeeDataProvider implements IDataProvider<Bee> {

    INSTANCE;

    public static final IData.Type<HivePosData> HIVE_POS = IData.createType(ResourceLocation.withDefaultNamespace("bee.hive_pos"));
    public static final StreamCodec<RegistryFriendlyByteBuf, HivePosData> HIVE_POS_CODEC = StreamCodec.composite(
        BlockPos.STREAM_CODEC, HivePosData::pos,
        HivePosData::new);

    @Override
    public void appendData(IDataWriter data, IServerAccessor<Bee> accessor, IPluginConfig config) {
        if (config.getBoolean(Options.BEE_HIVE_POS)) {
            var hivePos = accessor.getTarget().getHivePos();
            if (hivePos != null) {
                data.addImmediate(new HivePosData(hivePos));
            }
        }
    }

    public record HivePosData(BlockPos pos) implements IData {

        @Override
        public Type<? extends IData> type() {
            return HIVE_POS;
        }

    }

}
