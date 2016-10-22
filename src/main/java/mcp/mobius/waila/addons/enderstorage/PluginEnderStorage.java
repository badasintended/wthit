package mcp.mobius.waila.addons.enderstorage;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.IWailaRegistrar;
import mcp.mobius.waila.api.WailaPlugin;
import org.apache.logging.log4j.Level;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

@WailaPlugin("EnderStorage")
public class PluginEnderStorage implements IWailaPlugin {

    public static Class<?> TileFrequencyOwner = null;
    public static Field TileFrequencyOwner_Freq = null;

    public static Class<?> EnderStorageManager = null;
    public static Method GetColourFromFreq = null;

    public static Class<?> TileEnderTank = null;

    @Override
    public void register(IWailaRegistrar registrar) {
        try {

            TileFrequencyOwner = Class.forName("codechicken.enderstorage.common.TileFrequencyOwner");
            TileFrequencyOwner_Freq = TileFrequencyOwner.getField("freq");

            EnderStorageManager = Class.forName("codechicken.enderstorage.api.EnderStorageManager");
            GetColourFromFreq = EnderStorageManager.getDeclaredMethod("getColourFromFreq", Integer.TYPE, Integer.TYPE);

            TileEnderTank = Class.forName("codechicken.enderstorage.storage.liquid.TileEnderTank");

        } catch (ClassNotFoundException e) {
            Waila.log.log(Level.WARN, "[EnderStorage] Class not found. " + e);
            return;
        } catch (NoSuchMethodException e) {
            Waila.log.log(Level.WARN, "[EnderStorage] Method not found." + e);
            return;
        } catch (NoSuchFieldException e) {
            Waila.log.log(Level.WARN, "[EnderStorage] Field not found." + e);
            return;
        } catch (Exception e) {
            Waila.log.log(Level.WARN, "[EnderStorage] Unhandled exception." + e);
            return;
        }

        registrar.addConfig("EnderStorage", "enderstorage.colors");
        registrar.registerBodyProvider(new HUDHandlerStorage(), TileFrequencyOwner);
        registrar.registerNBTProvider(new HUDHandlerStorage(), TileFrequencyOwner);
    }
}
