package mcp.mobius.waila.plugin.vanilla.component;

import mcp.mobius.waila.api.IBlockAccessor;
import mcp.mobius.waila.api.IBlockComponentProvider;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.api.IWailaConfig;
import mcp.mobius.waila.api.WailaConstants;
import mcp.mobius.waila.plugin.vanilla.config.Options;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;

public enum SpawnerComponent implements IBlockComponentProvider {

    INSTANCE;

    @Override
    public void appendHead(ITooltip tooltip, IBlockAccessor accessor, IPluginConfig config) {
        if (config.getBoolean(Options.SPAWNER_TYPE)) {
            SpawnerBlockEntity spawner = (SpawnerBlockEntity) accessor.getBlockEntity();
            Entity entity = spawner != null ? spawner.getSpawner().getOrCreateDisplayEntity(accessor.getWorld()) : null;
            if (entity != null) {
                String formatting = IWailaConfig.get().getFormatting().getBlockName();
                tooltip.set(WailaConstants.OBJECT_NAME_TAG, new TextComponent(String.format(formatting,
                    accessor.getBlock().getName().getString() + " (" + entity.getDisplayName().getString() + ")")));
            }
        }
    }

}
