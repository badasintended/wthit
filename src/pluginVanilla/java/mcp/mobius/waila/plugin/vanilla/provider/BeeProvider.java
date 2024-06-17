package mcp.mobius.waila.plugin.vanilla.provider;

import mcp.mobius.waila.api.IData;
import mcp.mobius.waila.api.IDataProvider;
import mcp.mobius.waila.api.IDataWriter;
import mcp.mobius.waila.api.IEntityAccessor;
import mcp.mobius.waila.api.IEntityComponentProvider;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.IServerAccessor;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.api.component.PairComponent;
import mcp.mobius.waila.api.component.PositionComponent;
import mcp.mobius.waila.api.component.WrappedComponent;
import mcp.mobius.waila.buildconst.Tl;
import mcp.mobius.waila.plugin.vanilla.config.Options;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Bee;

public enum BeeProvider implements IEntityComponentProvider, IDataProvider<Bee> {

    INSTANCE;

    public static final IData.Type<HivePosData> HIVE_POS_DATA = IData.createType(ResourceLocation.withDefaultNamespace("bee.hive_pos"));
    public static final StreamCodec<RegistryFriendlyByteBuf, HivePosData> HIVE_POS_DATA_CODEC = StreamCodec.composite(
        BlockPos.STREAM_CODEC, HivePosData::pos,
        HivePosData::new);

    @Override
    public void appendBody(ITooltip tooltip, IEntityAccessor accessor, IPluginConfig config) {
        var hivePos = accessor.getData().get(HIVE_POS_DATA);
        if (hivePos != null && config.getBoolean(Options.BEE_HIVE_POS)) {
            tooltip.addLine(new PairComponent(
                new WrappedComponent(Component.translatable(Tl.Tooltip.Bee.HIVE)),
                new PositionComponent(hivePos.pos)));
        }
    }

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
            return HIVE_POS_DATA;
        }

    }

}
