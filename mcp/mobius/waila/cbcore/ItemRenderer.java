package mcp.mobius.waila.cbcore;

import static mcp.mobius.waila.cbcore.GuiDraw.fontRenderer;
import static mcp.mobius.waila.cbcore.GuiDraw.renderEngine;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashSet;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

import org.lwjgl.opengl.GL11;

/* THIS CLASS IS A COPY OF THE ORIGINAL CLASS FROM CHICKENBONES */
/* ALL CREDITS GO TO HIM FOR MAKING IT */

public class ItemRenderer {

    public static RenderItem drawItems = new RenderItem();
	
    public static FontRenderer getFontRenderer(ItemStack stack)
    {
        if(stack != null && stack.getItem() != null)
        {
            FontRenderer f = stack.getItem().getFontRenderer(stack);
            if(f != null)
                return f;
        }
        return fontRenderer;
    }

    public static void drawItem(int i, int j, ItemStack itemstack)
    {
        drawItem(i, j, itemstack, getFontRenderer(itemstack));
    }
    
    private static HashSet<String> stackTraces = new HashSet<String>();
    public static void drawItem(int i, int j, ItemStack itemstack, FontRenderer fontRenderer)
    {
        enable3DRender();
        drawItems.zLevel += 100F;
        try
        {
            drawItems.renderItemAndEffectIntoGUI(fontRenderer, renderEngine, itemstack, i, j);
            drawItems.renderItemOverlayIntoGUI(fontRenderer, renderEngine, itemstack, i, j);
        }
        catch(Exception e)
        {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String stackTrace = itemstack+sw.toString();
            if(!stackTraces.contains(stackTrace))
            {
                System.err.println("Error while rendering: "+itemstack);
                e.printStackTrace();
                stackTraces.add(stackTrace);
            }
            
            //if(Tessellator.instance.isDrawing)
            Tessellator.instance.draw();
            drawItems.renderItemIntoGUI(fontRenderer, renderEngine, new ItemStack(Blocks.fire, 1, 0), i, j);
        }
        drawItems.zLevel -= 100F;
        enable2DRender();

        //if(Tessellator.instance.isDrawing)
        try{
        	Tessellator.instance.draw();
        } catch (Exception e){
        	
        }
    }
    
    public static void enable3DRender()
    {
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
    }

    public static void enable2DRender()
    {
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
    }    	
	
}
