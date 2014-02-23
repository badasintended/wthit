package mcp.mobius.waila.addons.thaumcraft;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.logging.Level;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.impl.ModuleRegistrar;

public class ThaumcraftModule {

	public static Class IAspectContainer = null;
	public static Class TileAlchemyFurnace = null;
	public static Class ItemGoggles = null;
	
	public static Class Thaumcraft = null;
	public static Class TCClientProxy = null;
	public static Class TCCommonProxy = null;
	public static Class PlayerKnowledge = null;
	public static Class Aspect = null;
	public static Class AspectList = null;
	
	public static Field TC_proxy       = null; //Thaumcraft.proxy
	public static Field playerResearch = null; //ClientProxy.playerResearch
	public static Field aspectsDiscovered = null; //PlayerKnowledge.aspectsDiscovered 
	public static Field aspect_tag        = null; //Aspect.tag
	
	public static Method hasDiscoveredAspect = null; //PlayerKnowledge.hasDiscoveredAspect
	public static Method getAspect = null; //Aspect.getAspect
	public static Method getKnownAspects = null; //CommonProxy.getKnownAspects
	public static Method list_getAspects = null; //AspectList.getAspects
	
	public static void register(){

		try{
			Thaumcraft = Class.forName("thaumcraft.common.Thaumcraft");
			Waila.log.log(Level.INFO, "Thaumcraft mod found.");
		} catch (ClassNotFoundException e){
			Waila.log.log(Level.INFO, "[Thaumcraft] Thaumcraft mod not found.");
			return;
		}
		
		try{
			IAspectContainer   = Class.forName("thaumcraft.api.aspects.IAspectContainer");
			TileAlchemyFurnace = Class.forName("thaumcraft.common.tiles.TileAlchemyFurnace");
			ItemGoggles        = Class.forName("thaumcraft.common.items.armor.ItemGoggles");
			TCClientProxy      = Class.forName("thaumcraft.client.ClientProxy");
			TCCommonProxy      = Class.forName("thaumcraft.common.CommonProxy");
			PlayerKnowledge    = Class.forName("thaumcraft.common.lib.research.PlayerKnowledge");
			Aspect             = Class.forName("thaumcraft.api.aspects.Aspect");
			AspectList         = Class.forName("thaumcraft.api.aspects.AspectList");
			
			TC_proxy           = Thaumcraft.getDeclaredField("proxy");
			playerResearch     = TCClientProxy.getDeclaredField("playerResearch");
			aspectsDiscovered  = PlayerKnowledge.getDeclaredField("aspectsDiscovered");
			playerResearch.setAccessible(true);
			aspect_tag         = Aspect.getDeclaredField("tag");
			aspect_tag.setAccessible(true);
			
			hasDiscoveredAspect = PlayerKnowledge.getDeclaredMethod("hasDiscoveredAspect", String.class, Aspect);
			getAspect           = Aspect.getDeclaredMethod("getAspect", String.class);
			getKnownAspects     = TCCommonProxy.getDeclaredMethod("getKnownAspects");
			list_getAspects     = AspectList.getDeclaredMethod("getAspects");
			
		} catch (ClassNotFoundException e){
			Waila.log.log(Level.WARNING, "[Thaumcraft] Class not found. " + e);
			return;
		} catch (Exception e){
			Waila.log.log(Level.WARNING, "[Thaumcraft] Unhandled exception." + e);
			return;			
		}
		
		ModuleRegistrar.instance().addConfigRemote("Thaumcraft", "thaumcraft.aspects");		
		ModuleRegistrar.instance().registerBodyProvider(new HUDHandlerIAspectContainer(), IAspectContainer);
		ModuleRegistrar.instance().registerBodyProvider(new HUDHandlerIAspectContainer(), TileAlchemyFurnace);
		ModuleRegistrar.instance().registerSyncedNBTKey("*", IAspectContainer);

	}

}
