package mcp.mobius.waila.api.impl;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.IWailaEntityProvider;
import mcp.mobius.waila.api.TagLocation;
import mcp.mobius.waila.cbcore.Layout;
import mcp.mobius.waila.network.Message0x01TERequest;
import mcp.mobius.waila.network.Message0x03EntRequest;
import mcp.mobius.waila.network.WailaPacketHandler;
import mcp.mobius.waila.utils.WailaExceptionHandler;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class MetaDataProvider {

    private Map<Integer, List<IWailaDataProvider>> headBlockProviders = new TreeMap<Integer, List<IWailaDataProvider>>();
    private Map<Integer, List<IWailaDataProvider>> bodyBlockProviders = new TreeMap<Integer, List<IWailaDataProvider>>();
    private Map<Integer, List<IWailaDataProvider>> tailBlockProviders = new TreeMap<Integer, List<IWailaDataProvider>>();

    private Map<Integer, List<IWailaEntityProvider>> headEntityProviders = new TreeMap<Integer, List<IWailaEntityProvider>>();
    private Map<Integer, List<IWailaEntityProvider>> bodyEntityProviders = new TreeMap<Integer, List<IWailaEntityProvider>>();
    private Map<Integer, List<IWailaEntityProvider>> tailEntityProviders = new TreeMap<Integer, List<IWailaEntityProvider>>();

    private Class prevBlock = null;
    private Class prevTile = null;

    public ItemStack identifyBlockHighlight(World world, EntityPlayer player, RayTraceResult mop, DataAccessorCommon accessor) {
        Block block = accessor.getBlock();
        int blockID = accessor.getBlockID();

        if (ModuleRegistrar.instance().hasProviders(block, TagLocation.STACK)) {
            for (List<IWailaDataProvider> providerList : ModuleRegistrar.instance().getBlockProviders(block, TagLocation.STACK).values()) {
                for (IWailaDataProvider dataProvider : providerList) {
                    try {
                        ItemStack retval = dataProvider.getWailaStack(accessor, ConfigHandler.instance());
                        if (retval != null)
                            return retval;
                    } catch (Throwable e) {
                        WailaExceptionHandler.handleErr(e, dataProvider.getClass().toString(), null);
                    }
                }
            }
        }
        return null;
    }

    public List<String> handleBlockTextData(ItemStack itemStack, World world, EntityPlayer player, RayTraceResult mop, DataAccessorCommon accessor, List<String> currenttip, Layout layout) {
        Block block = accessor.getBlock();

        if (accessor.getTileEntity() != null && Waila.instance.serverPresent && accessor.isTimeElapsed(250) && ConfigHandler.instance().showTooltip()) {
            accessor.resetTimer();
            HashSet<String> keys = new HashSet<String>();

            if (keys.size() != 0 || ModuleRegistrar.instance().hasProviders(block, TagLocation.DATA) || ModuleRegistrar.instance().hasProviders(accessor.getTileEntity(), TagLocation.DATA))
                WailaPacketHandler.INSTANCE.sendToServer(new Message0x01TERequest(accessor.getTileEntity(), keys));

        } else if (accessor.getTileEntity() != null && !Waila.instance.serverPresent && accessor.isTimeElapsed(250) && ConfigHandler.instance().showTooltip()) {

            try {
                NBTTagCompound tag = new NBTTagCompound();
                accessor.getTileEntity().writeToNBT(tag);
                accessor.setNBTData(tag);
            } catch (Exception e) {
                WailaExceptionHandler.handleErr(e, this.getClass().getName(), null);
            }
        }

        headBlockProviders.clear();
        bodyBlockProviders.clear();
        tailBlockProviders.clear();

		/* Lookup by class (for blocks)*/
        if (layout == Layout.HEADER && ModuleRegistrar.instance().hasProviders(block, TagLocation.HEAD))
            headBlockProviders.putAll(ModuleRegistrar.instance().getBlockProviders(block, TagLocation.HEAD));

        else if (layout == Layout.BODY && ModuleRegistrar.instance().hasProviders(block, TagLocation.BODY))
            bodyBlockProviders.putAll(ModuleRegistrar.instance().getBlockProviders(block, TagLocation.BODY));

        else if (layout == Layout.FOOTER && ModuleRegistrar.instance().hasProviders(block, TagLocation.TAIL))
            tailBlockProviders.putAll(ModuleRegistrar.instance().getBlockProviders(block, TagLocation.TAIL));

		
		/* Lookup by class (for tileentities)*/
        if (layout == Layout.HEADER && ModuleRegistrar.instance().hasProviders(accessor.getTileEntity(), TagLocation.HEAD))
            headBlockProviders.putAll(ModuleRegistrar.instance().getTileProviders(accessor.getTileEntity(), TagLocation.HEAD));

        else if (layout == Layout.BODY && ModuleRegistrar.instance().hasProviders(accessor.getTileEntity(), TagLocation.BODY))
            bodyBlockProviders.putAll(ModuleRegistrar.instance().getTileProviders(accessor.getTileEntity(), TagLocation.BODY));

        else if (layout == Layout.FOOTER && ModuleRegistrar.instance().hasProviders(accessor.getTileEntity(), TagLocation.TAIL))
            tailBlockProviders.putAll(ModuleRegistrar.instance().getTileProviders(accessor.getTileEntity(), TagLocation.TAIL));
	
		/* Apply all collected providers */
        if (layout == Layout.HEADER)
            for (List<IWailaDataProvider> providersList : headBlockProviders.values()) {
                for (IWailaDataProvider dataProvider : providersList)
                    try {
                        currenttip = dataProvider.getWailaHead(itemStack, currenttip, accessor, ConfigHandler.instance());
                    } catch (Throwable e) {
                        currenttip = WailaExceptionHandler.handleErr(e, dataProvider.getClass().toString(), currenttip);
                    }
            }

        if (layout == Layout.BODY)
            for (List<IWailaDataProvider> providersList : bodyBlockProviders.values()) {
                for (IWailaDataProvider dataProvider : providersList)
                    try {
                        currenttip = dataProvider.getWailaBody(itemStack, currenttip, accessor, ConfigHandler.instance());
                    } catch (Throwable e) {
                        currenttip = WailaExceptionHandler.handleErr(e, dataProvider.getClass().toString(), currenttip);
                    }
            }
        if (layout == Layout.FOOTER)
            for (List<IWailaDataProvider> providersList : tailBlockProviders.values()) {
                for (IWailaDataProvider dataProvider : providersList)
                    try {
                        currenttip = dataProvider.getWailaTail(itemStack, currenttip, accessor, ConfigHandler.instance());
                    } catch (Throwable e) {
                        currenttip = WailaExceptionHandler.handleErr(e, dataProvider.getClass().toString(), currenttip);
                    }
            }
        return currenttip;
    }

    public List<String> handleEntityTextData(Entity entity, World world, EntityPlayer player, RayTraceResult mop, DataAccessorCommon accessor, List<String> currenttip, Layout layout) {

        if (accessor.getEntity() != null && Waila.instance.serverPresent && accessor.isTimeElapsed(250)) {
            accessor.resetTimer();
            HashSet<String> keys = new HashSet<String>();

            if (keys.size() != 0 || ModuleRegistrar.instance().hasProviders(entity, TagLocation.DATA) || ModuleRegistrar.instance().hasProviders(accessor.getEntity(), TagLocation.HEAD))
                WailaPacketHandler.INSTANCE.sendToServer(new Message0x03EntRequest(accessor.getEntity(), keys));

        } else if (accessor.getEntity() != null && !Waila.instance.serverPresent && accessor.isTimeElapsed(250)) {

            try {
                NBTTagCompound tag = new NBTTagCompound();
                accessor.getEntity().writeToNBT(tag);
                accessor.remoteNbt = tag;
            } catch (Exception e) {
                WailaExceptionHandler.handleErr(e, this.getClass().getName(), null);
            }
        }

        headEntityProviders.clear();
        bodyEntityProviders.clear();
        tailEntityProviders.clear();
		
		/* Lookup by class (for entities)*/
        if (layout == Layout.HEADER && ModuleRegistrar.instance().hasProviders(entity, TagLocation.HEAD))
            headEntityProviders.putAll(ModuleRegistrar.instance().getEntityProviders(entity, TagLocation.HEAD));

        else if (layout == Layout.BODY && ModuleRegistrar.instance().hasProviders(entity, TagLocation.BODY))
            bodyEntityProviders.putAll(ModuleRegistrar.instance().getEntityProviders(entity, TagLocation.BODY));

        else if (layout == Layout.FOOTER && ModuleRegistrar.instance().hasProviders(entity, TagLocation.TAIL))
            tailEntityProviders.putAll(ModuleRegistrar.instance().getEntityProviders(entity, TagLocation.TAIL));

		/* Apply all collected providers */
        if (layout == Layout.HEADER)
            for (List<IWailaEntityProvider> providersList : headEntityProviders.values()) {
                for (IWailaEntityProvider dataProvider : providersList)
                    try {
                        currenttip = dataProvider.getWailaHead(entity, currenttip, accessor, ConfigHandler.instance());
                    } catch (Throwable e) {
                        currenttip = WailaExceptionHandler.handleErr(e, dataProvider.getClass().toString(), currenttip);
                    }
            }

        if (layout == Layout.BODY)
            for (List<IWailaEntityProvider> providersList : bodyEntityProviders.values()) {
                for (IWailaEntityProvider dataProvider : providersList)
                    try {
                        currenttip = dataProvider.getWailaBody(entity, currenttip, accessor, ConfigHandler.instance());
                    } catch (Throwable e) {
                        currenttip = WailaExceptionHandler.handleErr(e, dataProvider.getClass().toString(), currenttip);
                    }
            }

        if (layout == Layout.FOOTER)
            for (List<IWailaEntityProvider> providersList : tailEntityProviders.values()) {
                for (IWailaEntityProvider dataProvider : providersList)
                    try {
                        currenttip = dataProvider.getWailaTail(entity, currenttip, accessor, ConfigHandler.instance());
                    } catch (Throwable e) {
                        currenttip = WailaExceptionHandler.handleErr(e, dataProvider.getClass().toString(), currenttip);
                    }
            }

        return currenttip;
    }
}
