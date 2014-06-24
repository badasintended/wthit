package mcp.mobius.waila.gui_old.widget;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;

public class StackDisplay extends BaseWidget {

    protected static RenderItem drawItems = new RenderItem();
	protected ItemStack stack;
	protected Icon icon;
	protected float scale = 1.0F;
	protected boolean drawOverlay = false;
	
	public StackDisplay(GuiScreen parent){
		this.parent = parent;
		StackDisplay.drawItems.zLevel = 500.0f;		
	};

	public StackDisplay(GuiScreen parent, ItemStack stack){
		this.parent = parent;
		this.setStack(stack);
	};	
	
	public void setDrawOverlay(boolean value){
		this.drawOverlay = value;
	}
	
	public void setStack(ItemStack stack){
		this.stack = stack;
		this.icon  = stack.getIconIndex();
	}
	
	@Override
	public void draw() {
		this.drawBackground();
		GL11.glPushMatrix();
		GL11.glScalef(scale, scale, 1.0f);
		
		RenderHelper.disableStandardItemLighting();
	    drawItems.renderItemAndEffectIntoGUI(this.fontRenderer, this.renderEngine, this.stack, (int)(this.posX/scale), (int)(this.posY/scale));
	    if (this.drawOverlay)
	    	drawItems.renderItemOverlayIntoGUI(this.fontRenderer, this.renderEngine, this.stack, (int)(this.posX/scale), (int)(this.posY/scale), String.valueOf(stack.stackSize));
		RenderHelper.enableStandardItemLighting();	    
	    GL11.glPopMatrix();
	    
	}

	
	@Override
	public int getWidth(){
		return (int)(16 * this.scale);
	}

	@Override
	public int getHeight(){
		return (int)(16 * this.scale);
	}	
	
	public void setScale(float scale_){
		this.scale = scale_;
	}
	
	public float getScale(){
		return this.scale;
	}
}
