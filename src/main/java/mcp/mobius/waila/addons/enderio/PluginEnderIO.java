package mcp.mobius.waila.addons.enderio;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.IWailaRegistrar;
import mcp.mobius.waila.api.WailaPlugin;
import org.apache.logging.log4j.Level;

import java.lang.reflect.Method;

@WailaPlugin("EnderIO")
public class PluginEnderIO implements IWailaPlugin {

    public static Class<?> TileCapacitorBank = null;
    public static Method TCB_getMaxInput = null;
    public static Method TCB_getMaxOutput = null;
    public static Method TCB_getEnergyStored = null;
    public static Method TCB_getMaxEnergyStored = null;
    public static Method TCB_getMaxIO = null;

    public static Class TileTesseract = null;

    @Override
    public void register(IWailaRegistrar registrar) {
        try {

            TileCapacitorBank = Class.forName("crazypants.enderio.machine.power.TileCapacitorBank");
            TCB_getEnergyStored = TileCapacitorBank.getMethod("getEnergyStored");
            TCB_getMaxInput = TileCapacitorBank.getMethod("getMaxInput");
            TCB_getMaxOutput = TileCapacitorBank.getMethod("getMaxOutput");
            TCB_getMaxEnergyStored = TileCapacitorBank.getMethod("getMaxEnergyStored");
            TCB_getMaxIO = TileCapacitorBank.getMethod("getMaxIO");

            TileTesseract = Class.forName("crazypants.enderio.machine.hypercube.TileHyperCube");

        } catch (ClassNotFoundException e) {
            Waila.LOGGER.log(Level.WARN, "[EnderIO] Class not found. " + e);
            return;
        } catch (NoSuchMethodException e) {
            Waila.LOGGER.log(Level.WARN, "[EnderIO] Method not found." + e);
            return;
//		} catch (NoSuchFieldException e){
//			mod_Waila.log.log(Level.WARNING, "[EnderIO] Field not found." + e);
//			return;			
        } catch (Exception e) {
            Waila.LOGGER.log(Level.WARN, "[EnderIO] Unhandled exception." + e);
            return;
        }

        registrar.addConfig("EnderIO", "enderio.inout");
        registrar.addConfig("EnderIO", "enderio.storage");
        registrar.addConfig("EnderIO", "enderio.owner");
        registrar.addConfig("EnderIO", "enderio.channel");
        registrar.registerBodyProvider(new HUDHandlerCapacitor(), TileCapacitorBank);
        registrar.registerBodyProvider(new HUDHandlerTesseract(), TileTesseract);
        registrar.registerNBTProvider(new HUDHandlerCapacitor(), TileCapacitorBank);
        registrar.registerNBTProvider(new HUDHandlerTesseract(), TileTesseract);
    }

}
