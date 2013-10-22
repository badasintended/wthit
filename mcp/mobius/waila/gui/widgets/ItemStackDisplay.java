package mcp.mobius.waila.gui.widgets;

import mcp.mobius.waila.gui.interfaces.IWidget;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.Point;

import codechicken.nei.forge.GuiContainerManager;

public class ItemStackDisplay extends WidgetBase {

	ItemStack stack = null;
	
	public ItemStackDisplay(IWidget parent){
		this(parent, null);
	}

	public ItemStackDisplay(IWidget parent, ItemStack stack){
		super(parent);
		this.stack = stack;
		this.geom.setSize(16, 16);
	}
	
	public void setStack(ItemStack stack){
		this.stack = stack;
	}
	
	public ItemStack getStack(){
		return this.stack;
	}
	
	@Override
	public void draw(Point pos) {
		if (this.stack == null) return;
		
		GL11.glPushMatrix();
		GL11.glScalef(this.getSize().getX()/16.0f, this.getSize().getY()/16.0f, 1.0f);
		
        RenderHelper.enableGUIStandardItemLighting();
		GuiContainerManager.drawItem(pos.getX(), pos.getY(), this.stack);
		GL11.glPopMatrix();
	}

}
