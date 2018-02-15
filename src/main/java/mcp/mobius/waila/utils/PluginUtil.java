package mcp.mobius.waila.utils;

import com.google.common.base.Stopwatch;
import com.google.common.base.Strings;
import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.IWailaRegistrar;
import mcp.mobius.waila.api.impl.ModuleRegistrar;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.discovery.ASMDataTable;
import org.apache.commons.lang3.reflect.MethodUtils;

import javax.annotation.Nullable;
import java.lang.reflect.Method;
import java.util.Map;

public class PluginUtil {

    public static void gatherAnnotatedPlugins(Map<Class<?>, IWailaPlugin> plugins) {
        for (ASMDataTable.ASMData data : Waila.plugins) {
            try {
                String requiredMod = (String) data.getAnnotationInfo().get("value");
                if (Strings.isNullOrEmpty(requiredMod))
                    requiredMod = "anything";
                Waila.LOGGER.debug("Attempting to discover plugin for {} from {}", requiredMod, data.getClassName());
                if (Loader.isModLoaded(requiredMod) || requiredMod.equalsIgnoreCase("anything")) {
                    Stopwatch stopwatch = Stopwatch.createStarted();
                    Class<?> asmClass = Class.forName(data.getClassName());
                    if (IWailaPlugin.class.isAssignableFrom(asmClass)) {
                        plugins.put(asmClass, (IWailaPlugin) asmClass.newInstance());
                        Waila.LOGGER.debug("Successfully discovered plugin for {} from {} in {}", requiredMod, data.getClassName(), stopwatch.stop());
                    } else
                        Waila.LOGGER.error("{} attempted to register a plugin for {} that did not implement IWailaPlugin", data.getClassName(), requiredMod);
                } else Waila.LOGGER.error("{} is not loaded. Passing over plugin.", requiredMod);
            } catch (Exception e) {
                Waila.LOGGER.error("Error discovering plugin for class {}: {}", data.getClassName(), e.getMessage());
            }
        }
    }

    public static void gatherIMCPlugins(Map<Class<?>, IWailaPlugin> plugins) {
        for (Map.Entry<String, String> entry : ModuleRegistrar.instance().IMCRequests.entrySet()) {
            Method callback = getCallbackMethod(entry.getKey(), entry.getValue());
            if (callback == null)
                continue;

            plugins.put(callback.getDeclaringClass(), registrar -> {
                try {
                    callback.invoke(null, ModuleRegistrar.instance());
                } catch (Exception e) {
                    Waila.LOGGER.error("Error registering wrapped plugin from {} at {}", entry.getValue(), entry.getKey());
                }
            });
        }
    }

    @Nullable
    private static Method getCallbackMethod(String method, String modname) {
        String[] splitName = method.split("\\.");
        String methodName = splitName[splitName.length - 1];
        String className = method.substring(0, method.length() - methodName.length() - 1);

        Waila.LOGGER.debug("Attempting to wrap reflected plugin at {}.{}", className, methodName);

        try {
            Class reflectClass = Class.forName(className);
            Method reflectMethod = MethodUtils.getAccessibleMethod(reflectClass, methodName, IWailaRegistrar.class);
            if (reflectMethod == null)
                throw new NullPointerException("Could not find method.");
            Waila.LOGGER.debug("Successfully wrapped plugin for {} at {}", modname, method);
            return reflectMethod;
        } catch (Exception e) {
            Waila.LOGGER.warn("Error wrapping plugin at {}: {}: {}", method, e.getClass().getSimpleName(), e.getMessage());
        }

        return null;
    }
}
