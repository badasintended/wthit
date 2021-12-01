package mcp.mobius.waila.api;

import java.util.Collection;
import java.util.List;

import mcp.mobius.waila.impl.Impl;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.NonExtendable
public interface IPluginInfo {

    static IPluginInfo get(ResourceLocation pluginId) {
        return Impl.get(IPluginInfo.class, 0, pluginId);
    }

    static Collection<IPluginInfo> getAllFromMod(String modId) {
        return Impl.get(IPluginInfo.class, 1, modId);
    }

    static Collection<IPluginInfo> getAll() {
        return Impl.get(IPluginInfo.class, 2);
    }

    IModInfo getModInfo();

    ResourceLocation getPluginId();

    Side getSide();

    IWailaPlugin getInitializer();

    List<String> getRequiredModIds();

    enum Side {
        /**
         * This plugin only loaded on the client jar.
         */
        CLIENT,

        /**
         * This plugin only loaded on the dedicated server jar.
         */
        SERVER,

        /**
         * This plugin loaded on both client and server jar.
         */
        BOTH
    }

}
