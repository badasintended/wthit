package mcp.mobius.waila.gui.widget;

import mcp.mobius.waila.mod_Waila;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;

public class ButtonConfigOption extends Button2States {

	String configKey;
	
	public ButtonConfigOption(int par1, String _stateFalse, String _stateTrue, String _label, String _configKey) {
		super(par1, 0, 0, 80, 20, _stateFalse, _stateTrue, _label, -1, false);
		this.setupInitialState(_configKey, true);
	}

	public ButtonConfigOption(int par1, String _stateFalse, String _stateTrue, String _label, String _configKey, boolean _state) {
		super(par1, 0, 0, 80, 20, _stateFalse, _stateTrue, _label, -1, _state);
		this.setupInitialState(_configKey, _state);
	}	
	
	public ButtonConfigOption(int par1, int par2, int par3, String _stateFalse, String _stateTrue, String _label, int _labelSize, boolean _state, String _configKey) {
		super(par1, par2, par3, _stateFalse, _stateTrue, _label, _labelSize, _state);
		this.setupInitialState(_configKey, true);
	}

	public ButtonConfigOption(int par1, int par2, int par3, int par4, int par5,	String _stateFalse, String _stateTrue, String _label, int _labelSize, boolean _state, String _configKey) {
		super(par1, par2, par3, par4, par5, _stateFalse, _stateTrue, _label, _labelSize, _state);
		this.setupInitialState(_configKey, true);
	}

	public void setupInitialState(String _configKey, boolean _state){
		this.configKey = _configKey;
		this.state = this.readConfigKey(this.configKey, _state);
		this.displayString = state ? stateTrue:stateFalse;		
	}
	
	@Override
	public void pressButton(){
		super.pressButton();
		this.writeConfigKey(this.configKey, this.state);
	}	

	protected void writeConfigKey(String keyname, boolean value){
		mod_Waila.instance.config.getCategory(Configuration.CATEGORY_GENERAL).put(keyname, new Property(keyname,String.valueOf(value),Property.Type.BOOLEAN));
		mod_Waila.instance.config.save();
	}		
	
	protected boolean readConfigKey(String keyname, boolean _state){
		mod_Waila.instance.config.load();
		Property prop = mod_Waila.instance.config.get(Configuration.CATEGORY_GENERAL, keyname, _state);
		return prop.getBoolean(_state);
	}
	
}
