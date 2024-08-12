package mcp.mobius.waila.api;

import java.util.Collection;
import java.util.List;

import mcp.mobius.waila.api.__internal__.IApiService;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;

/**
 * @deprecated no replacement. In practice, plugin should never require this.
 */
@Deprecated
@ApiStatus.NonExtendable
public interface IPluginInfo {

    static IPluginInfo get(ResourceLocation pluginId) {
        return IApiService.INSTANCE.getPluginInfo(pluginId);
    }

    static Collection<IPluginInfo> getAllFromMod(String modId) {
        return IApiService.INSTANCE.getAllPluginInfoFromMod(modId);
    }

    static Collection<IPluginInfo> getAll() {
        return IApiService.INSTANCE.getAllPluginInfo();
    }

    IModInfo getModInfo();

    ResourceLocation getPluginId();

    Side getSide();

    @Deprecated
    IWailaPlugin getInitializer();

    List<String> getRequiredModIds();

    boolean isEnabled();

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
        BOTH;

        public boolean matches(Side other) {
            return this == BOTH || other == BOTH || this == other;
        }
    }

}
