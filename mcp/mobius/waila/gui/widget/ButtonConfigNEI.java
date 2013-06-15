package mcp.mobius.waila.gui.widget;

import codechicken.nei.NEIClientConfig;

public class ButtonConfigNEI extends ButtonConfigOption {

	public ButtonConfigNEI(int par1, String _stateFalse, String _stateTrue,
			String _label, String _configKey) {
		super(par1, _stateFalse, _stateTrue, _label, _configKey);
	}

	public ButtonConfigNEI(int par1, int par2, int par3, String _stateFalse, String _stateTrue, String _label, int _labelSize, boolean _state,
			String _configKey) {
		super(par1, par2, par3, _stateFalse, _stateTrue, _label, _labelSize,
				_state, _configKey);
	}

	public ButtonConfigNEI(int par1, int par2, int par3, int par4, int par5,
			String _stateFalse, String _stateTrue, String _label,
			int _labelSize, boolean _state, String _configKey) {
		super(par1, par2, par3, par4, par5, _stateFalse, _stateTrue, _label,
				_labelSize, _state, _configKey);
	}

	@Override
	public void setupInitialState(String _configKey, boolean _state){
		this.configKey = _configKey;
		this.state = NEIClientConfig.getSetting(this.configKey).getBooleanValue();
		this.displayString = state ? stateTrue:stateFalse;		
	}	
	
	@Override
	public void pressButton(){
		super.pressButton();
		this.state = !NEIClientConfig.getSetting(this.configKey).getBooleanValue();
		NEIClientConfig.getSetting(this.configKey).setBooleanValue(this.state);		
	}	
	
}
