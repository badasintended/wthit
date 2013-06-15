package mcp.mobius.waila.addons;

import java.util.ArrayList;
import java.util.HashMap;

import codechicken.nei.api.API;

import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.IWailaRegistrar;
import mcp.mobius.waila.handlers.HUDHandlerExternal;

public class ExternalModulesHandler implements IWailaRegistrar {

	private static ExternalModulesHandler instance = null;
	public HashMap<Integer, ArrayList<IWailaDataProvider>> headProviders  = new HashMap<Integer, ArrayList<IWailaDataProvider>>();
	public HashMap<Integer, ArrayList<IWailaDataProvider>> bodyProviders  = new HashMap<Integer, ArrayList<IWailaDataProvider>>();
	public HashMap<Integer, ArrayList<IWailaDataProvider>> stackProviders = new HashMap<Integer, ArrayList<IWailaDataProvider>>();	
	
	private ExternalModulesHandler() {
		instance = this;
	}

	public static ExternalModulesHandler instance(){
		if (ExternalModulesHandler.instance == null)
			ExternalModulesHandler.instance = new ExternalModulesHandler();
		return ExternalModulesHandler.instance;
	}

	@Override
	public void addConfig(String modname, String key, String configname) {
		ConfigHandler.instance().addConfig(modname, key, configname);
	}

	@Override
	public void registerHeadProvider(IWailaDataProvider dataProvider, int blockID) {
		if (!this.headProviders.containsKey(blockID))
			this.headProviders.put(blockID, new ArrayList<IWailaDataProvider>());
		this.headProviders.get(blockID).add(dataProvider);
	}

	@Override
	public void registerBodyProvider(IWailaDataProvider dataProvider, int blockID) {
		if (!this.bodyProviders.containsKey(blockID))
			this.bodyProviders.put(blockID, new ArrayList<IWailaDataProvider>());
		this.bodyProviders.get(blockID).add(dataProvider);
	}

	@Override
	public void registerStackProvider(IWailaDataProvider dataProvider, int blockID) {
		if (!this.stackProviders.containsKey(blockID))
			this.stackProviders.put(blockID, new ArrayList<IWailaDataProvider>());
		this.stackProviders.get(blockID).add(dataProvider);
		API.registerHighlightIdentifier(blockID, new HUDHandlerExternal());
	}	
	
	public ArrayList<IWailaDataProvider> getHeadProviders(int blockID) {
		return this.headProviders.get(blockID);
	}

	public ArrayList<IWailaDataProvider> getBodyProviders(int blockID) {
		return this.bodyProviders.get(blockID);
	}	

	public ArrayList<IWailaDataProvider> getStackProviders(int blockID) {
		return this.stackProviders.get(blockID);
	}	
	
	public boolean hasHeadProviders(int blockID){
		return this.headProviders.containsKey(blockID);
	}
	
	public boolean hasBodyProviders(int blockID){
		return this.bodyProviders.containsKey(blockID);
	}
	
	public boolean hasStackProviders(int blockID){
		return this.stackProviders.containsKey(blockID);
	}	
}
