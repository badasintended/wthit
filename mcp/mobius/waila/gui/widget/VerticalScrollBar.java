package mcp.mobius.waila.gui.widget;

import net.minecraft.client.gui.GuiScreen;

public class VerticalScrollBar extends BaseWidget {

	private int maxvalue;
	private int currvalue = 0;
	private int buttonH;
	private int buttonTop;
	private int step = 6;
	
	public VerticalScrollBar(){};
	
	public void setup(GuiScreen parent, int posX, int posY, int width, int height, int buttonH, int maxvalue) {
		this.parent   = parent;
		this.posX     = posX;
		this.posY     = posY;
		this.width    = width;
		this.height   = height;
		this.maxvalue = maxvalue;
		this.buttonH = buttonH;
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
		this.buttonTop = (int)((this.getHeight() - this.buttonH) * currentRatio) + this.posY;

		this.parent.drawGradientRect(this.posX, 
									 buttonTop, 
									 this.posX + this.getWidth(), 
									 buttonTop + this.buttonH,
									 0xffffffff, 0xffffffff);

	}

	public void setCurrentValue(int currvalue){
		this.currvalue = currvalue;
	}

	public void addCurrentValue(int value){
		this.currvalue += value;
		this.currvalue = Math.max(0, this.currvalue);
		this.currvalue = Math.min(this.maxvalue, this.currvalue);

	}
	
	public int getCurrentValue(){
		return this.currvalue;
	}
	
	public int getStep(){
		return this.step;
	}
	
	@Override
	public boolean mouseClicked(int mouseX, int mouseY, int buttonID){
		if(mouseY < this.buttonTop)
			this.addCurrentValue(-1 * this.step);
		else if (mouseY > this.buttonTop + this.buttonH)
			this.addCurrentValue(this.step);			
		else
			System.out.printf("Clicked on button\n");
		return true;
	}	
	
}
