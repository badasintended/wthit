package mcp.mobius.waila.api.impl;

import java.util.LinkedHashSet;
import java.util.List;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.IComponentProvider;
import mcp.mobius.waila.api.IEntityComponentProvider;
import mcp.mobius.waila.api.TooltipPosition;
import mcp.mobius.waila.api.impl.config.PluginConfig;
import mcp.mobius.waila.utils.ExceptionHandler;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;

public class MetaDataProvider {

    private final WailaRegistrar registrar = WailaRegistrar.INSTANCE;

    public void gatherBlockComponents(DataAccessor accessor, List<Text> tooltip, TooltipPosition position) {
        Block block = accessor.getBlock();
        BlockEntity blockEntity = accessor.getBlockEntity();

        int rate = Waila.CONFIG.get().getGeneral().getRateLimit();

        if (blockEntity != null && accessor.isTimeElapsed(rate) && Waila.CONFIG.get().getGeneral().shouldDisplayTooltip()) {
            accessor.resetTimer();
            if (!(WailaRegistrar.INSTANCE.getNBTProviders(block).isEmpty() && WailaRegistrar.INSTANCE.getNBTProviders(blockEntity).isEmpty())) {
                Waila.network.requestBlock(blockEntity);
            }
        }

        LinkedHashSet<IComponentProvider> providers = WailaRegistrar.INSTANCE.getBlockProviders(block, position);
        for (IComponentProvider provider : providers) {
            try {
                switch (position) {
                    case HEAD:
                        provider.appendHead(tooltip, accessor, PluginConfig.INSTANCE);
                        break;
                    case BODY:
                        provider.appendBody(tooltip, accessor, PluginConfig.INSTANCE);
                        break;
                    case TAIL:
                        provider.appendTail(tooltip, accessor, PluginConfig.INSTANCE);
                        break;
                }
            } catch (Throwable e) {
                ExceptionHandler.handleErr(e, provider.getClass().toString(), tooltip);
            }
        }
    }

    public void gatherEntityComponents(Entity entity, DataAccessor accessor, List<Text> tooltip, TooltipPosition position) {
        Entity trueEntity = accessor.getEntity();

        int rate = Waila.CONFIG.get().getGeneral().getRateLimit();

        if (trueEntity != null && accessor.isTimeElapsed(rate)) {
            accessor.resetTimer();

            if (!WailaRegistrar.INSTANCE.getNBTEntityProviders(trueEntity).isEmpty()) {
                Waila.network.requestEntity(trueEntity);
            }
        }

        LinkedHashSet<IEntityComponentProvider> providers = WailaRegistrar.INSTANCE.getEntityProviders(entity, position);
        for (IEntityComponentProvider provider : providers) {
            try {
                switch (position) {
                    case HEAD:
                        provider.appendHead(tooltip, accessor, PluginConfig.INSTANCE);
                        break;
                    case BODY:
                        provider.appendBody(tooltip, accessor, PluginConfig.INSTANCE);
                        break;
                    case TAIL:
                        provider.appendTail(tooltip, accessor, PluginConfig.INSTANCE);
                        break;
                }
            } catch (Throwable e) {
                ExceptionHandler.handleErr(e, provider.getClass().toString(), tooltip);
            }
        }
    }

}
