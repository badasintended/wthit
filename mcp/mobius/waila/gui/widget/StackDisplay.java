package mcp.mobius.waila.gui.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.RenderEngine;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;

public class StackDisplay extends BaseWidget {

    protected static RenderItem drawItems = new RenderItem();
	protected ItemStack stack;
	protected Icon icon;
	protected int sizeX;
	protected int sizeY;
	
	public StackDisplay(GuiScreen parent){
		this.parent = parent;
	};
	
	public void setStack(ItemStack stack){
		this.stack = stack;
		this.icon  = stack.getIconIndex();
	}
	
	@Override
	public void draw() {
	    drawItems.renderItemAndEffectIntoGUI(this.fontRenderer, this.renderEngine, this.stack, this.posX, this.posY);
	}

	@Override
	public int getWidth(){
		return 16;
	}

	@Override
	public int getHeight(){
		return 16;
	}	
}
