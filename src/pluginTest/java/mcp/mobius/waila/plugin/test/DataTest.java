package mcp.mobius.waila.plugin.test;

import mcp.mobius.waila.api.IBlockAccessor;
import mcp.mobius.waila.api.IBlockComponentProvider;
import mcp.mobius.waila.api.IData;
import mcp.mobius.waila.api.IDataProvider;
import mcp.mobius.waila.api.IDataWriter;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.IServerAccessor;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.api.component.PairComponent;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.ChestBlockEntity;

public enum DataTest implements IBlockComponentProvider, IDataProvider<ChestBlockEntity> {

    INSTANCE;

    public static final ResourceLocation ENABLED = new ResourceLocation("test:data");

    public static class Data implements IData {

        private final String value;

        public Data(String value) {
            this.value = value;
        }

        public Data(FriendlyByteBuf buf) {
            this(buf.readUtf());
        }

        @Override
        public void write(FriendlyByteBuf buf) {
            buf.writeUtf(value);
        }

    }

    @Override
    public void appendBody(ITooltip tooltip, IBlockAccessor accessor, IPluginConfig config) {
        Data data = accessor.getData().get(Data.class);
        if (data != null) tooltip.addLine(new PairComponent(Component.literal("data"), Component.literal(data.value)));
    }

    @Override
    public void appendData(IDataWriter data, IServerAccessor<ChestBlockEntity> accessor, IPluginConfig config) {
        if (config.getBoolean(ENABLED)) data.add(new Data("moe moe kyun"));
    }

}
