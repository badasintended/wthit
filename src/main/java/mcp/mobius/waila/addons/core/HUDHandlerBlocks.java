package mcp.mobius.waila.addons.core;

import com.google.common.base.Strings;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.impl.ConfigHandler;
import mcp.mobius.waila.handlers.VanillaTooltipHandler;
import mcp.mobius.waila.overlay.DisplayUtil;
import mcp.mobius.waila.utils.Constants;
import mcp.mobius.waila.utils.ModIdentification;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;

import java.util.List;

public class HUDHandlerBlocks implements IWailaDataProvider {

    static final IWailaDataProvider INSTANCE = new HUDHandlerBlocks();

    @Override
    public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config) {
        return null;
    }

    @Override
    public List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        if (accessor.getBlockState().getMaterial().isLiquid())
            return currenttip;

        String name = null;
        try {
            String s = DisplayUtil.itemDisplayNameShort(itemStack);
            if (s != null && !s.endsWith("Unnamed"))
                name = s;
            if (name != null)
                currenttip.add(name);
        } catch (Exception e) {}

        if (itemStack.getItem() == Items.REDSTONE) {
            int md = accessor.getMetadata();
            String s = "" + md;
            if (s.length() < 2)
                s = " " + s;
            currenttip.set(currenttip.size() - 1, name + " " + s);
        }
        if (currenttip.size() == 0)
            currenttip.add("< Unnamed >");
        else {
            String metaMetaData = String.format(
                    VanillaTooltipHandler.metaDataThroughput,
                    accessor.getBlock().getRegistryName().toString(),
                    accessor.getMetadata()
            );

            if (ConfigHandler.instance().getConfig(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_METADATA, true) && !Strings.isNullOrEmpty(VanillaTooltipHandler.metaDataWrapper)) {
                currenttip.add(String.format(VanillaTooltipHandler.metaDataWrapper, metaMetaData));
            }
        }

        return currenttip;
    }

    @Override
    public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        return currenttip;
    }

    @Override
    public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        if (accessor.getBlockState().getMaterial().isLiquid())
            return currenttip;
        String modName = ModIdentification.nameFromStack(itemStack);
        if (!Strings.isNullOrEmpty(VanillaTooltipHandler.modNameWrapper)) {
            currenttip.add(String.format(VanillaTooltipHandler.modNameWrapper, modName));
        }

        return currenttip;
    }

    @Override
    public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, BlockPos pos) {
        return tag;
    }
}
