package mcp.mobius.waila.addons.railcraft;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.IWailaRegistrar;
import mcp.mobius.waila.api.WailaPlugin;
import org.apache.logging.log4j.Level;

import java.lang.reflect.Method;

@WailaPlugin("railcraft")
public class PluginRailcraft implements IWailaPlugin {
    public static Class<?> TileTankBase = null;
    public static Class<?> ITankTile = null;
    public static Method ITankTile_getTank = null;
    //public static Class  StandardTank = null;

    @Override
    public void register(IWailaRegistrar registrar) {
        try {
            TileTankBase = Class.forName("mods.railcraft.common.blocks.machine.beta.TileTankBase");
            ITankTile = Class.forName("mods.railcraft.common.blocks.machine.ITankTile");
            ITankTile_getTank = ITankTile.getMethod("getTank");
            //StandardTank = Class.forName("mods.railcraft.common.fluids.tanks.StandardTank");

        } catch (ClassNotFoundException e) {
            Waila.LOGGER.log(Level.WARN, "[Railcraft] Class not found. " + e);
            return;
        } catch (NoSuchMethodException e) {
            Waila.LOGGER.log(Level.WARN, "[Railcraft] Method not found." + e);
            return;
        }

        registrar.addConfigRemote("Railcraft", "railcraft.fluidamount");
        registrar.registerBodyProvider(new HUDHandlerTank(), TileTankBase);
        registrar.registerHeadProvider(new HUDHandlerTank(), TileTankBase);
        registrar.registerNBTProvider(new HUDHandlerTank(), TileTankBase);
    }
}
