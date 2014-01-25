package mcp.mobius.waila.gui_old.widget_old;

import mcp.mobius.waila.mod_Waila;
import net.minecraft.client.Minecraft;

public class ButtonConfigRemote extends ButtonConfigOption {

	public ButtonConfigRemote(int par1, String _stateFalse, String _stateTrue,	String _label, String _configKey) {
		super(par1, _stateFalse, _stateTrue, _label, _configKey);
	}
	public ButtonConfigRemote(int par1, String _stateFalse, String _stateTrue,	String _label, String _configKey, boolean _state) {
		super(par1, _stateFalse, _stateTrue, _label, _configKey, _state);
	}
	public ButtonConfigRemote(int par1, int par2, int par3, String _stateFalse,	String _stateTrue, String _label, int _labelSize, boolean _state, String _configKey) {
		super(par1, par2, par3, _stateFalse, _stateTrue, _label, _labelSize, _state, _configKey);
	}

	public ButtonConfigRemote(int par1, int par2, int par3, int par4, int par5,	String _stateFalse, String _stateTrue, String _label, int _labelSize, boolean _state, String _configKey) {
		super(par1, par2, par3, par4, par5, _stateFalse, _stateTrue, _label, _labelSize, _state, _configKey);
	}

    @Override
	public void drawButton(Minecraft par1Minecraft, int par2, int par3)
    {
    	this.enabled = mod_Waila.instance.serverPresent;
    	super.drawButton(par1Minecraft, par2, par3);
    }

    @Override
	public boolean mousePressed(Minecraft par1Minecraft, int par2, int par3)
    {
    	this.enabled = mod_Waila.instance.serverPresent;
    	return super.mousePressed(par1Minecraft, par2, par3);
    }	

    @Override
    protected int getHoverState(boolean par1)
    {
    	this.enabled = mod_Waila.instance.serverPresent;
    	return super.getHoverState(par1);
    }    
    
}
