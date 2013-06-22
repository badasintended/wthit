package mcp.mobius.waila.gui.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
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
	public FontRenderer fontRender   = Minecraft.getMinecraft().fontRenderer;
	public RenderEngine renderEngine = Minecraft.getMinecraft().renderEngine;
	
	public StackDisplay(ItemStack stack, int sizeX, int sizeY) {
		this.stack = stack;
		this.icon  = stack.getIconIndex();
		this.sizeX = sizeX;
		this.sizeY = sizeY;
	}

	@Override
	public void draw(int x, int y, int z) {
	    drawItems.renderItemAndEffectIntoGUI(this.fontRender, this.renderEngine, this.stack, x, y);
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
