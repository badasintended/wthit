package mcp.mobius.waila.gui.widget;

import net.minecraft.client.gui.GuiScreen;

public class DraggableViewport extends BaseWidget {

	public DraggableViewport(GuiScreen parent){
    	this.parent = parent;
	}

	@Override
	public int getWidth() {
		return 0;
	}

	@Override
	public int getHeight() {
		return 0;
	}

	@Override
	public void draw() {
	}

}
