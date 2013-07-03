package mcp.mobius.waila.addons;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.logging.Level;

import net.minecraft.block.Block;

import codechicken.nei.api.API;

import mcp.mobius.waila.mod_Waila;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.IWailaRegistrar;
import mcp.mobius.waila.api.IWailaSummaryProvider;
import mcp.mobius.waila.handlers.HUDHandlerExternal;

public class ExternalModulesHandler implements IWailaRegistrar {

	private static ExternalModulesHandler instance = null;
	public LinkedHashMap<Integer, ArrayList<IWailaDataProvider>> headProviders  = new LinkedHashMap<Integer, ArrayList<IWailaDataProvider>>();
	public LinkedHashMap<Integer, ArrayList<IWailaDataProvider>> bodyProviders  = new LinkedHashMap<Integer, ArrayList<IWailaDataProvider>>();
	public LinkedHashMap<Integer, ArrayList<IWailaDataProvider>> tailProviders  = new LinkedHashMap<Integer, ArrayList<IWailaDataProvider>>();	
	public LinkedHashMap<Integer, ArrayList<IWailaDataProvider>> stackProviders = new LinkedHashMap<Integer, ArrayList<IWailaDataProvider>>();	

	public LinkedHashMap<Class, ArrayList<IWailaDataProvider>> headBlockProviders  = new LinkedHashMap<Class, ArrayList<IWailaDataProvider>>();
	public LinkedHashMap<Class, ArrayList<IWailaDataProvider>> bodyBlockProviders  = new LinkedHashMap<Class, ArrayList<IWailaDataProvider>>();
	public LinkedHashMap<Class, ArrayList<IWailaDataProvider>> tailBlockProviders  = new LinkedHashMap<Class, ArrayList<IWailaDataProvider>>();	
	public LinkedHashMap<Class, ArrayList<IWailaDataProvider>> stackBlockProviders = new LinkedHashMap<Class, ArrayList<IWailaDataProvider>>();	
	
	public LinkedHashMap<String, LinkedHashMap <String, String>> wikiDescriptions = new LinkedHashMap<String, LinkedHashMap <String, String>>();

	public LinkedHashMap<Class, ArrayList<IWailaSummaryProvider>> summaryProviders = new LinkedHashMap<Class, ArrayList<IWailaSummaryProvider>>();
	
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
	public void addConfigRemote(String modname, String key, String configname) {
		ConfigHandler.instance().addConfigServer(modname, key, configname);
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

	@Override
	public void registerStackProvider(IWailaDataProvider dataProvider, Class block) {
		for (int i = 0; i < Block.blocksList.length; i++)
			if (block.isInstance(Block.blocksList[i])){
				this.registerStackProvider(dataProvider, i);
			}
	}		

	@Override
	public void registerShortDataProvider(IWailaSummaryProvider dataProvider, Class item) {
		if (!this.summaryProviders.containsKey(item))
			this.summaryProviders.put(item, new ArrayList<IWailaSummaryProvider>());
		this.summaryProviders.get(item).add(dataProvider);		
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
		for (Class clazz : this.bodyBlockProviders.keySet())
			if (clazz.isInstance(block))
				returnList.addAll(this.bodyBlockProviders.get(clazz));
				
		return returnList;
	}	

	public ArrayList<IWailaDataProvider> getStackProviders(int blockID) {
		return this.stackProviders.get(blockID);
	}	

	public ArrayList<IWailaSummaryProvider> getSummaryProvider(Object item){
		ArrayList<IWailaSummaryProvider> returnList = new ArrayList<IWailaSummaryProvider>();
		for (Class clazz : this.summaryProviders.keySet())
			if (clazz.isInstance(item))
				returnList.addAll(this.summaryProviders.get(clazz));
				
		return returnList;
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

	public boolean hasStackProviders(int blockID){
		return this.stackProviders.containsKey(blockID);
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
	
	@Override
	public void registerDocTextFile(String modid, String filename) {
		String docData  = null;
		int    nentries = 0;
		
		try{
			docData = this.readFileAsString(filename);
		} catch (IOException e){
			mod_Waila.log.log(Level.WARNING, String.format("Error while accessing file %s : %s", filename, e));
			return;
		}

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
		mod_Waila.instance.log.log(Level.INFO, String.format("Registered %s entries from %s", nentries, filename));
	}	
	
	public boolean hasDocText(String modid, String name){
		if (this.wikiDescriptions.containsKey(modid))
			return this.wikiDescriptions.get(modid).containsKey(name);
		else
			return false;
	}
	
	public String getDocText(String modid, String name){
		return this.wikiDescriptions.get(modid).get(name);
	}
	
	private String readFileAsString(String filePath) throws IOException {
//		URL fileURL   = this.getClass().getResource(filePath);
//		File filedata = new File(fileURL);
//		Reader paramReader = new InputStreamReader(this.getClass().getResourceAsStream(filePath)); 
  
		InputStream in = getClass().getResourceAsStream(filePath);
		BufferedReader input = new BufferedReader(new InputStreamReader(in));		
		
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
	}
}
