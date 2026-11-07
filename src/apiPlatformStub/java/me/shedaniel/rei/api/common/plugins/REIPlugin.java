
package me.shedaniel.rei.api.common.plugins;

import me.shedaniel.rei.api.common.registry.ReloadStage;

public interface REIPlugin<P extends REIPlugin<?>> extends Comparable<REIPlugin<P>> {

    default double getPriority() {
        throw new AssertionError("stub");
    }

    @Override
    default int compareTo(REIPlugin o) {
        throw new AssertionError("stub");
    }

    default void preStage(PluginManager<P> manager, ReloadStage stage) {
    }

    default void postStage(PluginManager<P> manager, ReloadStage stage) {
    }

}
