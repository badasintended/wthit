package mcp.mobius.waila.client;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mcp.mobius.waila.WailaExceptionHandler;
import mcp.mobius.waila.api.IWailaBlockDecorator;
import mcp.mobius.waila.api.impl.ConfigHandler;
import mcp.mobius.waila.api.impl.DataAccessor;
import mcp.mobius.waila.api.impl.ModuleRegistrar;
import mcp.mobius.waila.overlay.RayTracing;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.ForgeSubscribe;

public class WailaClientEventHandler {

	@ForgeSubscribe
    @SideOnly(Side.CLIENT)
    public void onRenderWorldLast(RenderWorldLastEvent event) {	
		if (RayTracing.instance().getTarget() == null || RayTracing.instance().getTargetStack() == null) return;
		
		double partialTicks = event.partialTicks;

		DataAccessor accessor       = DataAccessor.instance;
		World world                 = Minecraft.getMinecraft().theWorld;
		EntityPlayer player         = Minecraft.getMinecraft().thePlayer;
		EntityLivingBase viewEntity = Minecraft.getMinecraft().renderViewEntity;
		
		if (world == null || player == null || viewEntity == null) return;
		
		accessor.set(world, player, RayTracing.instance().getTarget(), viewEntity, partialTicks);		

		Block block   = accessor.getBlock();
		int   blockID = accessor.getBlockID();

		if (!ModuleRegistrar.instance().hasBlockDecorator(block)) return;
		
        GL11.glPushAttrib(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDepthMask(false);		

		if (ModuleRegistrar.instance().hasBlockDecorator(block)){
			for (IWailaBlockDecorator decorator : ModuleRegistrar.instance().getBlockDecorators(block))
				try{
					GL11.glPushMatrix();
					decorator.decorateBlock(RayTracing.instance().getTargetStack(), accessor, ConfigHandler.instance());
					GL11.glPopMatrix();					
				} catch (Throwable e){
					GL11.glPopMatrix();
					WailaExceptionHandler.handleErr(e, decorator.getClass().toString(), null);
				}			
		}
		
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glPopAttrib();		

	}
}
