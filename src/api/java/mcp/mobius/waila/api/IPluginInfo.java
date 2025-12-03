package mcp.mobius.waila.api;

import java.util.Collection;
import java.util.List;

import mcp.mobius.waila.api.__internal__.IApiService;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

/**
 * @deprecated no replacement. In practice, plugin should never require this.
 */
@Deprecated
@ApiStatus.NonExtendable
public interface IPluginInfo {

    static @Nullable IPluginInfo get(Identifier pluginId) {
        return IApiService.INSTANCE.getPluginInfo(pluginId);
    }

    static Collection<IPluginInfo> getAllFromMod(String modId) {
        return IApiService.INSTANCE.getAllPluginInfoFromMod(modId);
    }

    static Collection<IPluginInfo> getAll() {
        return IApiService.INSTANCE.getAllPluginInfo();
    }

    IModInfo getModInfo();

    Identifier getPluginId();

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
