package mcp.mobius.waila.api.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import au.com.bytecode.opencsv.CSVReader;
import net.minecraft.block.Block;
import codechicken.lib.lang.LangUtil;
import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.IWailaBlockDecorator;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.IWailaEntityProvider;
import mcp.mobius.waila.api.IWailaRegistrar;
import mcp.mobius.waila.api.IWailaSummaryProvider;

public class ModuleRegistrar implements IWailaRegistrar {

	private static ModuleRegistrar instance = null;

	public LinkedHashMap<Class, ArrayList<IWailaDataProvider>> headBlockProviders  = new LinkedHashMap<Class, ArrayList<IWailaDataProvider>>();
	public LinkedHashMap<Class, ArrayList<IWailaDataProvider>> bodyBlockProviders  = new LinkedHashMap<Class, ArrayList<IWailaDataProvider>>();
	public LinkedHashMap<Class, ArrayList<IWailaDataProvider>> tailBlockProviders  = new LinkedHashMap<Class, ArrayList<IWailaDataProvider>>();	
	public LinkedHashMap<Class, ArrayList<IWailaDataProvider>> stackBlockProviders = new LinkedHashMap<Class, ArrayList<IWailaDataProvider>>();	

	public LinkedHashMap<Class, ArrayList<IWailaBlockDecorator>> blockClassDecorators = new LinkedHashMap<Class,   ArrayList<IWailaBlockDecorator>>();
	
	public LinkedHashMap<Class, ArrayList<IWailaEntityProvider>> headEntityProviders      = new LinkedHashMap<Class, ArrayList<IWailaEntityProvider>>();
	public LinkedHashMap<Class, ArrayList<IWailaEntityProvider>> bodyEntityProviders      = new LinkedHashMap<Class, ArrayList<IWailaEntityProvider>>();
	public LinkedHashMap<Class, ArrayList<IWailaEntityProvider>> tailEntityProviders      = new LinkedHashMap<Class, ArrayList<IWailaEntityProvider>>();
	public LinkedHashMap<Class, ArrayList<IWailaEntityProvider>> overrideEntityProviders  = new LinkedHashMap<Class, ArrayList<IWailaEntityProvider>>();	
	
	public LinkedHashMap<Class, HashSet<String>> syncedNBTKeys = new LinkedHashMap<Class, HashSet<String>>();
	
	
	
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

	/* CONFIG HANDLING */
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
	
	
	/* REGISTRATION METHODS */
	@Override
	@Deprecated
	public void registerHeadProvider(IWailaDataProvider dataProvider, int blockID) {
		this.registerHeadProvider(dataProvider, Block.blocksList[blockID].getClass());
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
	@Deprecated
	public void registerBodyProvider(IWailaDataProvider dataProvider, int blockID) {
		this.registerBodyProvider(dataProvider, Block.blocksList[blockID].getClass());		
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
	@Deprecated
	public void registerTailProvider(IWailaDataProvider dataProvider, int blockID) {
		this.registerTailProvider(dataProvider, Block.blocksList[blockID].getClass());			
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
	@Deprecated
	public void registerStackProvider(IWailaDataProvider dataProvider, int blockID) {
		this.registerStackProvider(dataProvider, Block.blocksList[blockID].getClass());
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
	@Deprecated
	public void registerBlockDecorator(IWailaBlockDecorator decorator, int blockID) {
		this.registerBlockDecorator(decorator, Block.blocksList[blockID].getClass());	
	}

	@Override
	public void registerBlockDecorator(IWailaBlockDecorator decorator,	Class block) {
		if (!this.blockClassDecorators.containsKey(block))
			this.blockClassDecorators.put(block, new ArrayList<IWailaBlockDecorator>());
		this.blockClassDecorators.get(block).add(decorator);		
	}	

	
	@Override
	public void registerHeadProvider(IWailaEntityProvider dataProvider, Class entity) {
		if (!this.headEntityProviders.containsKey(entity))
			this.headEntityProviders.put(entity, new ArrayList<IWailaEntityProvider>());
		
		ArrayList<IWailaEntityProvider> providers = this.headEntityProviders.get(entity);
		if (providers.contains(dataProvider)) return;
		
		this.headEntityProviders.get(entity).add(dataProvider);		
	}	

	@Override
	public void registerBodyProvider(IWailaEntityProvider dataProvider, Class entity) {
		if (!this.bodyEntityProviders.containsKey(entity))
			this.bodyEntityProviders.put(entity, new ArrayList<IWailaEntityProvider>());
		
		ArrayList<IWailaEntityProvider> providers = this.bodyEntityProviders.get(entity);
		if (providers.contains(dataProvider)) return;		
		
		this.bodyEntityProviders.get(entity).add(dataProvider);
	}	
	
	@Override
	public void registerTailProvider(IWailaEntityProvider dataProvider, Class entity) {
		if (!this.tailEntityProviders.containsKey(entity))
			this.tailEntityProviders.put(entity, new ArrayList<IWailaEntityProvider>());
		
		ArrayList<IWailaEntityProvider> providers = this.tailEntityProviders.get(entity);
		if (providers.contains(dataProvider)) return;		
		
		this.tailEntityProviders.get(entity).add(dataProvider);
	}	
	
	@Override
	public void registerOverrideEntityProvider (IWailaEntityProvider dataProvider, Class entity){
		if (!this.overrideEntityProviders.containsKey(entity))
			this.overrideEntityProviders.put(entity, new ArrayList<IWailaEntityProvider>());
		
		ArrayList<IWailaEntityProvider> providers = this.overrideEntityProviders.get(entity);
		if (providers.contains(dataProvider)) return;		
		
		this.overrideEntityProviders.get(entity).add(dataProvider);		
	}
	
	
	@Override
	public void registerSyncedNBTKey(String key, Class target){
		if (!this.syncedNBTKeys.containsKey(target))
			this.syncedNBTKeys.put(target, new HashSet<String>());
		
		this.syncedNBTKeys.get(target).add(key);		
	}	
	
	
	
	/* PROVIDER GETTERS */
	
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
	
	public ArrayList<IWailaEntityProvider> getHeadEntityProviders(Object entity) {
		ArrayList<IWailaEntityProvider> returnList = new ArrayList<IWailaEntityProvider>();
		for (Class clazz : this.headEntityProviders.keySet())
			if (clazz.isInstance(entity))
				returnList.addAll(this.headEntityProviders.get(clazz));
				
		return returnList;
	}

	public ArrayList<IWailaEntityProvider> getBodyEntityProviders(Object entity) {
		ArrayList<IWailaEntityProvider> returnList = new ArrayList<IWailaEntityProvider>();
		for (Class clazz : this.bodyEntityProviders.keySet())
			if (clazz.isInstance(entity))
				returnList.addAll(this.bodyEntityProviders.get(clazz));
				
		return returnList;
	}	

	public ArrayList<IWailaEntityProvider> getTailEntityProviders(Object entity) {
		ArrayList<IWailaEntityProvider> returnList = new ArrayList<IWailaEntityProvider>();
		for (Class clazz : this.tailEntityProviders.keySet())
			if (clazz.isInstance(entity))
				returnList.addAll(this.tailEntityProviders.get(clazz));
				
		return returnList;
	}		
	
	public ArrayList<IWailaEntityProvider> getOverrideEntityProviders(Object entity) {
		ArrayList<IWailaEntityProvider> returnList = new ArrayList<IWailaEntityProvider>();
		for (Class clazz : this.overrideEntityProviders.keySet())
			if (clazz.isInstance(entity))
				returnList.addAll(this.overrideEntityProviders.get(clazz));
				
		return returnList;
	}	
	
	public HashSet<String> getSyncedNBTKeys(Object target){
		HashSet<String> returnList = new HashSet<String>();
		for (Class clazz : this.syncedNBTKeys.keySet())
			if (clazz.isInstance(target))
				returnList.addAll(this.syncedNBTKeys.get(clazz));
				
		return returnList;		
	}
	
	/* HAS METHODS */
	
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
	
	public boolean hasHeadEntityProviders(Object entity){
		for (Class clazz : this.headEntityProviders.keySet())
			if (clazz.isInstance(entity))
				return true;
		return false;
	}
	
	public boolean hasBodyEntityProviders(Object entity){
		for (Class clazz : this.bodyEntityProviders.keySet())
			if (clazz.isInstance(entity))
				return true;
		return false;
	}

	public boolean hasTailEntityProviders(Object entity){
		for (Class clazz : this.tailEntityProviders.keySet())
			if (clazz.isInstance(entity))
				return true;
		return false;
	}	
	
	public boolean hasOverrideEntityProviders(Object entity){
		for (Class clazz : this.overrideEntityProviders.keySet())
			if (clazz.isInstance(entity))
				return true;
		return false;
	}		
	
	public boolean hasSyncedNBTKeys(Object target){
		for (Class clazz : this.syncedNBTKeys.keySet())
			if (clazz.isInstance(target))
				return true;
		return false;
	}		
	
	/* ----------------- */
	@Override
	public void registerDocTextFile(String filename) {
		List<String[]> docData  = null;
		int    nentries = 0;
		
		
		try{
			docData = this.readFileAsString(filename);
		} catch (IOException e){
			Waila.log.log(Level.WARNING, String.format("Error while accessing file %s : %s", filename, e));
			return;
		}

		for (String[] ss : docData){
			String modid  = ss[0];
			String name   = ss[1];
			String meta   = ss[2];
			String desc   = ss[5].replace('$', '\n');
			if (!(desc.trim().equals(""))){
				if (!this.wikiDescriptions.containsKey(modid))
					this.wikiDescriptions.put(modid, new LinkedHashMap <String, LinkedHashMap <String, String>>());
				if (!this.wikiDescriptions.get(modid).containsKey(name))
					this.wikiDescriptions.get(modid).put(name, new LinkedHashMap<String, String>());
				
				this.wikiDescriptions.get(modid).get(name).put(meta, desc);
				System.out.printf("Registered %s %s %s\n", modid, name, meta);
				nentries += 1;			
			}
		}
		
		/*
		String[] sections = docData.split(">>>>");
		for (String s : sections){
			s.trim();
			if (!s.equals("")){
				try{
					String name   = s.split("\r?\n",2)[0].trim();
					String desc   = s.split("\r?\n",2)[1].trim();
					if (!this.wikiDescriptions.containsKey(modid))
						this.wikiDescriptions.put(modid, new LinkedHashMap <String, String>());
					this.wikiDescriptions.get(modid).put(name, desc);
					nentries += 1;
				}catch (Exception e){
					System.out.printf("%s\n", e);
				}
			}
		}
		*/
		Waila.log.log(Level.INFO, String.format("Registered %s entries from %s", nentries, filename));
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
	
	private List<String[]> readFileAsString(String filePath) throws IOException {
//		URL fileURL   = this.getClass().getResource(filePath);
//		File filedata = new File(fileURL);
//		Reader paramReader = new InputStreamReader(this.getClass().getResourceAsStream(filePath));
		
		InputStream in = getClass().getResourceAsStream(filePath);
		BufferedReader input = new BufferedReader(new InputStreamReader(in));		
		CSVReader reader = new CSVReader(input);
		
		List<String[]> myEntries = reader.readAll();
		reader.close();
		
		return myEntries;
		/*
		StringBuffer fileData = new StringBuffer();
        //BufferedReader reader = new BufferedReader(paramReader);
		
        char[] buf = new char[1024];
        int numRead=0;
        while((numRead=input.read(buf)) != -1){
            String readData = String.valueOf(buf, 0, numRead);
            fileData.append(readData);
        }
        input.close();
        return fileData.toString();
        */
	}
}
