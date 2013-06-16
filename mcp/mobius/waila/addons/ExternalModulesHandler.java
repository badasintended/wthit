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
	public HashMap<Integer, ArrayList<IWailaDataProvider>> tailProviders  = new HashMap<Integer, ArrayList<IWailaDataProvider>>();	
	public HashMap<Integer, ArrayList<IWailaDataProvider>> stackProviders = new HashMap<Integer, ArrayList<IWailaDataProvider>>();	

	public HashMap<Class, ArrayList<IWailaDataProvider>> headBlockProviders  = new HashMap<Class, ArrayList<IWailaDataProvider>>();
	public HashMap<Class, ArrayList<IWailaDataProvider>> bodyBlockProviders  = new HashMap<Class, ArrayList<IWailaDataProvider>>();
	public HashMap<Class, ArrayList<IWailaDataProvider>> tailBlockProviders  = new HashMap<Class, ArrayList<IWailaDataProvider>>();	
	public HashMap<Class, ArrayList<IWailaDataProvider>> stackBlockProviders = new HashMap<Class, ArrayList<IWailaDataProvider>>();	
	
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
	public void registerHeadProvider(IWailaDataProvider dataProvider, Class block) {
		if (!this.headBlockProviders.containsKey(block))
			this.headBlockProviders.put(block, new ArrayList<IWailaDataProvider>());
		this.headBlockProviders.get(block).add(dataProvider);		
	}	
	
	@Override
	public void registerBodyProvider(IWailaDataProvider dataProvider, int blockID) {
		if (!this.bodyProviders.containsKey(blockID))
			this.bodyProviders.put(blockID, new ArrayList<IWailaDataProvider>());
		this.bodyProviders.get(blockID).add(dataProvider);
	}

	@Override
	public void registerBodyProvider(IWailaDataProvider dataProvider, Class block) {
		if (!this.bodyBlockProviders.containsKey(block))
			this.bodyBlockProviders.put(block, new ArrayList<IWailaDataProvider>());
		this.bodyBlockProviders.get(block).add(dataProvider);
	}	
	
	public void registerTailProvider(IWailaDataProvider dataProvider, int blockID) {
		if (!this.tailProviders.containsKey(blockID))
			this.tailProviders.put(blockID, new ArrayList<IWailaDataProvider>());
		this.tailProviders.get(blockID).add(dataProvider);
	}	

	public void registerTailProvider(IWailaDataProvider dataProvider, Class block) {
		if (!this.tailBlockProviders.containsKey(block))
			this.tailBlockProviders.put(block, new ArrayList<IWailaDataProvider>());
		this.tailBlockProviders.get(block).add(dataProvider);
	}		
	
	@Override
	public void registerStackProvider(IWailaDataProvider dataProvider, int blockID) {
		if (!this.stackProviders.containsKey(blockID))
			this.stackProviders.put(blockID, new ArrayList<IWailaDataProvider>());
		this.stackProviders.get(blockID).add(dataProvider);
		API.registerHighlightIdentifier(blockID, new HUDHandlerExternal());
	}	

	/* Arrays getters */
	
	public ArrayList<IWailaDataProvider> getHeadProviders(int blockID) {
		return this.headProviders.get(blockID);
	}

	public ArrayList<IWailaDataProvider> getBodyProviders(int blockID) {
		return this.bodyProviders.get(blockID);
	}	

	public ArrayList<IWailaDataProvider> getTailProviders(int blockID) {
		return this.tailProviders.get(blockID);
	}		

	public ArrayList<IWailaDataProvider> getHeadProviders(Class block) {
		return this.headBlockProviders.get(block);
	}

	public ArrayList<IWailaDataProvider> getBodyProviders(Class block) {
		return this.bodyBlockProviders.get(block);
	}	

	public ArrayList<IWailaDataProvider> getTailProviders(Class block) {
		return this.tailBlockProviders.get(block);
	}	
	
	public ArrayList<IWailaDataProvider> getStackProviders(int blockID) {
		return this.stackProviders.get(blockID);
	}	
	
	/* Providers querry methods */
	
	public boolean hasHeadProviders(int blockID){
		return this.headProviders.containsKey(blockID);
	}
	
	public boolean hasBodyProviders(int blockID){
		return this.bodyProviders.containsKey(blockID);
	}

	public boolean hasTailProviders(int blockID){
		return this.tailProviders.containsKey(blockID);
	}	
	
	public boolean hasHeadProviders(Class block){
		return this.headBlockProviders.containsKey(block);
	}
	
	public boolean hasBodyProviders(Class block){
		return this.bodyBlockProviders.containsKey(block);
	}

	public boolean hasTailProviders(Class block){
		return this.tailBlockProviders.containsKey(block);
	}		
	
	public boolean hasStackProviders(int blockID){
		return this.stackProviders.containsKey(blockID);
	}

}
