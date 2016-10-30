package mcp.mobius.waila.addons.harvestcraft;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.IWailaRegistrar;
import mcp.mobius.waila.api.WailaPlugin;
import org.apache.logging.log4j.Level;

@WailaPlugin("harvestcraft")
public class PluginHarvestcraft implements IWailaPlugin {

    public static Class<?> TileEntityPamCrop = null;

    @Override
    public void register(IWailaRegistrar registrar) {
        try {
            TileEntityPamCrop = Class.forName("assets.pamharvestcraft.TileEntityPamCrop");
        } catch (ClassNotFoundException e) {
            Waila.LOGGER.log(Level.WARN, "[PamHarvestCraft] Class not found. " + e);
            return;
        }

        registrar.registerBodyProvider(new HUDHandlerPamCrop(), TileEntityPamCrop);
    }
}
