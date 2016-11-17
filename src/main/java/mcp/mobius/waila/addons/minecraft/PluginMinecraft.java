package mcp.mobius.waila.addons.minecraft;

import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.IWailaRegistrar;
import mcp.mobius.waila.api.WailaPlugin;
import net.minecraft.entity.passive.AbstractChestHorse;
import net.minecraft.entity.passive.EntityVillager;

@WailaPlugin
public class PluginMinecraft implements IWailaPlugin {

    @Override
    public void register(IWailaRegistrar registrar) {
        HUDHandlerVanilla.register(registrar);
        HUDHandlerFurnace.register(registrar);

        registrar.registerBodyProvider(HUDHandlerVillager.INSTANCE, EntityVillager.class);
        registrar.registerNBTProvider(HUDHandlerVillager.INSTANCE, EntityVillager.class);

        registrar.registerBodyProvider(HUDHandlerChestEntity.INSTANCE, AbstractChestHorse.class);
        registrar.registerNBTProvider(HUDHandlerChestEntity.INSTANCE, AbstractChestHorse.class);

        registrar.addConfig("VanillaMC", "vanilla.horseinventory");
    }
}
