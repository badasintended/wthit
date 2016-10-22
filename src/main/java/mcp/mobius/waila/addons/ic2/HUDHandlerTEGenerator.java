package mcp.mobius.waila.addons.ic2;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.impl.ConfigHandler;
import mcp.mobius.waila.cbcore.LangUtil;
import mcp.mobius.waila.utils.WailaExceptionHandler;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.List;

import static mcp.mobius.waila.api.SpecialChars.ALIGNRIGHT;
import static mcp.mobius.waila.api.SpecialChars.TAB;

public class HUDHandlerTEGenerator implements IWailaDataProvider {

    @Override
    public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config) {
        return null;
    }

    @Override
    public List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        return currenttip;
    }

    @Override
    public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        try {
            double storage = accessor.getNBTData().getDouble("storage");
            int production = accessor.getNBTData().getInteger("production");
            long maxStorage = accessor.getNBTData().getLong("maxStorage");

            String storedStr = LangUtil.translateG("hud.msg.stored");
            String outputStr = LangUtil.translateG("hud.msg.output");

			/* EU Storage */
            if (ConfigHandler.instance().getConfig("ic2.storage")) {
                if (maxStorage > 0)
                    currenttip.add(String.format("%s%s\u00a7f%d\u00a7r / \u00a7f%d\u00a7r EU", storedStr, TAB + ALIGNRIGHT, Math.round(Math.min(storage, maxStorage)), maxStorage));
            }

            if (ConfigHandler.instance().getConfig("ic2.outputeu")) {
                currenttip.add(String.format("%s%s\u00a7f%d\u00a7r EU/t", outputStr, TAB + ALIGNRIGHT, production));
            }

        } catch (Exception e) {
            currenttip = WailaExceptionHandler.handleErr(e, accessor.getTileEntity().getClass().getName(), currenttip);
        }

        return currenttip;
    }

    @Override
    public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        return currenttip;
    }

    @Override
    public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, BlockPos pos) {

        try {
            double storage = -1;
            long production = -1;
            long maxStorage = -1;

            if (PluginIC2.TileBaseGenerator.isInstance(te)) {
                storage = PluginIC2.TileBaseGenerator_storage.getDouble(te);
                production = (long) PluginIC2.TileBaseGenerator_production.getInt(te);
                maxStorage = (long) PluginIC2.TileBaseGenerator_maxStorage.getShort(te);
            } else if (PluginIC2.TileGeoGenerator.isInstance(te)) {
                storage = PluginIC2.TileGeoGenerator_storage.getDouble(te);
                production = (long) PluginIC2.TileGeoGenerator_production.getInt(te);
                maxStorage = (long) PluginIC2.TileGeoGenerator_maxStorage.getShort(te);
            } else if (PluginIC2.TileKineticGenerator.isInstance(te)) {
                storage = PluginIC2.TileKineticGenerator_storage.getDouble(te);
                production = MathHelper.floor_double_long(PluginIC2.TileKineticGenerator_production.getDouble(te));
                maxStorage = (long) PluginIC2.TileKineticGenerator_maxStorage.getInt(te);
            } else if (PluginIC2.TileSemifluidGenerator.isInstance(te)) {
                storage = PluginIC2.TileSemifluidGenerator_storage.getDouble(te);
                production = MathHelper.floor_double_long(PluginIC2.TileSemifluidGenerator_production.getDouble(te));
                maxStorage = (long) PluginIC2.TileSemifluidGenerator_maxStorage.getShort(te);
            } else if (PluginIC2.TileStirlingGenerator.isInstance(te)) {
                storage = PluginIC2.TileStirlingGenerator_storage.getDouble(te);
                production = MathHelper.floor_double_long(PluginIC2.TileStirlingGenerator_production.getDouble(te));
                maxStorage = (long) PluginIC2.TileStirlingGenerator_maxStorage.getShort(te);
            }

            tag.setDouble("storage", storage);
            tag.setLong("production", production);
            tag.setLong("maxStorage", maxStorage);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return tag;
    }

}
