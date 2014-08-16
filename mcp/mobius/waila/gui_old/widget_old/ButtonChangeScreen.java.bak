package mcp.mobius.waila.gui_old.widget_old;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

public class ButtonChangeScreen extends GuiButton {

	GuiScreen linkedScreen;
	
	public ButtonChangeScreen(int par1, String par4Str, GuiScreen _linkedScreen) {
		super(par1, 0, 0, 80, 20, par4Str);
		this.linkedScreen = _linkedScreen;
	}	
	
	public ButtonChangeScreen(int par1, int par2, int par3, String par4Str, GuiScreen _linkedScreen) {
		super(par1, par2, par3, par4Str);
		this.linkedScreen = _linkedScreen;
	}

	public ButtonChangeScreen(int par1, int par2, int par3, int par4, int par5,	String par6Str, GuiScreen _linkedScreen) {
		super(par1, par2, par3, par4, par5, par6Str);
		this.linkedScreen = _linkedScreen;		
	}
	
	public void pressButton(){
		Minecraft mc = Minecraft.getMinecraft();
		mc.displayGuiScreen(this.linkedScreen);
	}
	
    public int getHeight(){
    	return this.height;
    }
    
    public int getWidth(){
    	return this.width;
    }

    public void setHeight(int value){
    	this.height = value;
    }
    
    public void setWidth(int value){
    	this.width = value;
    }    
}
