package mcp.mobius.waila.gui.widget;

import net.minecraft.client.gui.GuiScreen;

public class VerticalScrollBar extends BaseWidget {

	private int maxvalue;
	private int currvalue;
	private int buttonheight;
	
	public VerticalScrollBar(){};
	
	public void setup(GuiScreen parent, int posX, int posY, int width, int height, int buttonH, int maxvalue) {
		this.parent   = parent;
		this.posX     = posX;
		this.posY     = posY;
		this.width    = width;
		this.height   = height;
		this.maxvalue = maxvalue;
		this.buttonheight = buttonH;
	}

	@Override
	public int getWidth() {
		return this.width;
	}

	@Override
	public int getHeight() {
		return this.height;
	}

	@Override
	public void draw() {
		this.parent.drawGradientRect(this.posX, 
                					 this.posY, 
                					 this.posX + this.getWidth(),
                					 this.posY + this.getHeight(),
                					 0xff999999, 0xff999999);

		float currentRatio = (float)(this.currvalue) / (float)(this.maxvalue);
		float currentTop   = (this.getHeight() - this.buttonheight) * currentRatio;

		this.parent.drawGradientRect(this.posX, 
									 (int)(this.posY + currentTop), 
									 this.posX + this.getWidth(), 
									 (int)(this.posY + currentTop + this.buttonheight),
									 0xffffffff, 0xffffffff);

	}

	public void setCurrentValue(int currvalue){
		this.currvalue = currvalue;
	}
	
}
