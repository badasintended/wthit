package mcp.mobius.waila.api.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;

import mcp.mobius.waila.Constants;
import mcp.mobius.waila.mod_Waila;
import mcp.mobius.waila.api.IWailaConfigHandler;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;

public class ConfigHandler implements IWailaConfigHandler {

	private static ConfigHandler _instance = null;
	private ConfigHandler() { _instance = this; }
	public static ConfigHandler instance(){	return _instance == null ? new ConfigHandler() : _instance;	}	
	
	private LinkedHashMap<String, ConfigModule> modules = new LinkedHashMap<String, ConfigModule>();
	private ArrayList<String> serverconfigs             = new ArrayList<String>();
	
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
		mod_Waila.instance.config.get(Constants.CATEGORY_MODULES, key, true);
		mod_Waila.instance.config.get(Constants.CATEGORY_SERVER , key, Constants.SERVER_FREE);			
		mod_Waila.instance.config.save();		
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
		
		Property prop = mod_Waila.instance.config.get(Constants.CATEGORY_MODULES, key, defvalue);
		return prop.getBoolean(defvalue);		
	}
	
	public boolean isServerRequired(String key){
		return this.serverconfigs.contains(key);
	}

	/* GENERAL ACCESS METHODS TO GET/SET VALUES IN THE CONFIG FILE */
	
	public boolean getConfig(String category, String key, boolean default_){
		Property prop = mod_Waila.instance.config.get(category, key, default_);
		return prop.getBoolean(default_);		
	}	
	
	public void setConfig(String category, String key, boolean state){
		mod_Waila.instance.config.getCategory(category).put(key, new Property(key,String.valueOf(state),Property.Type.BOOLEAN));
		mod_Waila.instance.config.save();		
	}
	
	public int getConfig(String category, String key, int default_){
		Property prop = mod_Waila.instance.config.get(category, key, default_);
		return prop.getInt();		
	}	
	
	public void setConfig(String category, String key, int state){
		mod_Waila.instance.config.getCategory(category).put(key, new Property(key,String.valueOf(state),Property.Type.INTEGER));
		mod_Waila.instance.config.save();		
	}	
}
