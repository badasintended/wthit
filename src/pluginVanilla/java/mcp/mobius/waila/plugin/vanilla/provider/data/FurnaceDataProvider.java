package mcp.mobius.waila.plugin.vanilla.provider.data;

import mcp.mobius.waila.api.IDataProvider;
import mcp.mobius.waila.api.IDataWriter;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.IServerAccessor;
import mcp.mobius.waila.api.data.ProgressData;
import mcp.mobius.waila.mixin.AbstractFurnaceBlockEntityAccess;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;

public enum FurnaceDataProvider implements IDataProvider<AbstractFurnaceBlockEntity> {

    INSTANCE;

    @Override
    public void appendData(IDataWriter data, IServerAccessor<AbstractFurnaceBlockEntity> accessor, IPluginConfig config) {
        data.add(ProgressData.TYPE, res -> {
            var furnace = accessor.getTarget();
            var access = (AbstractFurnaceBlockEntityAccess) furnace;

            if (furnace.getBlockState().getValue(AbstractFurnaceBlock.LIT)) res.add(ProgressData
                .tick(access.wthit_cookingTimer(), access.wthit_cookingTotalTime())
                .itemGetter(furnace::getItem)
                .input(0, 1)
                .output(2));
        });
    }

}
