package mcp.mobius.waila.api.impl;

import au.com.bytecode.opencsv.CSVReader;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.*;
import mcp.mobius.waila.cbcore.LangUtil;
import mcp.mobius.waila.utils.Constants;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

public class ModuleRegistrar implements IWailaRegistrar {

    private static ModuleRegistrar instance = null;

    // Data providers
    public EnumMap<TagLocation, LinkedHashMap<Class, ArrayList<IWailaDataProvider>>> blockProviders = Maps.newEnumMap(TagLocation.class);
    public EnumMap<TagLocation, LinkedHashMap<Class, ArrayList<IWailaEntityProvider>>> entityProviders = Maps.newEnumMap(TagLocation.class);
    public LinkedHashMap<String, String> IMCRequests = new LinkedHashMap<String, String>();

    // Wiki things. Dunno what it's for.
    public LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, String>>> wikiDescriptions = new LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, String>>>();
    public LinkedHashMap<Class, ArrayList<IWailaSummaryProvider>> summaryProviders = new LinkedHashMap<Class, ArrayList<IWailaSummaryProvider>>();

    // Decorators
    public LinkedHashMap<Class, ArrayList<IWailaBlockDecorator>> blockClassDecorators = new LinkedHashMap<Class, ArrayList<IWailaBlockDecorator>>();
    public LinkedHashMap<String, IWailaTooltipRenderer> tooltipRenderers = new LinkedHashMap<String, IWailaTooltipRenderer>();

    // FMP stuff that isn't used
    public LinkedHashMap<String, ArrayList<IWailaFMPProvider>> headFMPProviders = new LinkedHashMap<String, ArrayList<IWailaFMPProvider>>();
    public LinkedHashMap<String, ArrayList<IWailaFMPProvider>> bodyFMPProviders = new LinkedHashMap<String, ArrayList<IWailaFMPProvider>>();
    public LinkedHashMap<String, ArrayList<IWailaFMPProvider>> tailFMPProviders = new LinkedHashMap<String, ArrayList<IWailaFMPProvider>>();
    public LinkedHashMap<String, ArrayList<IWailaFMPDecorator>> FMPClassDecorators = new LinkedHashMap<String, ArrayList<IWailaFMPDecorator>>();

    private ModuleRegistrar() {
        instance = this;

        for (TagLocation location : TagLocation.values()) {
            blockProviders.put(location, new LinkedHashMap<Class, ArrayList<IWailaDataProvider>>());
            entityProviders.put(location, new LinkedHashMap<Class, ArrayList<IWailaEntityProvider>>());
        }
    }

    /* IMC HANDLING */
    public void addIMCRequest(String method, String modname) {
        this.IMCRequests.put(method, modname);
    }

    /* CONFIG HANDLING */
    @Override
    public void addConfig(String modname, String key, String configname) {
        this.addConfig(modname, key, configname, Constants.CFG_DEFAULT_VALUE);
    }

    @Override
    public void addConfigRemote(String modname, String key, String configname) {
        this.addConfigRemote(modname, key, configname, Constants.CFG_DEFAULT_VALUE);
    }

    @Override
    public void addConfig(String modname, String key) {
        this.addConfig(modname, key, Constants.CFG_DEFAULT_VALUE);
    }

    @Override
    public void addConfigRemote(String modname, String key) {
        this.addConfigRemote(modname, key, Constants.CFG_DEFAULT_VALUE);
    }

    @Override
    public void addConfig(String modname, String key, String configname, boolean defvalue) {
        ConfigHandler.instance().addConfig(modname, key, LangUtil.translateG(configname), defvalue);
    }

    @Override
    public void addConfigRemote(String modname, String key, String configname, boolean defvalue) {
        ConfigHandler.instance().addConfigServer(modname, key, LangUtil.translateG(configname), defvalue);
    }

    @Override
    public void addConfig(String modname, String key, boolean defvalue) {
        ConfigHandler.instance().addConfig(modname, key, LangUtil.translateG("option." + key), defvalue);
    }

    @Override
    public void addConfigRemote(String modname, String key, boolean defvalue) {
        ConfigHandler.instance().addConfigServer(modname, key, LangUtil.translateG("option." + key), defvalue);
    }

    /* REGISTRATION METHODS */

    @Override
    public void registerBlockProvider(IWailaDataProvider dataProvider, Class<? extends Block> blockClass, TagLocation... locations) {
        for (TagLocation location : locations) {
            Map<Class, ArrayList<IWailaDataProvider>> registry = blockProviders.get(location);
            List<IWailaDataProvider> providers = registry.get(blockClass);
            if (providers == null) {
                registry.put(blockClass, Lists.newArrayList(dataProvider));
                return;
            }

            providers.add(dataProvider);
        }
    }

    @Override
    public void registerTileProvider(IWailaDataProvider dataProvider, Class<? extends TileEntity> tileClass, TagLocation... locations) {
        for (TagLocation location : locations) {
            Map<Class, ArrayList<IWailaDataProvider>> registry = blockProviders.get(location);
            List<IWailaDataProvider> providers = registry.get(tileClass);
            if (providers == null) {
                registry.put(tileClass, Lists.newArrayList(dataProvider));
                return;
            }

            providers.add(dataProvider);
        }
    }

    @Override
    public void registerEntityProvider(IWailaEntityProvider dataProvider, Class<? extends Entity> entityClass, TagLocation... locations) {
        for (TagLocation location : locations) {
            Map<Class, ArrayList<IWailaEntityProvider>> registry = entityProviders.get(location);
            List<IWailaEntityProvider> providers = registry.get(entityClass);
            if (providers == null) {
                registry.put(entityClass, Lists.newArrayList(dataProvider));
                return;
            }

            providers.add(dataProvider);
        }
    }

    // Deprecated registration methods

    @Override
    public void registerHeadProvider(IWailaDataProvider dataProvider, Class block) {
        this.registerProvider(dataProvider, block, blockProviders.get(TagLocation.HEAD));
    }

    @Override
    public void registerBodyProvider(IWailaDataProvider dataProvider, Class block) {
        this.registerProvider(dataProvider, block, blockProviders.get(TagLocation.BODY));
    }

    @Override
    public void registerTailProvider(IWailaDataProvider dataProvider, Class block) {
        this.registerProvider(dataProvider, block, blockProviders.get(TagLocation.TAIL));
    }

    @Override
    public void registerStackProvider(IWailaDataProvider dataProvider, Class block) {
        this.registerProvider(dataProvider, block, blockProviders.get(TagLocation.STACK));
    }

    @Override
    public void registerNBTProvider(IWailaDataProvider dataProvider, Class entity) {
        this.registerProvider(dataProvider, entity, blockProviders.get(TagLocation.DATA));
    }

    @Override
    public void registerHeadProvider(IWailaEntityProvider dataProvider, Class entity) {
        this.registerProvider(dataProvider, entity, entityProviders.get(TagLocation.HEAD));
    }

    @Override
    public void registerBodyProvider(IWailaEntityProvider dataProvider, Class entity) {
        this.registerProvider(dataProvider, entity, entityProviders.get(TagLocation.BODY));
    }

    @Override
    public void registerTailProvider(IWailaEntityProvider dataProvider, Class entity) {
        this.registerProvider(dataProvider, entity, entityProviders.get(TagLocation.TAIL));
    }

    @Override
    public void registerNBTProvider(IWailaEntityProvider dataProvider, Class entity) {
        this.registerProvider(dataProvider, entity, entityProviders.get(TagLocation.DATA));
    }

    @Override
    public void registerOverrideEntityProvider(IWailaEntityProvider dataProvider, Class entity) {
        this.registerProvider(dataProvider, entity, entityProviders.get(TagLocation.OVERRIDE));
    }

    @Override
    public void registerHeadProvider(IWailaFMPProvider dataProvider, String name) {
        this.registerProvider(dataProvider, name, this.headFMPProviders);
    }

    @Override
    public void registerBodyProvider(IWailaFMPProvider dataProvider, String name) {
        this.registerProvider(dataProvider, name, this.bodyFMPProviders);
    }

    @Override
    public void registerTailProvider(IWailaFMPProvider dataProvider, String name) {
        this.registerProvider(dataProvider, name, this.tailFMPProviders);
    }

    @Override
    public void registerDecorator(IWailaBlockDecorator decorator, Class block) {
        this.registerProvider(decorator, block, this.blockClassDecorators);
    }

	/*
    @Override
	public void registerShortDataProvider(IWailaSummaryProvider dataProvider, Class item) {
		this.registerProvider(dataProvider, item, this.summaryProviders);	
	}
	*/

    @Override
    public void registerDecorator(IWailaFMPDecorator decorator, String name) {
        this.registerProvider(decorator, name, this.FMPClassDecorators);
    }

    private <T, V> void registerProvider(T dataProvider, V clazz, LinkedHashMap<V, ArrayList<T>> target) {
        if (clazz == null || dataProvider == null)
            throw new RuntimeException(String.format("Trying to register a null provider or null block ! Please check the stacktrace to know what was the original registration method. [Provider : %s, Target : %s]", dataProvider.getClass().getName(), clazz));

        if (!target.containsKey(clazz))
            target.put(clazz, new ArrayList<T>());

        ArrayList<T> providers = target.get(clazz);
        if (providers.contains(dataProvider)) return;

        target.get(clazz).add(dataProvider);
    }

    @Override
    public void registerTooltipRenderer(String name, IWailaTooltipRenderer renderer) {
        if (!this.tooltipRenderers.containsKey(name))
            this.tooltipRenderers.put(name, renderer);
        else
            Waila.log.warn(String.format("A renderer named %s already exists (Class : %s). Skipping new renderer.", name, renderer.getClass().getName()));
    }

    /* PROVIDER GETTERS */

    public Map<Integer, List<IWailaDataProvider>> getBlockProviders(Block block, TagLocation location) {
        return getProviders(block, blockProviders.get(location));
    }

    public Map<Integer, List<IWailaDataProvider>> getTileProviders(TileEntity tile, TagLocation location) {
        return getProviders(tile, blockProviders.get(location));
    }

    public Map<Integer, List<IWailaEntityProvider>> getEntityProviders(Entity entity, TagLocation location) {
        return getProviders(entity, entityProviders.get(location));
    }

    public Map<Integer, List<IWailaBlockDecorator>> getBlockDecorators(Object block) {
        return getProviders(block, this.blockClassDecorators);
    }

    // Deprecated getters

    @Deprecated
    public Map<Integer, List<IWailaDataProvider>> getHeadProviders(Object block) {
        return getProviders(block, blockProviders.get(TagLocation.HEAD));
    }

    @Deprecated
    public Map<Integer, List<IWailaDataProvider>> getBodyProviders(Object block) {
        return getProviders(block, blockProviders.get(TagLocation.BODY));
    }

    @Deprecated
    public Map<Integer, List<IWailaDataProvider>> getTailProviders(Object block) {
        return getProviders(block, blockProviders.get(TagLocation.TAIL));
    }

    @Deprecated
    public Map<Integer, List<IWailaDataProvider>> getStackProviders(Object block) {
        return getProviders(block, blockProviders.get(TagLocation.STACK));
    }

    @Deprecated
    public Map<Integer, List<IWailaDataProvider>> getNBTProviders(Object block) {
        return getProviders(block, blockProviders.get(TagLocation.DATA));
    }

    @Deprecated
    public Map<Integer, List<IWailaEntityProvider>> getHeadEntityProviders(Object entity) {
        return getProviders(entity, entityProviders.get(TagLocation.HEAD));
    }

    @Deprecated
    public Map<Integer, List<IWailaEntityProvider>> getBodyEntityProviders(Object entity) {
        return getProviders(entity, entityProviders.get(TagLocation.BODY));
    }

    @Deprecated
    public Map<Integer, List<IWailaEntityProvider>> getTailEntityProviders(Object entity) {
        return getProviders(entity, entityProviders.get(TagLocation.TAIL));
    }

    @Deprecated
    public Map<Integer, List<IWailaEntityProvider>> getOverrideEntityProviders(Object entity) {
        return getProviders(entity, entityProviders.get(TagLocation.OVERRIDE));
    }

    @Deprecated
    public Map<Integer, List<IWailaEntityProvider>> getNBTEntityProviders(Object entity) {
        return getProviders(entity, entityProviders.get(TagLocation.DATA));
    }

    public Map<Integer, List<IWailaFMPProvider>> getHeadFMPProviders(String name) {
        return getProviders(name, this.headFMPProviders);
    }

    public Map<Integer, List<IWailaFMPProvider>> getBodyFMPProviders(String name) {
        return getProviders(name, this.bodyFMPProviders);
    }

    public Map<Integer, List<IWailaFMPProvider>> getTailFMPProviders(String name) {
        return getProviders(name, this.tailFMPProviders);
    }

    public Map<Integer, List<IWailaSummaryProvider>> getSummaryProvider(Object item) {
        return getProviders(item, this.summaryProviders);
    }

    public Map<Integer, List<IWailaFMPDecorator>> getFMPDecorators(String name) {
        return getProviders(name, this.FMPClassDecorators);
    }

    public IWailaTooltipRenderer getTooltipRenderer(String name) {
        return this.tooltipRenderers.get(name);
    }

    private <T> Map<Integer, List<T>> getProviders(Object obj, LinkedHashMap<Class, ArrayList<T>> target) {
        Map<Integer, List<T>> returnList = new TreeMap<Integer, List<T>>();
        Integer index = 0;

        for (Class clazz : target.keySet()) {
            if (clazz.isInstance(obj))
                returnList.put(index, target.get(clazz));

            index++;
        }

        return returnList;
    }

    private <T> Map<Integer, List<T>> getProviders(String name, LinkedHashMap<String, ArrayList<T>> target) {
        Map<Integer, List<T>> returnList = new TreeMap<Integer, List<T>>();
        returnList.put(0, target.get(name));
        return returnList;
    }
	
	/* HAS METHODS */

	public boolean hasProviders(Block block, TagLocation location) {
	    return hasProviders(block, blockProviders.get(location));
    }

    public boolean hasProviders(TileEntity tile, TagLocation location) {
        return hasProviders(tile, blockProviders.get(location));
    }

    public boolean hasProviders(Entity entity, TagLocation location) {
        return hasProviders(entity, entityProviders.get(location));
    }

    public boolean hasBlockDecorator(Object block) {
        return hasProviders(block, this.blockClassDecorators);
    }

    // Deprecated queries

    @Deprecated
    public boolean hasStackProviders(Object block) {
        return hasProviders(block, blockProviders.get(TagLocation.STACK));
    }

    @Deprecated
    public boolean hasHeadProviders(Object block) {
        return hasProviders(block, blockProviders.get(TagLocation.HEAD));
    }

    @Deprecated
    public boolean hasBodyProviders(Object block) {
        return hasProviders(block, blockProviders.get(TagLocation.BODY));
    }

    @Deprecated
    public boolean hasTailProviders(Object block) {
        return hasProviders(block, blockProviders.get(TagLocation.TAIL));
    }

    @Deprecated
    public boolean hasNBTProviders(Object block) {
        return hasProviders(block, blockProviders.get(TagLocation.DATA));
    }

    @Deprecated
    public boolean hasOverrideEntityProviders(Object entity) {
        return hasProviders(entity, entityProviders.get(TagLocation.OVERRIDE));
    }

    @Deprecated
    public boolean hasHeadEntityProviders(Object entity) {
        return hasProviders(entity, entityProviders.get(TagLocation.HEAD));
    }

    @Deprecated
    public boolean hasBodyEntityProviders(Object entity) {
        return hasProviders(entity, entityProviders.get(TagLocation.BODY));
    }

    @Deprecated
    public boolean hasTailEntityProviders(Object entity) {
        return hasProviders(entity, entityProviders.get(TagLocation.TAIL));
    }

    @Deprecated
    public boolean hasNBTEntityProviders(Object entity) {
        return hasProviders(entity, entityProviders.get(TagLocation.DATA));
    }

    public boolean hasHeadFMPProviders(String name) {
        return hasProviders(name, this.headFMPProviders);
    }

    public boolean hasBodyFMPProviders(String name) {
        return hasProviders(name, this.bodyFMPProviders);
    }

    public boolean hasTailFMPProviders(String name) {
        return hasProviders(name, this.tailFMPProviders);
    }

    public boolean hasFMPDecorator(String name) {
        return hasProviders(name, this.FMPClassDecorators);
    }

    private <T> boolean hasProviders(Object obj, LinkedHashMap<Class, ArrayList<T>> target) {
        for (Class clazz : target.keySet())
            if (clazz.isInstance(obj))
                return true;
        return false;
    }

    private <T> boolean hasProviders(String name, LinkedHashMap<String, ArrayList<T>> target) {
        return target.containsKey(name);
    }

    public boolean hasSummaryProvider(Class item) {
        return this.summaryProviders.containsKey(item);
    }

    public boolean hasDocTextModID(String modid) {
        return this.wikiDescriptions.containsKey(modid);
    }
	
	/* ----------------- */
	/*
	@Override
	public void registerDocTextFile(String filename) {
		List<String[]> docData  = null;
		int    nentries = 0;
		
		
		try{
			docData = this.readFileAsString(filename);
		} catch (IOException e){
			Waila.log.log(Level.WARN, String.format("Error while accessing file %s : %s", filename, e));
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
		
		
//		String[] sections = docData.split(">>>>");
//		for (String s : sections){
//			s.trim();
//			if (!s.equals("")){
//				try{
//					String name   = s.split("\r?\n",2)[0].trim();
//					String desc   = s.split("\r?\n",2)[1].trim();
//					if (!this.wikiDescriptions.containsKey(modid))
//						this.wikiDescriptions.put(modid, new LinkedHashMap <String, String>());
//					this.wikiDescriptions.get(modid).put(name, desc);
//					nentries += 1;
//				}catch (Exception e){
//					System.out.printf("%s\n", e);
//				}
//			}
//		}
		
		Waila.log.log(Level.INFO, String.format("Registered %s entries from %s", nentries, filename));
	}	
	*/

    public boolean hasDocTextItem(String modid, String item) {
        if (this.hasDocTextModID(modid))
            return this.wikiDescriptions.get(modid).containsKey(item);
        return false;
    }

    public boolean hasDocTextMeta(String modid, String item, String meta) {
        if (this.hasDocTextItem(modid, item))
            return this.wikiDescriptions.get(modid).get(item).containsKey(meta);
        return false;
    }

    public LinkedHashMap<String, String> getDocText(String modid, String name) {
        return this.wikiDescriptions.get(modid).get(name);
    }

    public String getDocText(String modid, String name, String meta) {
        return this.wikiDescriptions.get(modid).get(name).get(meta);
    }

    public boolean hasDocTextSpecificMeta(String modid, String name, String meta) {
        for (String s : this.getDocText(modid, name).keySet())
            if (s.equals(meta))
                return true;
        return false;
    }

    public String getDoxTextWildcardMatch(String modid, String name) {
        Set<String> keys = this.wikiDescriptions.get(modid).keySet();
        for (String s : keys) {
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

    public static ModuleRegistrar instance() {
        if (ModuleRegistrar.instance == null)
            ModuleRegistrar.instance = new ModuleRegistrar();
        return ModuleRegistrar.instance;
    }
}
