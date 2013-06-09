package mcp.mobius.waila;

import java.util.HashMap;
import java.util.logging.Logger;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.Configuration;
import codechicken.core.CommonUtils;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.PostInit;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.common.registry.ItemData;

@Mod(modid="Waila", name="Waila", version="0.0.3")
@NetworkMod(clientSideRequired=true, serverSideRequired=false)

public class mod_Waila {
    // The instance of your mod that Forge uses.
	@Instance("Waila")
	public static mod_Waila instance;

	@SidedProxy(clientSide="mcp.mobius.waila.ProxyClient", serverSide="mcp.mobius.waila.ProxyServer")
	public static ProxyServer proxy;	
	
	public static Logger log = Logger.getLogger("Waila");
	private HashMap<Integer, String> itemMap = new HashMap<Integer, String>();	
	public  Configuration config = null;	
	
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
	}
	
	public String getCanonicalName(ItemStack itemstack){
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
	

	
}