package mcp.mobius.waila.server;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.addons.agriculture.AgricultureModule;
import mcp.mobius.waila.addons.vanillamc.HUDHandlerFurnace;
import mcp.mobius.waila.addons.vanillamc.HUDHandlerVanilla;
import mcp.mobius.waila.api.IWailaRegistrar;
import mcp.mobius.waila.api.impl.ModuleRegistrar;
import mcp.mobius.waila.handlers.DecoratorFMP;
import mcp.mobius.waila.handlers.HUDHandlerFMP;
import net.minecraftforge.fml.common.Loader;

import java.lang.reflect.Method;

public class ProxyServer {

    public ProxyServer() {
    }

    public void registerHandlers() {
    }

    public void registerMods() {

        HUDHandlerVanilla.register();
        HUDHandlerFurnace.register();

		/* BUILDCRAFT */
        //BCModule.register();
		
		/* INDUSTRIALCRAFT2 */
        //IC2Module.register();
		
		/*Thaumcraft*/
        //ThaumcraftModule.register();
		
		/*EnderStorage*/
        //EnderStorageModule.register();
		
		/*Gravestone*/
        //GravestoneModule.register();
		
		/*Twilight forest*/
        //TwilightForestModule.register();
		
		/* Thermal Expansion */
        //ThermalExpansionModule.register();

		/* Thermal Dynamics */
        //ThermalDynamicsModule.register();
		
		/* ETB */
        //ETBModule.register();
		
		/* EnderIO */
        //EnderIOModule.register();
		
		/* ProjectRed API */
        //ProjectRedModule.register();
		
		/* ExtraUtilities */
        //ExtraUtilitiesModule.register();
		
		/* OpenBlocks */
        //OpenBlocksModule.register();
		
		/* Railcraft */
        //RailcraftModule.register();
		
		/* Steve's Carts */
        //StevesCartsModule.register();
		
		/* Secret Rooms */
        //SecretRoomsModule.register();
		
		/* Carpenter's Blocks */
        //CarpentersModule.register();

		/* Pam's HarvestCraft */
        //HarvestcraftModule.register();
		
		/* Magical crops */
        //MagicalCropsModule.register();
		
		/* Statues */
        //StatuesModule.register();
		
		/* Agriculture */
        AgricultureModule.register();

        if (Loader.isModLoaded("ForgeMultipart")) {
            HUDHandlerFMP.register();
            DecoratorFMP.register();
        }

        //ModuleRegistrar.instance().registerBodyProvider(new HUDHandlerBlocks(),   Block.class);
        //ModuleRegistrar.instance().registerBodyProvider(new HUDHandlerBlocks(),   TileEntity.class);
    }

    public void registerIMCs() {
        for (String s : ModuleRegistrar.instance().IMCRequests.keySet()) {
            this.callbackRegistration(s, ModuleRegistrar.instance().IMCRequests.get(s));
        }
    }

    public void callbackRegistration(String method, String modname) {
        String[] splitName = method.split("\\.");
        String methodName = splitName[splitName.length - 1];
        String className = method.substring(0, method.length() - methodName.length() - 1);

        Waila.log.info(String.format("Trying to reflect %s %s", className, methodName));

        try {
            Class reflectClass = Class.forName(className);
            Method reflectMethod = reflectClass.getDeclaredMethod(methodName, IWailaRegistrar.class);
            reflectMethod.invoke(null, (IWailaRegistrar) ModuleRegistrar.instance());

            Waila.log.info(String.format("Success in registering %s", modname));

        } catch (ClassNotFoundException e) {
            Waila.log.warn(String.format("Could not find class %s", className));
        } catch (NoSuchMethodException e) {
            Waila.log.warn(String.format("Could not find method %s", methodName));
        } catch (Exception e) {
            Waila.log.warn(String.format("Exception while trying to access the method : %s", e.toString()));
        }
    }


    public Object getFont() {
        return null;
    }
}
