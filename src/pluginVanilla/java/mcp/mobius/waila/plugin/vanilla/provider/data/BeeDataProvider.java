package mcp.mobius.waila.plugin.vanilla.provider.data;

import mcp.mobius.waila.api.IData;
import mcp.mobius.waila.api.IDataProvider;
import mcp.mobius.waila.api.IDataWriter;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.IServerAccessor;
import mcp.mobius.waila.plugin.vanilla.config.Options;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Bee;

public enum BeeDataProvider implements IDataProvider<Bee> {

    INSTANCE;

    public static final ResourceLocation HIVE_POS = new ResourceLocation("bee.hive_pos");

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
