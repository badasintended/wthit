package mcp.mobius.waila.plugin.vanilla.provider;

import mcp.mobius.waila.api.IBlockAccessor;
import mcp.mobius.waila.api.IBlockComponentProvider;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.api.IWailaConfig;
import mcp.mobius.waila.api.WailaConstants;
import mcp.mobius.waila.plugin.vanilla.config.Options;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;

public enum SpawnerProvider implements IBlockComponentProvider {

    INSTANCE;

    @Override
    public void appendHead(ITooltip tooltip, IBlockAccessor accessor, IPluginConfig config) {
        if (config.getBoolean(Options.SPAWNER_TYPE)) {
            SpawnerBlockEntity spawner = accessor.getBlockEntity();
            var entity = spawner != null ? spawner.getSpawner().getOrCreateDisplayEntity(accessor.getWorld(), spawner.getBlockPos()) : null;
            if (entity != null) {
                //noinspection DataFlowIssue
                var name = entity.getDisplayName().getString();

                tooltip.setLine(WailaConstants.OBJECT_NAME_TAG, IWailaConfig.get().getFormatter().blockName(
                    accessor.getBlock().getName().getString() + " (" + name + ")"));
            }
        }
    }

}
