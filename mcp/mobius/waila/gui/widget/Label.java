package mcp.mobius.waila.gui.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;

public class Label extends BaseWidget {

	public Label(GuiScreen parent){
		this.parent = parent;
	}

	public Label(GuiScreen parent, String label){
		this.parent = parent;
		this.setLabel(label);
	}	

	public Label(String label){
		this.parent = null;
		this.setLabel(label);
	}	
	
	public int getCharWidth(){ 
		return this.fontRenderer.getCharWidth((char)32); 
	}	
	
	@Override
	public void draw() {
		this.renderString(this.posX, this.posY, 0, 0xffffffff);
	}
	
	public String getTrimLabel(){
		int maxchars = this.width / this.getCharWidth();
		return this.label.substring(0, Math.min(this.label.length(), maxchars));
	}

	@Override
	public int getWidth(){
		return this.fontRenderer.getStringWidth(this.label);
	}
	
	@Override
	public void setLabel(String label){
		this.label = label;
		this.width = this.fontRenderer.getStringWidth(this.label);
	}
	
	public void renderString(int x, int y, int z, int color){
		//this.fontrender.drawString(this.getTrimLabel(), x, y, color);
		this.fontRenderer.drawString(this.getLabel(), x, y, color);
	}

	@Override
	public int getHeight() {
		return 8;
	}

}
