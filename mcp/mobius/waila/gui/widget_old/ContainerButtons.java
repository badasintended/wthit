package mcp.mobius.waila.gui.widget_old;

import java.util.ArrayList;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

public class ContainerButtons {

	GuiScreen parentScreen;
	int posX, posY, width, height;
	int maxLabelSize   = 0;
	int maxColumnWidth = 0;
	
	ArrayList<GuiButton> buttons = new ArrayList<GuiButton>(); 
	
	public ContainerButtons(GuiScreen _parentScreen, int _posX, int _posY, int _width, int _height) {
		this.parentScreen = _parentScreen;
		this.posX = _posX;   this.posY = _posY; 
		this.width = _width; this.height = _height;
		this.maxColumnWidth = this.width/2;
	}

	public void addButton(GuiButton button){
		
		this.buttons.add(button);
		
		/*
        FontRenderer fontrenderer = Minecraft.getMinecraft().fontRenderer;
        if (button instanceof Button2States){
        	maxLabelSize   = Math.max(maxLabelSize,   fontrenderer.getStringWidth(((Button2States)button).label) + 5);
        	((Button2States)button).labelSize = maxLabelSize;
        	maxColumnWidth = Math.max(maxColumnWidth, ((Button2States)button).getWidth());
        }
        */

		/*
        for (GuiButton other : this.buttons){
        	if (other instanceof Button2States)
        		((Button2States) other).labelSize = this.maxLabelSize;
        	
        	if (other instanceof ButtonChangeScreen)
        		((ButtonChangeScreen) other).setWidth(maxColumnWidth);
        }
        */       
        
		if (button instanceof Button2States){
			((Button2States) button).setWidth(80);
			((Button2States) button).labelSize = this.width/2 - 80;
	        button.xPosition = (this.buttons.size() - 1)%2 * this.width / 2 + this.posX;
	        button.yPosition = (this.buttons.size() - 1)/2 * 20 + this.posY;			
		}
		
		if (button instanceof ButtonChangeScreen){
			//((ButtonChangeScreen) button).setWidth(this.width/2);
			((ButtonChangeScreen) button).setWidth(120);
	        button.xPosition = (this.buttons.size() - 1)%2 * this.width / 2 + this.posX + (this.width / 2 - ((ButtonChangeScreen) button).getWidth())/2;
	        button.yPosition = (this.buttons.size() - 1)/2 * 20 + this.posY;			
		}		
		
		

        
        this.parentScreen.buttonList.add(button);

	}
	
}
