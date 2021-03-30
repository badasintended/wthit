package mcp.mobius.waila.overlay;

import java.util.LinkedHashSet;
import java.util.List;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.IComponentProvider;
import mcp.mobius.waila.api.IEntityComponentProvider;
import mcp.mobius.waila.api.TooltipPosition;
import mcp.mobius.waila.config.PluginConfig;
import mcp.mobius.waila.utils.ExceptionHandler;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;

public class ComponentProvider {

    public static void gatherBlock(DataAccessor accessor, List<Text> tooltip, TooltipPosition position) {
        Registrar registrar = Registrar.INSTANCE;
        Block block = accessor.getBlock();
        BlockEntity blockEntity = accessor.getBlockEntity();

        int rate = Waila.CONFIG.get().getGeneral().getRateLimit();

        if (blockEntity != null && accessor.isTimeElapsed(rate) && Waila.CONFIG.get().getGeneral().shouldDisplayTooltip()) {
            accessor.resetTimer();
            if (!(registrar.getBlockData(block).isEmpty() && registrar.getBlockData(blockEntity).isEmpty())) {
                Waila.sender.requestBlock(blockEntity);
            }
        }

        handleBlock(accessor, tooltip, block, position);
        handleBlock(accessor, tooltip, blockEntity, position);
    }

    private static void handleBlock(DataAccessor accessor, List<Text> tooltip, Object obj, TooltipPosition position) {
        Registrar registrar = Registrar.INSTANCE;
        LinkedHashSet<IComponentProvider> providers = registrar.getBlockComponent(obj, position);
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

    public static void gatherEntity(Entity entity, DataAccessor accessor, List<Text> tooltip, TooltipPosition position) {
        Registrar registrar = Registrar.INSTANCE;
        Entity trueEntity = accessor.getEntity();

        int rate = Waila.CONFIG.get().getGeneral().getRateLimit();

        if (trueEntity != null && accessor.isTimeElapsed(rate)) {
            accessor.resetTimer();

            if (!registrar.getEntityData(trueEntity).isEmpty()) {
                Waila.sender.requestEntity(trueEntity);
            }
        }

        LinkedHashSet<IEntityComponentProvider> providers = registrar.getEntityComponent(entity, position);
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
