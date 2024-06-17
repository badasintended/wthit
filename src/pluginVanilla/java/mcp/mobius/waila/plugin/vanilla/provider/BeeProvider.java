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
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Bee;

public enum BeeProvider implements IEntityComponentProvider, IDataProvider<Bee> {

    INSTANCE;

    public static final ResourceLocation HIVE_POS_DATA = new ResourceLocation("bee.hive_pos");

    @Override
    public void appendBody(ITooltip tooltip, IEntityAccessor accessor, IPluginConfig config) {
        var hivePos = accessor.getData().get(HivePosData.class);
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

        public HivePosData(FriendlyByteBuf buf) {
            this(buf.readBlockPos());
        }

        @Override
        public void write(FriendlyByteBuf buf) {
            buf.writeBlockPos(pos);
        }

    }

}
