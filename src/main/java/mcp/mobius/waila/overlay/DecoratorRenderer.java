package mcp.mobius.waila.overlay;

import mcp.mobius.waila.api.IWailaBlockDecorator;
import mcp.mobius.waila.api.impl.ConfigHandler;
import mcp.mobius.waila.api.impl.DataAccessorCommon;
import mcp.mobius.waila.api.impl.ModuleRegistrar;
import mcp.mobius.waila.utils.WailaExceptionHandler;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class DecoratorRenderer {

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        if (RayTracing.instance().getTarget() == null || RayTracing.instance().getTargetStack().isEmpty())
            return;

        double partialTicks = event.getPartialTicks();

        DataAccessorCommon accessor = DataAccessorCommon.instance;
        World world = Minecraft.getMinecraft().world;
        EntityPlayer player = Minecraft.getMinecraft().player;
        Entity viewEntity = Minecraft.getMinecraft().getRenderViewEntity();

        if (world == null || player == null || viewEntity == null)
            return;

        accessor.set(world, player, RayTracing.instance().getTarget(), viewEntity, partialTicks);

        Block block = accessor.getBlock();
        if (!ModuleRegistrar.instance().hasBlockDecorator(block))
            return;

        GlStateManager.pushAttrib();
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.depthMask(false);

        for (List<IWailaBlockDecorator> decoratorsList : ModuleRegistrar.instance().getBlockDecorators(block).values()) {
            for (IWailaBlockDecorator decorator : decoratorsList)
                try {
                    GlStateManager.pushMatrix();
                    decorator.decorateBlock(RayTracing.instance().getTargetStack(), accessor, ConfigHandler.instance());
                    GlStateManager.popMatrix();
                } catch (Throwable e) {
                    GlStateManager.popMatrix();
                    WailaExceptionHandler.handleErr(e, decorator.getClass().toString(), null);
                }
        }

        GlStateManager.enableTexture2D();
        GlStateManager.popAttrib();
    }
}