package mcp.mobius.waila.addons.minecraft;

import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.IWailaRegistrar;
import mcp.mobius.waila.api.TagLocation;
import mcp.mobius.waila.api.WailaPlugin;
import net.minecraft.entity.passive.EntityVillager;

@WailaPlugin
public class PluginMinecraft implements IWailaPlugin {

    @Override
    public void register(IWailaRegistrar registrar) {
        HUDHandlerVanilla.register(registrar);
        HUDHandlerFurnace.register(registrar);

        registrar.registerProvider(new HUDHandlerVillager(), EntityVillager.class, TagLocation.BODY);
    }
}
