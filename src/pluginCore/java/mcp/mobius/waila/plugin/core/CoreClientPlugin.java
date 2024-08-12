package mcp.mobius.waila.plugin.core;

import mcp.mobius.waila.api.IClientRegistrar;
import mcp.mobius.waila.api.IWailaClientPlugin;
import mcp.mobius.waila.api.WailaConstants;
import mcp.mobius.waila.plugin.core.event.CoreEventListener;
import mcp.mobius.waila.plugin.core.provider.BlockProvider;
import mcp.mobius.waila.plugin.core.provider.EntityProvider;
import mcp.mobius.waila.plugin.core.provider.FluidProvider;
import mcp.mobius.waila.plugin.core.raycast.CoreRayCastVectorProvider;
import mcp.mobius.waila.plugin.core.theme.GradientTheme;
import mcp.mobius.waila.plugin.core.theme.NinePatchTheme;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;

import static mcp.mobius.waila.api.TooltipPosition.BODY;
import static mcp.mobius.waila.api.TooltipPosition.HEAD;
import static mcp.mobius.waila.api.TooltipPosition.TAIL;

public class CoreClientPlugin implements IWailaClientPlugin {

    private static final int PRIORITY = 900;

    @Override
    public void register(IClientRegistrar registrar) {
        registrar.addRayCastVector(CoreRayCastVectorProvider.INSTANCE, 1100);

        registrar.addComponent(BlockProvider.INSTANCE, HEAD, Block.class, PRIORITY);
        registrar.addComponent(BlockProvider.INSTANCE, BODY, Block.class, PRIORITY);
        registrar.addComponent(BlockProvider.INSTANCE, TAIL, Block.class, PRIORITY);

        registrar.addEventListener(CoreEventListener.INSTANCE, PRIORITY);

        registrar.addIcon(BlockProvider.INSTANCE, Block.class, 1100);

        registrar.addIcon(FluidProvider.INSTANCE, LiquidBlock.class, PRIORITY);
        registrar.addComponent(FluidProvider.INSTANCE, HEAD, LiquidBlock.class, PRIORITY);

        registrar.addIcon(EntityProvider.INSTANCE, Entity.class, 1100);
        registrar.addComponent(EntityProvider.INSTANCE, HEAD, Entity.class, PRIORITY);
        registrar.addComponent(EntityProvider.INSTANCE, BODY, LivingEntity.class, PRIORITY);
        registrar.addComponent(EntityProvider.INSTANCE, TAIL, Entity.class, PRIORITY);

        registrar.addRedirect(BlockProvider.INSTANCE, Block.class, 500);
        registrar.addRedirect(EntityProvider.INSTANCE, Entity.class, 500);

        registrar.addThemeType(WailaConstants.THEME_TYPE_GRADIENT, GradientTheme.TYPE);
        registrar.addThemeType(WailaConstants.THEME_TYPE_NINE_PATCH, NinePatchTheme.TYPE);
    }

}
