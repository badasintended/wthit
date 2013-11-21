package mcp.mobius.waila.gui.helpers;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.Vec3;

public class UIHelper {
	
    public static void drawTexture(int posX, int posY, int sizeX, int sizeY)
    {
    	UIHelper.drawTexture(posX, posY, sizeX, sizeY, 0, 0, 256, 256);
    }	
    
    public static void drawTexture(int posX, int posY, int sizeX, int sizeY, int texU, int texV, int texSizeU, int texSizeV)
    {
        float zLevel = 0.0F;
        float f = 0.00390625F;
        
        Tessellator tess = Tessellator.instance;
        tess.startDrawingQuads();
        tess.addVertexWithUV((double)(posX + 0),     (double)(posY + sizeY), (double)zLevel, texU*f, (texV + texSizeV)*f);
        tess.addVertexWithUV((double)(posX + sizeX), (double)(posY + sizeY), (double)zLevel, (texU + texSizeU)*f, (texV + texSizeV)*f);
        tess.addVertexWithUV((double)(posX + sizeX), (double)(posY + 0),     (double)zLevel, (texU + texSizeU)*f, texV*f);
        tess.addVertexWithUV((double)(posX + 0),     (double)(posY + 0),     (double)zLevel, texU*f, texV*f);
        tess.draw();
    }	    
    
    public static void drawGradientRect(int minx, int miny, int maxx, int maxy, int zlevel, int color1, int color2)
    {
        float alpha1 = (float)(color1 >> 24 & 255) / 255.0F;
        float red1   = (float)(color1 >> 16 & 255) / 255.0F;
        float green1 = (float)(color1 >> 8 & 255) / 255.0F;
        float blue1  = (float)(color1 & 255) / 255.0F;
        float alpha2 = (float)(color2 >> 24 & 255) / 255.0F;
        float red2   = (float)(color2 >> 16 & 255) / 255.0F;
        float green2 = (float)(color2 >> 8 & 255) / 255.0F;
        float blue2  = (float)(color2 & 255) / 255.0F;
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glShadeModel(GL11.GL_SMOOTH);
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.setColorRGBA_F(red1, green1, blue1, alpha1);
        tessellator.addVertex((double)maxx, (double)miny, (double)zlevel);
        tessellator.addVertex((double)minx, (double)miny, (double)zlevel);
        tessellator.setColorRGBA_F(red2, green2, blue2, alpha2);
        tessellator.addVertex((double)minx, (double)maxy, (double)zlevel);
        tessellator.addVertex((double)maxx, (double)maxy, (double)zlevel);
        tessellator.draw();
        GL11.glShadeModel(GL11.GL_FLAT);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }    
    
    public static void drawBillboardText(String text, Vec3 pos, float offX, float offY, float offZ, double partialFrame){
    	UIHelper.drawBillboardText(text, (float)pos.xCoord, (float)pos.yCoord, (float)pos.zCoord, offX, offY, offZ, partialFrame);
    }
    
    public static void drawBillboardText(String text, float posX, float posY, float posZ, float offX, float offY, float offZ, double partialFrame){
        EntityLivingBase player = Minecraft.getMinecraft().renderViewEntity;
        float playerViewY = player.prevRotationYaw + (player.rotationYaw - player.prevRotationYaw) * (float)partialFrame;
        float playerViewX = player.prevRotationPitch + (player.rotationPitch - player.prevRotationPitch) * (float)partialFrame;      	
        
        UIHelper.drawFloatingText(text, posX, posY, posZ, offX, offY, offZ, playerViewX, playerViewY * -1.0F, 0.0F);
    }

    public static void drawFloatingText(String text, Vec3 pos, float offX, float offY, float offZ, float rotX, float rotY, float rotZ){
    	UIHelper.drawFloatingText(text, (float)pos.xCoord, (float)pos.yCoord, (float)pos.zCoord, offX, offY, offZ, rotX, rotY, rotZ);
    }
    
    public static void drawFloatingText(String text, float posX, float posY, float posZ, float offX, float offY, float offZ, float rotX, float rotY, float rotZ){
    	
    	if (text.isEmpty()) return;
    	
        FontRenderer fontrenderer = Minecraft.getMinecraft().fontRenderer;    	
    	
        float f = 1.6F;
        float f1 = 0.016666668F * f;
        GL11.glPushMatrix();
        

        //GL11.glTranslatef((float)accessor.getPosition().blockX + 0.0F, (float)accessor.getPosition().blockY + 0.5F, (float)accessor.getPosition().blockZ);
        GL11.glTranslatef(posX + offX, posY + offY, posZ + offZ);
        
        //GL11.glTranslatef(posX, posY, posZ);
        GL11.glNormal3f(0.0F, 1.0F, 0.0F);
        GL11.glRotatef(rotY, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(rotX, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(rotZ, 0.0F, 0.0F, 1.0F);        
        //GL11.glTranslatef(offX, offY, offZ);
        
        GL11.glScalef(-f1, -f1, f1);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDepthMask(false);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        byte b0 = 0;

		Tessellator tessellator = Tessellator.instance;
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        tessellator.startDrawingQuads();
        int j = fontrenderer.getStringWidth(text) / 2;
        tessellator.setColorRGBA_F(0.0F, 0.0F, 0.0F, 0.25F);
        tessellator.addVertex((double)(-j - 1), (double)(-1 + b0), 0.0D);
        tessellator.addVertex((double)(-j - 1), (double)(8 + b0), 0.0D);
        tessellator.addVertex((double)(j + 1), (double)(8 + b0), 0.0D);
        tessellator.addVertex((double)(j + 1), (double)(-1 + b0), 0.0D);
        tessellator.draw();
        
        
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        fontrenderer.drawString(text, -fontrenderer.getStringWidth(text) / 2, b0, 553648127);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(true);
        fontrenderer.drawString(text, -fontrenderer.getStringWidth(text) / 2, b0, -1);
        //GL11.glEnable(GL11.GL_LIGHTING);
        //GL11.glDisable(GL11.GL_BLEND);
        //GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glPopMatrix();	    	
    	
    }     
    
    public static void drawRectangle(double x1, double y1, double z1, double x2, double y2, double z2, int r, int g, int b, int a){
		Tessellator tessellator = Tessellator.instance;
		
        tessellator.setColorRGBA(r, g, b, a);
        tessellator.startDrawingQuads();
    	
        tessellator.addVertex(x1, y2, z1);
        tessellator.addVertex(x1, y1, z2);
        tessellator.addVertex(x2, y1, z2);
        tessellator.addVertex(x2, y2, z1);

        tessellator.draw();
        
    }
    
    public static void drawRectangleEW(double x1, double y1, double z1, double x2, double y2, double z2, int r, int g, int b, int a){
		Tessellator tessellator = Tessellator.instance;
		
        tessellator.setColorRGBA(r, g, b, a);
        tessellator.startDrawingQuads();

        tessellator.addVertex(x1, y1, z1);
        tessellator.addVertex(x1, y1, z2);
        tessellator.addVertex(x2, y2, z2);
        tessellator.addVertex(x2, y2, z1);        
        
        tessellator.draw();    	
    }    
    
}
