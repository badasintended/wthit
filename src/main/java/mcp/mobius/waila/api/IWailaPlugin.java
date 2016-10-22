package mcp.mobius.waila.api;

/**
 * Main interface used for Waila plugins. Provides a valid instance of {@link IWailaRegistrar}.
 */
public interface IWailaPlugin {

    /**
     * Called during {@link net.minecraftforge.fml.common.event.FMLLoadCompleteEvent}.
     *
     * @param registrar - An instance of IWailaRegistrar to register your providers with.
     */
    void register(IWailaRegistrar registrar);
}
