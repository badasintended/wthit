package mcp.mobius.waila.plugin.test;

import mcp.mobius.waila.api.IBlockAccessor;
import mcp.mobius.waila.api.IBlockComponentProvider;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.IServerAccessor;
import mcp.mobius.waila.api.IServerDataProvider;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.api.component.PairComponent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;

public enum HitResultServerDependantTest implements IBlockComponentProvider, IServerDataProvider<BlockEntity> {

    INSTANCE;

    static final ResourceLocation ENABLED = new ResourceLocation("test:hit_result.enabled");

    @Override
    public void appendBody(ITooltip tooltip, IBlockAccessor accessor, IPluginConfig config) {
        if (config.getBoolean(ENABLED)) {
            tooltip.addLine(new PairComponent(Component.literal("hitX"), Component.literal("" + accessor.getServerData().getDouble("hitX"))));
        }
    }

    @Override
    public void appendServerData(CompoundTag data, IServerAccessor<BlockEntity> accessor, IPluginConfig config) {
        if (config.getBoolean(ENABLED)) {
            BlockHitResult hitResult = accessor.getHitResult();
            data.putDouble("hitX", hitResult.getLocation().x);
        }
    }

}
