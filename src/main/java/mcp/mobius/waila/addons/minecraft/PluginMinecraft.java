package mcp.mobius.waila.addons.minecraft;

import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.IWailaRegistrar;
import mcp.mobius.waila.api.WailaPlugin;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.tileentity.TileEntityFurnace;

@WailaPlugin
public class PluginMinecraft implements IWailaPlugin {

    @Override
    public void register(IWailaRegistrar registrar) {
        HUDHandlerVanilla.register(registrar);
        HUDHandlerFurnace.register(registrar);

        registrar.registerBodyProvider(new HUDHandlerVillager(), EntityVillager.class);
    }
}
