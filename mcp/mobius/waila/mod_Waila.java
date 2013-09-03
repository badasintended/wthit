package mcp.mobius.waila;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.logging.Logger;

import mcp.mobius.waila.addons.ConfigHandler;
import mcp.mobius.waila.addons.ExternalModulesHandler;
import mcp.mobius.waila.api.IWailaRegistrar;
import mcp.mobius.waila.network.WailaConnectionHandler;
import mcp.mobius.waila.network.WailaPacketHandler;
import mcp.mobius.waila.server.ProxyServer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.Configuration;
import codechicken.core.CommonUtils;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.IMCCallback;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.PostInit;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.event.FMLInterModComms.IMCMessage;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.common.registry.ItemData;

@Mod(modid="Waila", name="Waila", version="1.3.9")
@NetworkMod(channels = {"Waila"},clientSideRequired=false, serverSideRequired=false, connectionHandler = WailaConnectionHandler.class, 
			packetHandler = WailaPacketHandler.class, versionBounds="[1.3.0,)")

public class mod_Waila {
    // The instance of your mod that Forge uses.
	@Instance("Waila")
	public static mod_Waila instance;

	@SidedProxy(clientSide="mcp.mobius.waila.ProxyClient", serverSide="mcp.mobius.waila.server.ProxyServer")
	public static ProxyServer proxy;	
	
	public static Logger log = Logger.getLogger("Waila");
	private HashMap<Integer, String> itemMap = new HashMap<Integer, String>();	
	public  Configuration config = null;	
	public boolean serverPresent = false;
	public HashMap<String, String> modSourceList = new HashMap<String, String>();
	
	
	@PreInit
	public void preInit(FMLPreInitializationEvent event) {
		config = new Configuration(event.getSuggestedConfigurationFile());
	}	
	
	@Init
	public void load(FMLInitializationEvent event) {
		proxy.registerHandlers();
	}

	@PostInit
	public void postInit(FMLPostInitializationEvent event) {
		/* This section is about preloading the name/modid of the items in a table, so we don't look it up at every cycle */
		
        NBTTagList itemDataList = new NBTTagList();
        GameData.writeItemData(itemDataList);
        
        for(int i = 0; i < itemDataList.tagCount(); i++)
        {
            ItemData itemData = new ItemData((NBTTagCompound) itemDataList.tagAt(i));
            this.itemMap.put(itemData.getItemId(), itemData.getModId());
        } 	
        
        for (ModContainer mod : Loader.instance().getModList()) {
        	this.modSourceList.put(mod.getSource().getName(), mod.getName());
        	System.out.printf("%s\n", mod.getSource().getName());
        }
        
//        for (String s : this.modSourceList.keySet())
//        	if (this.modSourceList.get(s) == "Minecraft Coder Pack")
//        		this.modSourceList.put(s, "Minecraft");
        
	}

	@IMCCallback
	public void processIMC(FMLInterModComms.IMCEvent event)
	{
		for (IMCMessage imcMessage : event.getMessages()){
			if (!imcMessage.isStringMessage()) continue;
			
			if (imcMessage.key.equalsIgnoreCase("addconfig")){
				String[] params = imcMessage.getStringValue().split("\\$\\$");
				if (params.length != 3){
					mod_Waila.log.warning(String.format("Error while parsing config option from [ %s ] for %s", imcMessage.getSender(), imcMessage.getStringValue()));					
					continue;
				}
				mod_Waila.log.info(String.format("Receiving config request from [ %s ] for %s", imcMessage.getSender(), imcMessage.getStringValue()));				
				ConfigHandler.instance().addConfig(params[0], params[1], params[2]);
			}
			
			if (imcMessage.key.equalsIgnoreCase("register")){
				mod_Waila.log.info(String.format("Receiving registration request from [ %s ] for method %s", imcMessage.getSender(), imcMessage.getStringValue()));
				this.callbackRegistration(imcMessage.getStringValue(), imcMessage.getSender());
			}
		}
	}		
	
	public void callbackRegistration(String method, String modname){
		String[] splitName = method.split("\\.");
		String methodName = splitName[splitName.length-1];
		String className  = method.substring(0, method.length()-methodName.length()-1);
		
		mod_Waila.log.info(String.format("Trying to reflect %s %s", className, methodName));
		
		try{
			Class  reflectClass  = Class.forName(className);
			Method reflectMethod = reflectClass.getDeclaredMethod(methodName, IWailaRegistrar.class);
			reflectMethod.invoke(null, (IWailaRegistrar)ExternalModulesHandler.instance());
			
			mod_Waila.log.info(String.format("Success in registering %s", modname));
			
		} catch (ClassNotFoundException e){
			mod_Waila.log.warning(String.format("Could not find class %s", className));
		} catch (NoSuchMethodException e){
			mod_Waila.log.warning(String.format("Could not find method %s", methodName));
		} catch (Exception e){
			mod_Waila.log.warning(String.format("Exception while trying to access the method : %s", e.toString()));
		}
		
	}
	
	public String getModName(ItemStack itemstack){
		try{
			String modID = this.itemMap.get(itemstack.itemID);
			ModContainer mod = CommonUtils.findModContainer(modID);
			String modname = mod == null ? modID : mod.getName();		
			return modname;
		} catch (NullPointerException e){
			//System.out.printf("NPE : %s\n",itemstack.toString());
			return "";
		}
	}

	public String getModID(ItemStack itemstack){
		try{
			String modID = this.itemMap.get(itemstack.itemID);
			return modID;
		} catch (NullPointerException e){
			//System.out.printf("NPE : %s\n",itemstack.toString());
			return "";
		}
	}	

	
}