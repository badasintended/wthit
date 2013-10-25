package mcp.mobius.waila.tools;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Set;
import java.util.logging.Level;

import codechicken.core.CommonUtils;
import cpw.mods.fml.client.registry.KeyBindingRegistry;
import cpw.mods.fml.client.registry.KeyBindingRegistry.KeyHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.common.registry.ItemData;
import mcp.mobius.antigregtech.AccessHelper;
import mcp.mobius.waila.mod_Waila;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class ModIdentification {
	
	public static HashMap<String, String> modSource_Name   = new HashMap<String,  String>();
	public static HashMap<String, String> modSource_ID     = new HashMap<String,  String>();
	public static HashMap<Integer, String> itemMap         = new HashMap<Integer, String>();
	public static HashMap<String, String> keyhandlerStrings = new HashMap<String,  String>();
	
	public static void   init(){

        NBTTagList itemDataList = new NBTTagList();
        GameData.writeItemData(itemDataList);
        
        for(int i = 0; i < itemDataList.tagCount(); i++)
        {
            ItemData itemData = new ItemData((NBTTagCompound) itemDataList.tagAt(i));
            itemMap.put(itemData.getItemId(), itemData.getModId());
        } 		
		
        for (ModContainer mod : Loader.instance().getModList()){
        	modSource_Name.put(mod.getSource().getName(), mod.getName());
        	modSource_ID.put(mod.getSource().getName(), mod.getModId());
        }

        modSource_Name.put("1.6.2.jar", "Minecraft");
        modSource_Name.put("1.6.3.jar", "Minecraft");          
        modSource_Name.put("1.6.4.jar", "Minecraft");		
        modSource_ID.put("1.6.2.jar", "Minecraft");
        modSource_ID.put("1.6.3.jar", "Minecraft");          
        modSource_ID.put("1.6.4.jar", "Minecraft");
        
        
//      for (String s : this.modSourceList.keySet())
//    	if (this.modSourceList.get(s) == "Minecraft Coder Pack")
//    		this.modSourceList.put(s, "Minecraft");        
        
        // Code to retrieve all the registered keybindings along with the mod adding them.
        Field keyHandlers_Field = AccessHelper.getDeclaredField("cpw.mods.fml.client.registry.KeyBindingRegistry", "keyHandlers");
        try {
        	Set<KeyHandler> keyHandlers = (Set<KeyHandler>)keyHandlers_Field.get(KeyBindingRegistry.instance());
            for (KeyHandler keyhandler : keyHandlers)
            	for (int i = 0; i < keyhandler.getKeyBindings().length; i++)
            		keyhandlerStrings.put(keyhandler.getKeyBindings()[i].keyDescription, idFromObject(keyhandler));
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}        
        
	}
	
	public static String nameFromObject(Object obj){
		String objPath = obj.getClass().getProtectionDomain().getCodeSource().getLocation().toString();
		
		try {
			objPath = URLDecoder.decode(objPath, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}		
		
		String modName = "<Unknown>";
		for (String s: modSource_Name.keySet())
			if (objPath.contains(s))
				modName = modSource_Name.get(s);
		
		if (modName.equals("Minecraft Coder Pack"))
			modName = "Minecraft";
		
		return modName;
	}
	
	public static String idFromObject(Object obj){
		String objPath = obj.getClass().getProtectionDomain().getCodeSource().getLocation().toString();
	
		try {
			objPath = URLDecoder.decode(objPath, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		
		String modName = "<Unknown>";
		for (String s: modSource_ID.keySet())
			if (objPath.contains(s))
				modName = modSource_ID.get(s);
		
		if (modName.equals("Minecraft Coder Pack"))
			modName = "Minecraft";
		
		return modName;
	}	
	
	public static String nameFromStack(ItemStack itemstack){
		try{
			String modID = itemMap.get(itemstack.itemID);
			ModContainer mod = CommonUtils.findModContainer(modID);
			String modname = mod == null ? modID : mod.getName();		
			return modname;
		} catch (NullPointerException e){
			//System.out.printf("NPE : %s\n",itemstack.toString());
			return "";
		}
	}

	public static String idFromStack(ItemStack itemstack){
		try{
			String modID = itemMap.get(itemstack.itemID);
			return modID;
		} catch (NullPointerException e){
			//System.out.printf("NPE : %s\n",itemstack.toString());
			return "";
		}
	}	
}
