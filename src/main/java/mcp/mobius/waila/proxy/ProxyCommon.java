package mcp.mobius.waila.proxy;

import com.google.common.base.Stopwatch;
import com.google.common.base.Strings;
import mcp.mobius.waila.Waila;
import mcp.mobius.waila.addons.core.PluginCore;
import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.IWailaRegistrar;
import mcp.mobius.waila.api.WailaPlugin;
import mcp.mobius.waila.api.impl.ConfigHandler;
import mcp.mobius.waila.api.impl.ModuleRegistrar;
import mcp.mobius.waila.config.OverlayConfig;
import mcp.mobius.waila.utils.ModIdentification;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.discovery.ASMDataTable;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.io.File;
import java.lang.reflect.Method;

public class ProxyCommon implements IProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        Waila.plugins = event.getAsmData().getAll(WailaPlugin.class.getCanonicalName());

        Waila.configDir = new File(event.getModConfigurationDirectory(), "waila");
        Waila.themeDir = new File(Waila.configDir, "theme");
        ConfigHandler.instance().loadDefaultConfig(event);

        OverlayConfig.updateColors();
    }

    @Override
    public void init(FMLInitializationEvent event) {

    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        ModIdentification.init();
    }

    @Override
    public void loadComplete(FMLLoadCompleteEvent event) {
        registerMods();
        registerIMCs();
    }

    // Utilities

    public void registerMods() {
        // Register core plugin to make sure it gets loaded before all others.
        try {
            PluginCore core = new PluginCore();
            core.register(ModuleRegistrar.instance());
        } catch (Exception e) {
            Waila.LOGGER.error("Error registering plugin for class {}", PluginCore.class.getCanonicalName());
        }

        for (ASMDataTable.ASMData data : Waila.plugins) {
            try {
                String requiredMod = (String) data.getAnnotationInfo().get("value");
                if (Strings.isNullOrEmpty(requiredMod))
                    requiredMod = "anything";
                Waila.LOGGER.info("Attempting to register plugin for {} from {}", requiredMod, data.getClassName());
                if (Loader.isModLoaded(requiredMod) || requiredMod.equalsIgnoreCase("anything")) {
                    Stopwatch stopwatch = Stopwatch.createStarted();
                    Class<?> asmClass = Class.forName(data.getClassName());
                    if (IWailaPlugin.class.isAssignableFrom(asmClass)) {
                        IWailaPlugin wailaPlugin = (IWailaPlugin) asmClass.newInstance();
                        wailaPlugin.register(ModuleRegistrar.instance());
                        Waila.LOGGER.info("Registered plugin for {} from {} in {}", requiredMod, data.getClassName(), stopwatch.stop());
                    } else Waila.LOGGER.error("{} attempted to register a plugin for {} that did not implement IWailaPlugin", data.getClassName(), requiredMod);
                } else Waila.LOGGER.error("{} is not loaded. Passing over plugin.", requiredMod);
            } catch (Exception e) {
                Waila.LOGGER.error("Error registering plugin for class {}", data.getClassName());
            }
        }
    }

    private void registerIMCs() {
        for (String s : ModuleRegistrar.instance().IMCRequests.keySet())
            this.callbackRegistration(s, ModuleRegistrar.instance().IMCRequests.get(s));
    }

    private void callbackRegistration(String method, String modname) {
        String[] splitName = method.split("\\.");
        String methodName = splitName[splitName.length - 1];
        String className = method.substring(0, method.length() - methodName.length() - 1);

        Waila.LOGGER.info(String.format("Trying to reflect %s %s", className, methodName));

        try {
            Class reflectClass = Class.forName(className);
            Method reflectMethod = reflectClass.getDeclaredMethod(methodName, IWailaRegistrar.class);
            reflectMethod.invoke(null, (IWailaRegistrar) ModuleRegistrar.instance());

            Waila.LOGGER.info(String.format("Success in registering %s", modname));

        } catch (ClassNotFoundException e) {
            Waila.LOGGER.warn(String.format("Could not find class %s", className));
        } catch (NoSuchMethodException e) {
            Waila.LOGGER.warn(String.format("Could not find method %s", methodName));
        } catch (Exception e) {
            Waila.LOGGER.warn(String.format("Exception while trying to access the method : %s", e.toString()));
        }
    }
}
