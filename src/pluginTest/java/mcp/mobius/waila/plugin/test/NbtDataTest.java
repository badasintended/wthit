package mcp.mobius.waila.plugin.test;

import mcp.mobius.waila.api.IBlockAccessor;
import mcp.mobius.waila.api.IBlockComponentProvider;
import mcp.mobius.waila.api.IDataProvider;
import mcp.mobius.waila.api.IDataWriter;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.IServerAccessor;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.api.component.PairComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.FurnaceBlockEntity;

public enum NbtDataTest implements IBlockComponentProvider, IDataProvider<FurnaceBlockEntity> {

    INSTANCE;

    public static final ResourceLocation ENABLED = ResourceLocation.parse("test:data.nbt");

    @Override
    public void appendBody(ITooltip tooltip, IBlockAccessor accessor, IPluginConfig config) {
        var data = accessor.getData().raw();
        if (data.contains("testnbt")) {
            var value = data.getInt("testnbt");
            tooltip.addLine(new PairComponent(Component.literal("testnbt"), Component.literal(String.valueOf(value))));
        }
    }

    @Override
    public void appendData(IDataWriter data, IServerAccessor<FurnaceBlockEntity> accessor, IPluginConfig config) {
        if (config.getBoolean(ENABLED)) {
            data.raw().putInt("testnbt", 2421);
        }
    }

}
