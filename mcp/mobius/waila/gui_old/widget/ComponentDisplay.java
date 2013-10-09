package mcp.mobius.waila.gui_old.widget;

import java.util.ArrayList;

import mcp.mobius.waila.handlers.CraftingTreeNode;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemStack;

public class ComponentDisplay extends BaseWidget {

	ArrayList<ItemStack> components = null;
	CraftingTreeNode node = null;
	int spacingX = 4;
	int spacingY = 4;
	int stackDisplayWidth = 0;
	boolean drawOverlay = false;
	
	public ComponentDisplay(GuiScreen parent){
		this.parent = parent;
		this.components = new ArrayList<ItemStack>();
	};

	public ComponentDisplay(GuiScreen parent, CraftingTreeNode node){
		this.parent = parent;
		this.node   = node;
		this.setComponents(node.elements);
	};	

	public void setDrawOverlay(boolean value){
		for(IWidget widget : this.getWidgets())
			if(widget instanceof StackDisplay)
				((StackDisplay)widget).setDrawOverlay(value);
	}	
	
	public void setComponents(ArrayList<ItemStack> components){
		this.components = components;
		this.stackDisplayWidth = 0;

		for (ItemStack component : components)
			this.addWidget(String.format("widget_%03d", this.getWidgets().size()), new StackDisplay(this.parent, component));
		
		for (IWidget widget:this.getWidgets())
			this.stackDisplayWidth = Math.max(this.stackDisplayWidth, widget.getWidth());
	}
	
	@Override
	public int getWidth() {
		int width = 0;
		for (IWidget widget:this.getWidgets())
			width += widget.getWidth();
		width += (this.getWidgets().size() + 1) * this.spacingX;
		return width;
	}

	@Override
	public int getHeight() {
		int height = 0;
		for (IWidget widget:this.getWidgets())
			height = Math.max(widget.getHeight(), height);
		height += this.spacingY * 2;
		return height;
	}

	@Override
	public void draw() {
		//this.drawBackground();
		int index = 0;
		for (IWidget widget: this.getWidgets()){
			widget.setPos(this.posX + (this.spacingX * (index + 1)) + ( index * this.stackDisplayWidth ), this.posY + this.spacingY, 0);
			widget.draw();
			index += 1;
		}
	}

}
