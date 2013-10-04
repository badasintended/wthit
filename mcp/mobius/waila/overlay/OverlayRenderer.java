package mcp.mobius.waila.overlay;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import codechicken.nei.api.IHighlightHandler;
import codechicken.nei.api.ItemInfo;
import codechicken.nei.forge.GuiContainerManager;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.EntityRegistry.EntityRegistration;

import java.awt.Dimension;
import java.awt.Point;

import mcp.mobius.waila.Constants;
import mcp.mobius.waila.addons.ConfigHandler;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumMovingObjectType;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.IShearable;
import static codechicken.core.gui.GuiDraw.*;

public class OverlayRenderer {

    public static void renderOverlay()
    {
        Minecraft mc = Minecraft.getMinecraft();
        if(!(mc.currentScreen == null &&
             mc.theWorld != null &&
             !mc.gameSettings.keyBindPlayerList.pressed &&
             ConfigHandler.instance().getConfig(Constants.CFG_WAILA_SHOW, true) &&
             mc.objectMouseOver != null)) 
        	return;
    
        if (RayTracing.raytracedTarget.typeOfHit == EnumMovingObjectType.TILE)
        {
            World world = mc.theWorld;
            ArrayList<ItemStack> items = RayTracing.getIdentifierItems();
            if (items.isEmpty())
                return;
            
            Collections.sort(items, new Comparator<ItemStack>()
            {
                @Override
                public int compare(ItemStack stack0, ItemStack stack1)
                {
                    return stack1.getItemDamage() - stack0.getItemDamage();
                }
            });

            ItemStack stack = items.get(0);
            renderOverlay(stack, ItemInfo.getText(stack, world, mc.thePlayer, RayTracing.raytracedTarget), getPositioning());
        }
        
        if (RayTracing.raytracedTarget.typeOfHit == EnumMovingObjectType.ENTITY)
        {
        	Entity ent = mc.objectMouseOver.entityHit;
        	String modName = getEntityMod(ent);
        	
        	List<String> textData = new ArrayList<String>();
        	try{
        		textData.add("\u00a7f" + ent.getEntityName());
        	} catch (Exception e){
        		textData.add("\u00a7f" + "Unknown");
        	}
        	textData.add("\u00a79\u00a7o" + modName);
        	
        	renderOverlay(ent, textData, getPositioning());
        }
    }		
	
    private static String getEntityMod(Entity entity){
    	String modName = "";
    	try{
    		EntityRegistration er = EntityRegistry.instance().lookupModSpawn(entity.getClass(), true);
    		ModContainer modC = er.getContainer();
    		modName = modC.getName();
    	} catch (NullPointerException e){
    		modName = "Minecraft";	
    	}
    	return modName;
    }
	
    private static Point getPositioning()
    {
        return new Point(5000, 100);
    }    
    
    public static void renderOverlay(ItemStack stack, List<String> textData, Point pos)
    {
        int w = 0;
        for (String s : textData)
            w = Math.max(w, getStringWidth(s)+29);
        int h = Math.max(24, 10 + 10*textData.size());

        Dimension size = displaySize();
        int x = (size.width-w-1)*pos.x/10000;
        int y = (size.height-h-1)*pos.y/10000;
        
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        
        drawTooltipBox(x, y, w, h);

        int ty = (h-8*textData.size())/2;
        for (int i = 0; i < textData.size(); i++)
            drawString(textData.get(i), x + 24, y + ty + 10*i, 0xFFA0A0A0, true);

        RenderHelper.enableGUIStandardItemLighting();
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        
        if (stack.getItem() != null)
            GuiContainerManager.drawItem(x+5, y+h/2-8, stack);
    }    
    
    public static void renderOverlay(Entity entity, List<String> textData, Point pos)
    {
        int w = 0;
        for (String s : textData)
            w = Math.max(w, getStringWidth(s)+10);
        int h = Math.max(24, 10 + 10*textData.size());

        Dimension size = displaySize();
        int x = (size.width-w-1)*pos.x/10000;
        int y = (size.height-h-1)*pos.y/10000;
        
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        
        drawTooltipBox(x, y, w, h);

        int ty = (h-8*textData.size())/2;
        for (int i = 0; i < textData.size(); i++)
            drawString(textData.get(i), x + 6, y + ty + 10*i, 0xFFA0A0A0, true);

        RenderHelper.enableGUIStandardItemLighting();
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
    }     
    
}
