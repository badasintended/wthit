package mcp.mobius.waila.gui.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

public class Label extends BaseWidget {
	private FontRenderer fontrender;
	
	public Label(String label){
		this.label      = label;
		this.fontrender = Minecraft.getMinecraft().fontRenderer;
		this.width      = fontrender.getStringWidth(this.label);
	}

	public int getCharWidth(){ return this.fontrender.getCharWidth((char)32); }	
	
	@Override
	public void draw(int x, int y, int z) {
		this.renderString(x, y, 0, 0xffffffff);
	}
	
	public String getTrimLabel(){
		int maxchars = this.width / this.getCharWidth();
		return this.label.substring(0, Math.min(this.label.length(), maxchars));
	}
	
	public void renderString(int x, int y, int z, int color){
		//this.fontrender.drawString(this.getTrimLabel(), x, y, color);
		this.fontrender.drawString(this.getLabel(), x, y, color);
	}

}
