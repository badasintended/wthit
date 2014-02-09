package mcp.mobius.waila.api.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

//import au.com.bytecode.opencsv.CSVReader;
import net.minecraft.block.Block;
import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.IWailaBlockDecorator;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.IWailaRegistrar;
import mcp.mobius.waila.api.IWailaSummaryProvider;
import mcp.mobius.waila.cbcore.LangUtil;

public class ModuleRegistrar implements IWailaRegistrar {

	private static ModuleRegistrar instance = null;
	//public LinkedHashMap<Integer, ArrayList<IWailaDataProvider>> headProviders  = new LinkedHashMap<Integer, ArrayList<IWailaDataProvider>>();
	//public LinkedHashMap<Integer, ArrayList<IWailaDataProvider>> bodyProviders  = new LinkedHashMap<Integer, ArrayList<IWailaDataProvider>>();
	//public LinkedHashMap<Integer, ArrayList<IWailaDataProvider>> tailProviders  = new LinkedHashMap<Integer, ArrayList<IWailaDataProvider>>();	
	//public LinkedHashMap<Integer, ArrayList<IWailaDataProvider>> stackProviders = new LinkedHashMap<Integer, ArrayList<IWailaDataProvider>>();	

	public LinkedHashMap<Class, ArrayList<IWailaDataProvider>> headBlockProviders  = new LinkedHashMap<Class, ArrayList<IWailaDataProvider>>();
	public LinkedHashMap<Class, ArrayList<IWailaDataProvider>> bodyBlockProviders  = new LinkedHashMap<Class, ArrayList<IWailaDataProvider>>();
	public LinkedHashMap<Class, ArrayList<IWailaDataProvider>> tailBlockProviders  = new LinkedHashMap<Class, ArrayList<IWailaDataProvider>>();	
	public LinkedHashMap<Class, ArrayList<IWailaDataProvider>> stackBlockProviders = new LinkedHashMap<Class, ArrayList<IWailaDataProvider>>();	
	
	public LinkedHashMap<Class,   ArrayList<IWailaBlockDecorator>> blockClassDecorators = new LinkedHashMap<Class,   ArrayList<IWailaBlockDecorator>>();	
	
	public LinkedHashMap<String, LinkedHashMap <String, LinkedHashMap <String, String>>> wikiDescriptions = new LinkedHashMap<String, LinkedHashMap <String, LinkedHashMap <String, String>>>();

	public LinkedHashMap<Class, ArrayList<IWailaSummaryProvider>> summaryProviders = new LinkedHashMap<Class, ArrayList<IWailaSummaryProvider>>();
	
	private ModuleRegistrar() {
		instance = this;
	}

	public static ModuleRegistrar instance(){
		if (ModuleRegistrar.instance == null)
			ModuleRegistrar.instance = new ModuleRegistrar();
		return ModuleRegistrar.instance;
	}

	@Override
	public void addConfig(String modname, String key, String configname) {
		ConfigHandler.instance().addConfig(modname, key, LangUtil.translateG(configname));
	}

	@Override
	public void addConfigRemote(String modname, String key, String configname) {
		ConfigHandler.instance().addConfigServer(modname, key, LangUtil.translateG(configname));
	}	
	
	@Override
	public void addConfig(String modname, String key) {
		ConfigHandler.instance().addConfig(modname, key, LangUtil.translateG("option." + key));
	}

	@Override
	public void addConfigRemote(String modname, String key) {
		ConfigHandler.instance().addConfigServer(modname, key, LangUtil.translateG("option." + key));
	}	
	
	@Override
	public void registerHeadProvider(IWailaDataProvider dataProvider, Class block) {
		if (!this.headBlockProviders.containsKey(block))
			this.headBlockProviders.put(block, new ArrayList<IWailaDataProvider>());
		
		ArrayList<IWailaDataProvider> providers = this.headBlockProviders.get(block);
		if (providers.contains(dataProvider)) return;
		
		this.headBlockProviders.get(block).add(dataProvider);		
	}	
	
	@Override
	public void registerBodyProvider(IWailaDataProvider dataProvider, Class block) {
		if (!this.bodyBlockProviders.containsKey(block))
			this.bodyBlockProviders.put(block, new ArrayList<IWailaDataProvider>());
		
		ArrayList<IWailaDataProvider> providers = this.bodyBlockProviders.get(block);
		if (providers.contains(dataProvider)) return;		
		
		this.bodyBlockProviders.get(block).add(dataProvider);
	}	
	
	@Override
	public void registerTailProvider(IWailaDataProvider dataProvider, Class block) {
		if (!this.tailBlockProviders.containsKey(block))
			this.tailBlockProviders.put(block, new ArrayList<IWailaDataProvider>());
		
		ArrayList<IWailaDataProvider> providers = this.tailBlockProviders.get(block);
		if (providers.contains(dataProvider)) return;		
		
		this.tailBlockProviders.get(block).add(dataProvider);
	}		
	
	@Override
	public void registerStackProvider(IWailaDataProvider dataProvider, Class block) {
		if (!this.stackBlockProviders.containsKey(block))
			this.stackBlockProviders.put(block, new ArrayList<IWailaDataProvider>());
		
		ArrayList<IWailaDataProvider> providers = this.stackBlockProviders.get(block);
		if (providers.contains(dataProvider)) return;		
		
		this.stackBlockProviders.get(block).add(dataProvider);
	}		

	@Override
	public void registerShortDataProvider(IWailaSummaryProvider dataProvider, Class item) {
		if (!this.summaryProviders.containsKey(item))
			this.summaryProviders.put(item, new ArrayList<IWailaSummaryProvider>());
		this.summaryProviders.get(item).add(dataProvider);		
	}	
	
	@Override
	public void registerBlockDecorator(IWailaBlockDecorator decorator,	Class block) {
		if (!this.blockClassDecorators.containsKey(block))
			this.blockClassDecorators.put(block, new ArrayList<IWailaBlockDecorator>());
		this.blockClassDecorators.get(block).add(decorator);		
	}	

	public ArrayList<IWailaDataProvider> getHeadProviders(Object block) {
		ArrayList<IWailaDataProvider> returnList = new ArrayList<IWailaDataProvider>();
		for (Class clazz : this.headBlockProviders.keySet())
			if (clazz.isInstance(block))
				returnList.addAll(this.headBlockProviders.get(clazz));
				
		return returnList;
	}

	public ArrayList<IWailaDataProvider> getBodyProviders(Object block) {
		ArrayList<IWailaDataProvider> returnList = new ArrayList<IWailaDataProvider>();
		for (Class clazz : this.bodyBlockProviders.keySet())
			if (clazz.isInstance(block))
				returnList.addAll(this.bodyBlockProviders.get(clazz));
				
		return returnList;
	}	

	public ArrayList<IWailaDataProvider> getTailProviders(Object block) {
		ArrayList<IWailaDataProvider> returnList = new ArrayList<IWailaDataProvider>();
		for (Class clazz : this.tailBlockProviders.keySet())
			if (clazz.isInstance(block))
				returnList.addAll(this.tailBlockProviders.get(clazz));
				
		return returnList;
	}	

	public ArrayList<IWailaDataProvider> getStackProviders(Object block) {
		ArrayList<IWailaDataProvider> returnList = new ArrayList<IWailaDataProvider>();
		for (Class clazz : this.stackBlockProviders.keySet())
			if (clazz.isInstance(block))
				returnList.addAll(this.stackBlockProviders.get(clazz));
				
		return returnList;
	}	

	public ArrayList<IWailaSummaryProvider> getSummaryProvider(Object item){
		ArrayList<IWailaSummaryProvider> returnList = new ArrayList<IWailaSummaryProvider>();
		for (Class clazz : this.summaryProviders.keySet())
			if (clazz.isInstance(item))
				returnList.addAll(this.summaryProviders.get(clazz));
				
		return returnList;
	}	

	public ArrayList<IWailaBlockDecorator> getBlockDecorators(Object block){
		ArrayList<IWailaBlockDecorator> returnList = new ArrayList<IWailaBlockDecorator>();
		for (Class clazz : this.blockClassDecorators.keySet())
			if (clazz.isInstance(block))
				returnList.addAll(this.blockClassDecorators.get(clazz));
				
		return returnList;		
	}	
	
	public boolean hasStackProviders(Object block){
		for (Class clazz : this.stackBlockProviders.keySet())
			if (clazz.isInstance(block))
				return true;
		return false;
	}	
	
	public boolean hasHeadProviders(Object block){
		for (Class clazz : this.headBlockProviders.keySet())
			if (clazz.isInstance(block))
				return true;
		return false;
	}
	
	public boolean hasBodyProviders(Object block){
		for (Class clazz : this.bodyBlockProviders.keySet())
			if (clazz.isInstance(block))
				return true;
		return false;
	}

	public boolean hasTailProviders(Object block){
		for (Class clazz : this.tailBlockProviders.keySet())
			if (clazz.isInstance(block))
				return true;
		return false;
	}

	public boolean hasSummaryProvider(Class item){
		return this.summaryProviders.containsKey(item);
	}	
	
	public boolean hasBlockDecorator(Object block){
		for (Class clazz : this.blockClassDecorators.keySet())
			if (clazz.isInstance(block))
				return true;
		return false;
	}	
	
	/* ----------------- */
	@Override
	public void registerDocTextFile(String filename) {
	}	
	
	public boolean hasDocTextModID(String modid){
		return this.wikiDescriptions.containsKey(modid);
	}

	public boolean hasDocTextItem(String modid, String item){
		if (this.hasDocTextModID(modid))
			return this.wikiDescriptions.get(modid).containsKey(item);
		return false;
	}
	
	public boolean hasDocTextMeta(String modid, String item, String meta){
		if (this.hasDocTextItem(modid, item))
			return this.wikiDescriptions.get(modid).get(item).containsKey(meta);
		return false;		
	}	

	public LinkedHashMap<String, String> getDocText(String modid, String name){
		return this.wikiDescriptions.get(modid).get(name);
	}	
	
	public String getDocText(String modid, String name, String meta){
		return this.wikiDescriptions.get(modid).get(name).get(meta);
	}
	
	public boolean hasDocTextSpecificMeta(String modid, String name, String meta){
		for (String s : this.getDocText(modid, name).keySet())
			if (s.equals(meta))
				return true;
		return false;
	}
	
	public String getDoxTextWildcardMatch(String modid, String name){
		Set<String> keys = this.wikiDescriptions.get(modid).keySet();
		for (String s : keys){
			String regexed = s;
			regexed = regexed.replace(".", "\\.");
			regexed = regexed.replace("*", ".*");
			
			if (name.matches(s))
				return s;			
		}
		return null;
	}
}
