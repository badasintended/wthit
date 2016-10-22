package mcp.mobius.waila.server;

import com.google.common.base.Strings;
import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.IWailaRegistrar;
import mcp.mobius.waila.api.impl.ModuleRegistrar;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.discovery.ASMDataTable;

import java.lang.reflect.Method;

public class ProxyServer {

    public void registerHandlers() {
    }

    public void registerMods() {
        for (ASMDataTable.ASMData data : Waila.plugins) {
            try {
                String requiredMod = (String) data.getAnnotationInfo().get("value");
                if (Strings.isNullOrEmpty(requiredMod))
                    requiredMod = "anything";
                Waila.log.info("Attempting to register plugin for {} from {}", requiredMod, data.getClassName());
                if (Loader.isModLoaded(requiredMod) || requiredMod.equalsIgnoreCase("anything")) {
                    Class<?> asmClass = Class.forName(data.getClassName());
                    if (IWailaPlugin.class.isAssignableFrom(asmClass)) {
                        IWailaPlugin wailaPlugin = (IWailaPlugin) asmClass.newInstance();
                        wailaPlugin.register(ModuleRegistrar.instance());
                        Waila.log.info("Registered plugin for {} from {}", requiredMod, data.getClassName());
                    } else Waila.log.error("{} attempted to register a plugin for {} that did not implement IWailaPlugin", data.getClassName(), requiredMod);
                } else Waila.log.error("{} is not loaded. Passing over plugin.", requiredMod);
            } catch (Exception e) {
                Waila.log.error("Error registering plugin for class {}", data.getClassName());
            }
        }
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
