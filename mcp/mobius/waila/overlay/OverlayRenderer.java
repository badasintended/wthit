package mcp.mobius.waila.overlay;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import codechicken.nei.forge.GuiContainerManager;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.EntityRegistry.EntityRegistration;

import java.awt.Dimension;
import java.awt.Point;

import mcp.mobius.waila.Constants;
import mcp.mobius.waila.mod_Waila;
import mcp.mobius.waila.api.impl.ConfigHandler;
import mcp.mobius.waila.api.impl.DataAccessorEntity;
import mcp.mobius.waila.gui.truetyper.TrueTypeFont;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumMovingObjectType;
import static codechicken.core.gui.GuiDraw.*;

public class OverlayRenderer {

	protected static boolean hasBlending;
	protected static boolean hasLight;
	protected static int     boundTexIndex;   	
	
    public static void renderOverlay()
    {
        Minecraft mc = Minecraft.getMinecraft();
        if(!(mc.currentScreen == null &&
             mc.theWorld != null &&
             mc.isGuiEnabled() &&
             !mc.gameSettings.keyBindPlayerList.pressed &&
             ConfigHandler.instance().getConfig(Constants.CFG_WAILA_SHOW, true) &&
             RayTracing.instance().getTarget()      != null))
        	return;
    
        if (RayTracing.instance().getTarget().typeOfHit == EnumMovingObjectType.TILE && RayTracing.instance().getTargetStack() != null)
        {
            renderOverlay(RayTracing.instance().getTargetStack(), WailaTickHandler.instance().currenttip, getPositioning());
        }
        
        if (RayTracing.instance().getTarget().typeOfHit == EnumMovingObjectType.ENTITY)
        {
        	renderOverlay(DataAccessorEntity.instance.getEntity(), WailaTickHandler.instance().currenttip, getPositioning()); // Might need change for the override       	
        }
    }		
	
    private static Point getPositioning()
    {
        return new Point(ConfigHandler.instance().getConfigInt(Constants.CFG_WAILA_POSX), ConfigHandler.instance().getConfigInt(Constants.CFG_WAILA_POSY));
    }    
    
    /*
    public static int getColor(String key){
    	int alpha = (int)(ConfigHandler.instance().getConfigInt(Constants.CFG_WAILA_ALPHA) / 100.0f * 256) << 24;
    	return alpha + ConfigHandler.instance().getConfigInt(key);
    }
    */
    
    public static void renderOverlay(ItemStack stack, List<String> textData, Point pos)
    {
    	//TrueTypeFont font = (TrueTypeFont)mod_Waila.proxy.getFont();
    	
    	GL11.glPushMatrix();
    	
    	GL11.glScalef(mod_Waila.scale, mod_Waila.scale, 1.0f);
    	
        int w = 0;
        for (String s : textData)
            w = Math.max(w, getStringWidth(s)+29);
        int h = Math.max(24, 10 + 10*textData.size());

        Dimension size = displaySize();
        int x = ((int)(size.width / mod_Waila.scale)-w-1)*pos.x/10000;
        int y = ((int)(size.height / mod_Waila.scale)-h-1)*pos.y/10000;
        
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        
        drawTooltipBox(x, y, w, h, mod_Waila.bgcolor, mod_Waila.gradient1, mod_Waila.gradient2);

        int ty = (h-10*textData.size())/2;
        
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA,GL11.GL_ONE_MINUS_SRC_ALPHA);     
        for (int i = 0; i < textData.size(); i++)
        	//FontHelper.drawString(textData.get(i), x + 24, y + ty + 10*i, font, 1.0f, 1.0f, new float[] {1.0f, 1.0f, 1.0f});
            drawString(textData.get(i), x + 24, y + ty + 10*i, mod_Waila.fontcolor, true);
        GL11.glDisable(GL11.GL_BLEND);
        

        RenderHelper.enableGUIStandardItemLighting();
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        
        if (stack.getItem() != null)
            GuiContainerManager.drawItem(x+5, y+h/2-8, stack);
        
    	GL11.glPopMatrix();        
    }    
    
    public static void renderOverlay(Entity entity, List<String> textData, Point pos)
    {
    	GL11.glPushMatrix();    	
    	
    	GL11.glScalef(mod_Waila.scale, mod_Waila.scale, 1.0f);    	
    	
        int w = 0;
        for (String s : textData)
            w = Math.max(w, getStringWidth(s)+10);
        int h = Math.max(24, 10 + 10*textData.size());

        Dimension size = displaySize();
        int x = ((int)(size.width / mod_Waila.scale)-w-1)*pos.x/10000;
        int y = ((int)(size.height / mod_Waila.scale)-h-1)*pos.y/10000;
        
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        
        drawTooltipBox(x, y, w, h, mod_Waila.bgcolor, mod_Waila.gradient1, mod_Waila.gradient2);

        int ty = (h-10*textData.size())/2;

		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA,GL11.GL_ONE_MINUS_SRC_ALPHA);         
        for (int i = 0; i < textData.size(); i++)
            drawString(textData.get(i), x + 6, y + ty + 10*i, mod_Waila.fontcolor, true);
        GL11.glDisable(GL11.GL_BLEND);
        
        //RenderHelper.enableGUIStandardItemLighting();
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        
    	GL11.glPopMatrix();         
    }     

    public static void saveGLState(){
		hasBlending   = GL11.glGetBoolean(GL11.GL_BLEND);
		hasLight      = GL11.glGetBoolean(GL11.GL_LIGHTING);
    	boundTexIndex = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);  
    	GL11.glPushAttrib(GL11.GL_CURRENT_BIT);
    }
    
    public static void loadGLState(){
    	if (hasBlending) GL11.glEnable(GL11.GL_BLEND); else GL11.glDisable(GL11.GL_BLEND);
    	if (hasLight) GL11.glEnable(GL11.GL_LIGHTING); else	GL11.glDisable(GL11.GL_LIGHTING);
    	GL11.glBindTexture(GL11.GL_TEXTURE_2D, boundTexIndex);
    	GL11.glPopAttrib();
    	//GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
    }    
    
    public static void drawTooltipBox(int x, int y, int w, int h, int bg, int grad1, int grad2)
    {
        //int bg = 0xf0100010;
        drawGradientRect(x + 1, y, w - 1, 1, bg, bg);
        drawGradientRect(x + 1, y + h, w - 1, 1, bg, bg);
        drawGradientRect(x + 1, y + 1, w - 1, h - 1, bg, bg);//center
        drawGradientRect(x, y + 1, 1, h - 1, bg, bg);
        drawGradientRect(x + w, y + 1, 1, h - 1, bg, bg);
        //int grad1 = 0x505000ff;
        //int grad2 = 0x5028007F;
        drawGradientRect(x + 1, y + 2, 1, h - 3, grad1, grad2);
        drawGradientRect(x + w - 1, y + 2, 1, h - 3, grad1, grad2);
        
        drawGradientRect(x + 1, y + 1, w - 1, 1, grad1, grad1);
        drawGradientRect(x + 1, y + h - 1, w - 1, 1, grad2, grad2);
    }    
    
}
