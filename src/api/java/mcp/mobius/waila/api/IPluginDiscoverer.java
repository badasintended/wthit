package mcp.mobius.waila.api;

import java.util.List;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;

/**
 * Dynamic plugin discoverer.
 * <p>
 * Implement this class as a service provider.
 */
@ApiStatus.Experimental
public interface IPluginDiscoverer {

    /**
     * Returns the discoverer ID.
     */
    ResourceLocation getDiscovererId();

    /**
     * Discover plugin candidates.
     * <p>
     * Will only called once per game session.
     *
     * @param candidates the plugin candidates
     */
    void discover(Candidates candidates);

    @ApiStatus.NonExtendable
    interface Candidates {

        /**
         * Adds a plugin candidate.
         *
         * @param modId          the mod origin
         * @param pluginId       the plugin id
         * @param side           the plugin environment side
         * @param requiredModIds the plugin dependencies
         * @param defaultEnabled whether the plugin is enabled by default or require end user to manually enable it
         * @param factory        the instance factory, will only be called when the environment and dependencies match
         */
        void add(String modId, ResourceLocation pluginId, IPluginInfo.Side side, List<String> requiredModIds, boolean defaultEnabled, Factory factory);

    }

    interface Factory {

        IWailaPlugin get() throws Exception;

    }

}
