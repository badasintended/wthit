package mcp.mobius.waila.utils;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.registry.GameData;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Locale;

public class ModIdentification {

    public static HashMap<String, String> modSource_Name = new HashMap<String, String>();
    public static HashMap<String, String> modSource_ID = new HashMap<String, String>();
    public static HashMap<Integer, String> itemMap = new HashMap<Integer, String>();
    public static HashMap<String, String> keyhandlerStrings = new HashMap<String, String>();

    public static void init() {

        //TODO : Redo this for the new frameworkn (1.7.2)
		/*
        NBTTagList itemDataList = new NBTTagList();
        GameData.writeItemData(itemDataList);

        for(int i = 0; i < itemDataList.tagCount(); i++)
        {
            ItemData itemData = new ItemData((NBTTagCompound) itemDataList.tagAt(i));
            itemMap.put(itemData.getItemId(), itemData.getModId());
        }
		 */

        for (ModContainer mod : Loader.instance().getModList()) {
            modSource_Name.put(mod.getSource().getName(), mod.getName());
            modSource_ID.put(mod.getSource().getName(), mod.getModId());
        }

        //TODO : Update this to match new version (1.7.2)
        modSource_Name.put("1.6.2.jar", "Minecraft");
        modSource_Name.put("1.6.3.jar", "Minecraft");
        modSource_Name.put("1.6.4.jar", "Minecraft");
        modSource_Name.put("1.7.2.jar", "Minecraft");
        modSource_Name.put("1.8.0.jar", "Minecraft");
        modSource_Name.put("Forge", "Minecraft");
        modSource_ID.put("1.6.2.jar", "Minecraft");
        modSource_ID.put("1.6.3.jar", "Minecraft");
        modSource_ID.put("1.6.4.jar", "Minecraft");
        modSource_ID.put("1.7.2.jar", "Minecraft");
        modSource_ID.put("1.8.0.jar", "Minecraft");
        modSource_ID.put("Forge", "Minecraft");


//      for (String s : this.modSourceList.keySet())
//    	if (this.modSourceList.get(s) == "Minecraft Coder Pack")
//    		this.modSourceList.put(s, "Minecraft");        

        // Code to retrieve all the registered keybindings along with the mod adding them.
        /*
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
		*/
    }

    public static String nameFromObject(Object obj) {
        String objPath = obj.getClass().getProtectionDomain().getCodeSource().getLocation().toString();

        try {
            objPath = URLDecoder.decode(objPath, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String modName = "<Unknown>";
        for (String s : modSource_Name.keySet())
            if (objPath.contains(s)) {
                modName = modSource_Name.get(s);
                break;
            }

        if (modName.equals("Minecraft Coder Pack"))
            modName = "Minecraft";

        return modName;
    }

	/*
	public static String idFromObject(Object obj){
		String objPath = obj.getClass().getProtectionDomain().getCodeSource().getLocation().toString();
	
		try {
			objPath = URLDecoder.decode(objPath, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		
		String modName = "<Unknown>";
		for (String s: modSource_ID.keySet())
			if (objPath.contains(s)){
				modName = modSource_ID.get(s);
				break;
			}
		
		if (modName.equals("Minecraft Coder Pack"))
			modName = "Minecraft";
		
		return modName;
	}
	*/

    public static String nameFromStack(ItemStack stack) {
        try {
            ResourceLocation resource = (ResourceLocation) GameData.getItemRegistry().getNameForObject(stack.getItem());
            ModContainer mod = ModIdentification.findModContainer(resource.getResourceDomain());
            String modName = mod == null ? "Minecraft" : mod.getName();
            return modName;
        } catch (NullPointerException e) {
            return "";
        }
    }

    public static ModContainer findModContainer(String modID) {
        for (ModContainer mc : Loader.instance().getModList())
            if (modID.toLowerCase(Locale.US).equals(mc.getModId().toLowerCase(Locale.US)))
                return mc;
        return null;
    }
}