package mcp.mobius.waila;

import java.util.HashMap;
import java.util.Set;

import mcp.mobius.waila.api.IConfigHandler;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;

public class ConfigHandler implements IConfigHandler {

	private static ConfigHandler instance = null;
	private HashMap<String, ConfigModule> modules = new HashMap<String, ConfigModule>();
	
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
	
	@Override
	public boolean getConfig(String key){
		mod_Waila.instance.config.load();
		Property prop = mod_Waila.instance.config.get(Configuration.CATEGORY_GENERAL, key, true);
		return prop.getBoolean(true);		
	}
}
