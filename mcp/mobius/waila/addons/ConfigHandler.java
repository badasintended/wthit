package mcp.mobius.waila.addons;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;

import mcp.mobius.waila.mod_Waila;
import mcp.mobius.waila.api.IWailaConfigHandler;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;

public class ConfigHandler implements IWailaConfigHandler {

	private static ConfigHandler instance = null;
	private LinkedHashMap<String, ConfigModule> modules = new LinkedHashMap<String, ConfigModule>();
	private ArrayList<String> serverconfigs = new ArrayList<String>();
	
	private ConfigHandler() {
		instance = this;
	}

	public static ConfigHandler instance(){
		if (ConfigHandler.instance == null)
			ConfigHandler.instance = new ConfigHandler();
		return ConfigHandler.instance;
	}
	
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
	
	public void addConfig(String modName, String key, String name){
		if (!this.modules.containsKey(modName))
			this.modules.put(modName, new ConfigModule(modName));
		
		this.modules.get(modName).addOption(key, name);
	}

	public void addConfigServer(String modName, String key, String name){
		if (!this.modules.containsKey(modName))
			this.modules.put(modName, new ConfigModule(modName));
		
		this.modules.get(modName).addOption(key, name);
		this.serverconfigs.add(key);
	}	
	
	@Override
	public boolean getConfig(String key, boolean defvalue){
		mod_Waila.instance.config.load();
		
		if (this.serverconfigs.contains(key) && !mod_Waila.instance.serverPresent)
			return false;
		
		Property prop = mod_Waila.instance.config.get(Configuration.CATEGORY_GENERAL, key, defvalue);
		return prop.getBoolean(defvalue);		
	}
	
	public boolean isServerRequired(String key){
		return this.serverconfigs.contains(key);
	}
	
	@Override
	public boolean getConfig(String key){
		return this.getConfig(key, true);
	}
	
	public int getConfigInt(String key){
		mod_Waila.instance.config.load();
		Property prop = mod_Waila.instance.config.get(Configuration.CATEGORY_GENERAL, key, 0);
		return prop.getInt();			
	}
	
	@Override
	public void setConfig(String key, boolean value){
		mod_Waila.instance.config.load();
		mod_Waila.instance.config.get(Configuration.CATEGORY_GENERAL, key, true).set(value);;
		mod_Waila.instance.config.save();
	}
	
	public void setConfigInt(String key, int value){
		mod_Waila.instance.config.load();
		mod_Waila.instance.config.get(Configuration.CATEGORY_GENERAL, key, 0).set(value);;
		mod_Waila.instance.config.save();		
	}
}
