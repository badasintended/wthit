package mcp.mobius.waila.addons.thaumcraft;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import net.minecraft.nbt.NBTTagCompound;

import org.apache.logging.log4j.Level;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.impl.ModuleRegistrar;

public class ThaumcraftModule {
	
	public static Class  Thaumcraft = null;
	public static Field  Thaumcraft_proxy = null;
	
	public static Class  IAspectContainer = null;
	public static Method IAspectContainer_getAspects = null;
	
	public static Class  AspectList = null;
	public static Method AspectList_writeToNBT = null;
	public static Field  AspectList_aspects = null;

	public static Class TileAlchemyFurnace = null;
	public static Field TileAlchemyFurnace_aspects = null;	
	
	public static Class  Aspect = null;
	public static Field  Aspect_tag = null;		
	
	public static Class  CommonProxy = null;
	public static Method CommonProxy_getKnownAspects = null;

	public static Class IGoggles = null;	

	//public static Class TileAlchemyFurnace = null;
	
	
	public static void register(){
		try{
			Thaumcraft                  = Class.forName("thaumcraft.common.Thaumcraft");
			Thaumcraft_proxy            = Thaumcraft.getDeclaredField("proxy");
			
			IAspectContainer            = Class.forName("thaumcraft.api.aspects.IAspectContainer");
			IAspectContainer_getAspects = IAspectContainer.getDeclaredMethod("getAspects");
			
			AspectList                  = Class.forName("thaumcraft.api.aspects.AspectList");
			AspectList_writeToNBT       = AspectList.getDeclaredMethod("writeToNBT", NBTTagCompound.class);
			AspectList_aspects          = AspectList.getDeclaredField("aspects");

			TileAlchemyFurnace          = Class.forName("thaumcraft.common.tiles.TileAlchemyFurnace");
			TileAlchemyFurnace_aspects  = TileAlchemyFurnace.getDeclaredField("aspects");
			
			Aspect                      = Class.forName("thaumcraft.api.aspects.Aspect");
			Aspect_tag                  = Aspect.getDeclaredField("tag");
			Aspect_tag.setAccessible(true);
			
			CommonProxy                 = Class.forName("thaumcraft.common.CommonProxy");
			CommonProxy_getKnownAspects = CommonProxy.getDeclaredMethod("getKnownAspects");

			IGoggles           			= Class.forName("thaumcraft.api.IGoggles");			

			ModuleRegistrar.instance().addConfigRemote("Thaumcraft", "thaumcraft.aspects");		
			ModuleRegistrar.instance().registerBodyProvider(new HUDHandlerIAspectContainer(), IAspectContainer);
			ModuleRegistrar.instance().registerBodyProvider(new HUDHandlerIAspectContainer(), TileAlchemyFurnace);
			ModuleRegistrar.instance().registerNBTProvider (new HUDHandlerIAspectContainer(), IAspectContainer);
			ModuleRegistrar.instance().registerNBTProvider (new HUDHandlerIAspectContainer(), TileAlchemyFurnace);
			
		} catch (ClassNotFoundException e){
			Waila.log.log(Level.WARN, "[Thaumcraft] Class not found. " + e);
			return;
		} catch (Exception e){
			Waila.log.log(Level.WARN, "[Thaumcraft] Unhandled exception." + e);
			return;			
		}		
		
		
		
	}

}
