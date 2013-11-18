package mcp.mobius.waila.client;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mcp.mobius.waila.overlay.RayTracing;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.ForgeSubscribe;

public class WailaClientEventHandler {

	@ForgeSubscribe
    @SideOnly(Side.CLIENT)
    public void onRenderWorldLast(RenderWorldLastEvent event) {	
		if (RayTracing.raytracedTarget == null) return;
		
		double partialTicks = event.partialTicks;

        EntityLivingBase player = Minecraft.getMinecraft().renderViewEntity;
        double px = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks;
        double py = player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks;
        double pz = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks;

        int bx = RayTracing.raytracedTarget.blockX;
        int by = RayTracing.raytracedTarget.blockY;
        int bz = RayTracing.raytracedTarget.blockZ;

        double x = bx - px;
        double y = by - py;
        double z = bz - pz;        
        
        //double offset = 0.02;
        //double delta = 1 + 2 * offset;

        //double offset = 0.02;
        //double delta = 1 + 2 * offset;

        //double x = bx - px - offset;
        //double y = by - py - offset;
        //double z = bz - pz - offset;

        GL11.glPushAttrib(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDepthMask(false);

        /*
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();

        tessellator.setColorRGBA(255, 0, 0, 150);

        tessellator.addVertex(x, y, z);
        tessellator.addVertex(x + delta, y, z);
        tessellator.addVertex(x + delta, y, z + delta);
        tessellator.addVertex(x, y, z + delta);

        tessellator.addVertex(x, y + delta, z);
        tessellator.addVertex(x, y + delta, z + delta);
        tessellator.addVertex(x + delta, y + delta, z + delta);
        tessellator.addVertex(x + delta, y + delta, z);

        tessellator.addVertex(x, y, z);
        tessellator.addVertex(x, y + delta, z);
        tessellator.addVertex(x + delta, y + delta, z);
        tessellator.addVertex(x + delta, y, z);

        tessellator.addVertex(x, y, z + delta);
        tessellator.addVertex(x + delta, y, z + delta);
        tessellator.addVertex(x + delta, y + delta, z + delta);
        tessellator.addVertex(x, y + delta, z + delta);

        tessellator.addVertex(x, y, z);
        tessellator.addVertex(x, y, z + delta);
        tessellator.addVertex(x, y + delta, z + delta);
        tessellator.addVertex(x, y + delta, z);

        tessellator.addVertex(x + delta, y, z);
        tessellator.addVertex(x + delta, y + delta, z);
        tessellator.addVertex(x + delta, y + delta, z + delta);
        tessellator.addVertex(x + delta, y, z + delta);

        tessellator.draw();
		*/

        GL11.glEnable(GL11.GL_TEXTURE_2D);

        GL11.glPopAttrib();
        
    }
}
