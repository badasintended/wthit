package mcp.mobius.waila.api.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import mcp.mobius.waila.Constants;
import mcp.mobius.waila.mod_Waila;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.overlay.OverlayConfig;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;

public class ConfigHandler implements IWailaConfigHandler {

	private static ConfigHandler _instance = null;
	private ConfigHandler() { _instance = this; }
	public static ConfigHandler instance(){	return _instance == null ? new ConfigHandler() : _instance;	}	
	
	private LinkedHashMap<String, ConfigModule> modules = new LinkedHashMap<String, ConfigModule>();
	private ArrayList<String> serverconfigs             = new ArrayList<String>();
	public  Configuration config = null;	
	
	
	public void addModule(String modName, HashMap<String, String> options){
		this.addModule(modName, new ConfigModule(modName, options));
	}

	public void addModule(String modName, ConfigModule options){
		this.modules.put(modName, options);
	}	
	
	@Override
	public Set<String> getModuleNames(){
		return this.modules.keySet();
	}
	
	@Override
	public HashMap<String, String> getConfigKeys(String modName){
		if (this.modules.containsKey(modName))
			return this.modules.get(modName).options;
		else
			return null;
	}
	
	private void saveModuleKey(String modName, String key){
		config.get(Constants.CATEGORY_MODULES, key, true);
		config.get(Constants.CATEGORY_SERVER , key, Constants.SERVER_FREE);			
		config.save();		
	}
	
	public void addConfig(String modName, String key, String name){
		this.saveModuleKey(modName, key);
		
		if (!this.modules.containsKey(modName))
			this.modules.put(modName, new ConfigModule(modName));
		
		this.modules.get(modName).addOption(key, name);
	}

	public void addConfigServer(String modName, String key, String name){
		this.saveModuleKey(modName, key);
		
		if (!this.modules.containsKey(modName))
			this.modules.put(modName, new ConfigModule(modName));
		
		this.modules.get(modName).addOption(key, name);
		this.serverconfigs.add(key);
	}	
	
	@Override
	public boolean getConfig(String key){
		return this.getConfig(key, true);
	}	
	
	@Override
	public boolean getConfig(String key, boolean defvalue){
		if (this.serverconfigs.contains(key) && !mod_Waila.instance.serverPresent)
			return false;
		
		Property prop = config.get(Constants.CATEGORY_MODULES, key, defvalue);
		return prop.getBoolean(defvalue);		
	}
	
	public boolean isServerRequired(String key){
		return this.serverconfigs.contains(key);
	}

	
	
	
	
	
	/* GENERAL ACCESS METHODS TO GET/SET VALUES IN THE CONFIG FILE */
	
	public boolean getConfig(String category, String key, boolean default_){
		Property prop = config.get(category, key, default_);
		return prop.getBoolean(default_);		
	}	
	
	public void setConfig(String category, String key, boolean state){
		config.getCategory(category).put(key, new Property(key,String.valueOf(state),Property.Type.BOOLEAN));
		config.save();		
	}
	
	public int getConfig(String category, String key, int default_){
		Property prop = config.get(category, key, default_);
		return prop.getInt();		
	}	
	
	public void setConfig(String category, String key, int state){
		config.getCategory(category).put(key, new Property(key,String.valueOf(state),Property.Type.INTEGER));
		config.save();		
	}

	
	
	
	
	
	/* Default config loading */
	
	public void loadDefaultConfig(FMLPreInitializationEvent event){
		config = new Configuration(event.getSuggestedConfigurationFile());
	
		config.get(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_SHOW,     true);
		config.get(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_MODE,     true);
		config.get(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_LIQUID,   false);
		config.get(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_METADATA, false);
		config.get(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_KEYBIND,  true);
		
		OverlayConfig.posX = config.get(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_POSX,     5000).getInt();
		OverlayConfig.posY = config.get(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_POSY,     100).getInt();
	
		OverlayConfig.alpha =     config.get(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_ALPHA,     80).getInt();
		OverlayConfig.bgcolor =   config.get(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_BGCOLOR,   0x100010).getInt();
		OverlayConfig.gradient1 = config.get(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_GRADIENT1, 0x5000ff).getInt();
		OverlayConfig.gradient2 = config.get(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_GRADIENT2, 0x28007f).getInt();
		OverlayConfig.fontcolor = config.get(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_FONTCOLOR, 0xA0A0A0).getInt();
		OverlayConfig.scale     = config.get(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_SCALE,     100).getInt() / 100.0f;
		
		config.save();	
	}
}
