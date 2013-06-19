package mcp.mobius.waila.gui.widget;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;

public class Button2States extends GuiButton {

	boolean state;
	String  label;
	int     labelSize;
	String  stateFalse;
	String  stateTrue;
	
	public Button2States(int par1, String _stateFalse, String _stateTrue, String _label, int _labelSize, boolean _state) {
		super(par1, 0, 0, 80, 20, _stateFalse);
		state = _state;
		label = _label;
		stateFalse = _stateFalse;
		stateTrue  = _stateTrue;
		labelSize  = _labelSize;
		this.displayString = state ? stateTrue:stateFalse;
	}	
	
	public Button2States(int par1, int par2, int par3, String _stateFalse, String _stateTrue, String _label, int _labelSize, boolean _state) {
		super(par1, par2, par3, _stateFalse);
		state = _state;
		label = _label;
		stateFalse = _stateFalse;
		stateTrue  = _stateTrue;
		labelSize  = _labelSize;
		this.displayString = state ? stateTrue:stateFalse;
	}

	public Button2States(int par1, int par2, int par3, int par4, int par5, String _stateFalse, String _stateTrue, String _label, int _labelSize, boolean _state) {
		super(par1, par2, par3, par4, par5, _stateFalse);
		state = _state;
		label = _label;
		stateFalse = _stateFalse;
		stateTrue  = _stateTrue;
		labelSize  = _labelSize;
		this.displayString = state ? stateTrue:stateFalse;		
	}

    @Override
	public void drawButton(Minecraft par1Minecraft, int par2, int par3)
    {
        if (this.drawButton)
        {
            FontRenderer fontrenderer = par1Minecraft.fontRenderer;
            par1Minecraft.renderEngine.bindTexture("/gui/gui.png");
        	
        	if (this.labelSize == -1)
        		labelSize = fontrenderer.getStringWidth(this.label) + 5;
        	

            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.field_82253_i = par2 >= this.xPosition + this.labelSize && par3 >= this.yPosition && par2 < this.xPosition + this.width  + this.labelSize && par3 < this.yPosition + this.height;
            int k = this.getHoverState(this.field_82253_i);
            this.drawTexturedModalRect(this.xPosition + this.labelSize, this.yPosition, 0, 46 + k * 20, this.width / 2, this.height);
            this.drawTexturedModalRect(this.xPosition + this.labelSize + this.width / 2, this.yPosition, 200 - this.width / 2, 46 + k * 20, this.width / 2, this.height);
            this.mouseDragged(par1Minecraft, par2, par3);
            int l = 14737632;

            if (!this.enabled)
            {
                l = -6250336;
            }
            else if (this.field_82253_i)
            {
                l = 16777120;
            }

            this.drawCenteredString(fontrenderer, this.displayString, this.xPosition  + this.labelSize + this.width / 2, this.yPosition + (this.height - 8) / 2, l);
            this.drawCenteredString(fontrenderer, this.label, this.xPosition + this.labelSize/2, this.yPosition + (this.height - 8) / 2, 14737632);
        }
    }	
	
	public void pressButton(){
		state = state?false:true;
		this.displayString = state ? stateTrue:stateFalse;
	}	
	
	public boolean getState(){
		return state;
	}
	
    @Override
	public boolean mousePressed(Minecraft par1Minecraft, int par2, int par3)
    {
        return this.enabled && this.drawButton && par2 >= this.xPosition + this.labelSize && par3 >= this.yPosition && par2 < this.xPosition + this.labelSize + this.width && par3 < this.yPosition + this.height;
    }	
	
    public int getHeight(){
    	return this.height;
    }
    
    public int getWidth(){
    	return this.width  + this.labelSize;
    }
    
    public void setHeight(int value){
    	this.height = value;
    }
    
    public void setWidth(int value){
    	this.width = value;
    }      
    
}
