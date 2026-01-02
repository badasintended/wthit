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

public class CoreClientPlugin implements IWailaClientPlugin {

    private static final int PRIORITY = 900;

    @Override
    public void register(IClientRegistrar registrar) {
        registrar.rayCastVector(CoreRayCastVectorProvider.INSTANCE, 1100);

        registrar.head(BlockProvider.INSTANCE, Block.class, PRIORITY);
        registrar.body(BlockProvider.INSTANCE, Block.class, PRIORITY);
        registrar.tail(BlockProvider.INSTANCE, Block.class, PRIORITY);

        registrar.eventListener(CoreEventListener.INSTANCE, PRIORITY);

        registrar.icon(BlockProvider.INSTANCE, Block.class, 1100);

        registrar.icon(FluidProvider.INSTANCE, LiquidBlock.class, PRIORITY);
        registrar.head(FluidProvider.INSTANCE, LiquidBlock.class, PRIORITY);

        registrar.icon(EntityProvider.INSTANCE, Entity.class, 1100);
        registrar.head(EntityProvider.INSTANCE, Entity.class, PRIORITY);
        registrar.body(EntityProvider.INSTANCE, LivingEntity.class, PRIORITY);
        registrar.tail(EntityProvider.INSTANCE, Entity.class, PRIORITY);

        registrar.redirect(BlockProvider.INSTANCE, Block.class, 500);
        registrar.redirect(EntityProvider.INSTANCE, Entity.class, 500);

        registrar.themeType(WailaConstants.THEME_TYPE_GRADIENT, GradientTheme.TYPE);
        registrar.themeType(WailaConstants.THEME_TYPE_NINE_PATCH, NinePatchTheme.TYPE);
    }

}
