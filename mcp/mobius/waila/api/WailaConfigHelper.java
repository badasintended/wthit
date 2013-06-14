package mcp.mobius.waila.api;

import cpw.mods.fml.common.event.FMLInterModComms;

public class WailaConfigHelper {

	public static void addConfig(String modname, String key, String configname){
		FMLInterModComms.sendMessage("Waila", "addconfig", String.format("%s$$%s$$%s", modname, key, configname));
	}

}
