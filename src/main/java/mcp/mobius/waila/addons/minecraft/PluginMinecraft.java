package mcp.mobius.waila.addons.minecraft;

import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.IWailaRegistrar;
import mcp.mobius.waila.api.WailaPlugin;
import net.minecraft.tileentity.TileEntityFurnace;

@WailaPlugin
public class PluginMinecraft implements IWailaPlugin {

    @Override
    public void register(IWailaRegistrar registrar) {
        HUDHandlerVanilla.register();
        
        registrar.registerBodyProvider(new HUDHandlerFurnace(), TileEntityFurnace.class);
        registrar.registerNBTProvider(new HUDHandlerFurnace(), TileEntityFurnace.class);
    }
}
