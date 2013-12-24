package mcp.mobius.waila.handlers.hud;

import java.util.ArrayList;
import java.util.List;

import mcp.mobius.waila.WailaExceptionHandler;
import mcp.mobius.waila.mod_Waila;
import mcp.mobius.waila.addons.ConfigHandler;
import mcp.mobius.waila.addons.ExternalModulesHandler;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.handlers.DataAccessor;
import mcp.mobius.waila.network.Packet0x01TERequest;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import codechicken.nei.api.ItemInfo.Layout;
import cpw.mods.fml.common.network.PacketDispatcher;

public class HUDHandlerExternal{
	
	private ArrayList<IWailaDataProvider>   headProviders = new ArrayList<IWailaDataProvider>();
	private ArrayList<IWailaDataProvider>   bodyProviders = new ArrayList<IWailaDataProvider>();
	private ArrayList<IWailaDataProvider>   tailProviders = new ArrayList<IWailaDataProvider>();
	private Class prevBlock = null;
	private Class prevTile  = null;
	
	public ItemStack identifyHighlight(World world, EntityPlayer player, MovingObjectPosition mop, DataAccessor accessor) {
		Block block   = accessor.getBlock();
		int   blockID = accessor.getBlockID();
		
		/*
		if (IWailaBlock.class.isInstance(block)){
			try{
				return ((IWailaBlock)block).getWailaStack(accessor, ConfigHandler.instance());
			}catch (Throwable e){
				WailaExceptionHandler.handleErr(e, block.getClass().toString(), null);
			}
		}
		*/

		if(ExternalModulesHandler.instance().hasStackProviders(blockID)){
			for (IWailaDataProvider dataProvider : ExternalModulesHandler.instance().getStackProviders(blockID)){
				try{
					ItemStack retval = dataProvider.getWailaStack(accessor, ConfigHandler.instance());
					if (retval != null)
						return retval;					
				}catch (Throwable e){
					WailaExceptionHandler.handleErr(e, dataProvider.getClass().toString(), null);					
				}				
			}
		}
		return null;
	}

	public List<String> handleTextData(ItemStack itemStack, World world, EntityPlayer player, MovingObjectPosition mop, DataAccessor accessor, List<String> currenttip, Layout layout) {
		Block block   = accessor.getBlock();
		int   blockID = accessor.getBlockID();
		
		if (accessor.getTileEntity() != null && mod_Waila.instance.serverPresent && 
				((System.currentTimeMillis() - accessor.timeLastUpdate >= 250))){
			accessor.timeLastUpdate = System.currentTimeMillis();
			PacketDispatcher.sendPacketToServer(Packet0x01TERequest.create(world, mop));
		}

		/* Interface IWailaBlock */
		/*
		if (IWailaBlock.class.isInstance(block)){
			TileEntity entity = world.getBlockTileEntity(mop.blockX, mop.blockY, mop.blockZ);
			if (layout == Layout.HEADER)
				try{				
					return ((IWailaBlock)block).getWailaHead(itemStack, currenttip, accessor, ConfigHandler.instance());
				} catch (Throwable e){
					return WailaExceptionHandler.handleErr(e, block.getClass().toString(), currenttip);
				}					
			else if (layout == Layout.BODY)
				try{					
					return ((IWailaBlock)block).getWailaBody(itemStack, currenttip, accessor, ConfigHandler.instance());
				} catch (Throwable e){
					return WailaExceptionHandler.handleErr(e, block.getClass().toString(), currenttip);
				}
			else if (layout == Layout.FOOTER)
				try{					
					return ((IWailaBlock)block).getWailaTail(itemStack, currenttip, accessor, ConfigHandler.instance());
				} catch (Throwable e){
					return WailaExceptionHandler.handleErr(e, block.getClass().toString(), currenttip);
				}				
		}		
		*/

		headProviders.clear();
		bodyProviders.clear();
		tailProviders.clear();
		
		/* Lookup by class (for blocks)*/		
		if (layout == Layout.HEADER && ExternalModulesHandler.instance().hasHeadProviders(block))
			headProviders.addAll(ExternalModulesHandler.instance().getHeadProviders(block));

		else if (layout == Layout.BODY && ExternalModulesHandler.instance().hasBodyProviders(block))
			bodyProviders.addAll(ExternalModulesHandler.instance().getBodyProviders(block));

		else if (layout == Layout.FOOTER && ExternalModulesHandler.instance().hasTailProviders(block))
			tailProviders.addAll(ExternalModulesHandler.instance().getTailProviders(block));

		
		/* Lookup by class (for tileentities)*/		
		if (layout == Layout.HEADER && ExternalModulesHandler.instance().hasHeadProviders(accessor.getTileEntity()))
			headProviders.addAll(ExternalModulesHandler.instance().getHeadProviders(accessor.getTileEntity()));

		else if (layout == Layout.BODY && ExternalModulesHandler.instance().hasBodyProviders(accessor.getTileEntity()))
			bodyProviders.addAll(ExternalModulesHandler.instance().getBodyProviders(accessor.getTileEntity()));
	
		else if (layout == Layout.FOOTER && ExternalModulesHandler.instance().hasTailProviders(accessor.getTileEntity()))
			tailProviders.addAll(ExternalModulesHandler.instance().getTailProviders(accessor.getTileEntity()));
	
		/* Apply all collected providers */
		if (layout == Layout.HEADER)
			for (IWailaDataProvider dataProvider : headProviders)
				try{				
					currenttip = dataProvider.getWailaHead(itemStack, currenttip, accessor, ConfigHandler.instance());
				} catch (Throwable e){
					currenttip = WailaExceptionHandler.handleErr(e, dataProvider.getClass().toString(), currenttip);
				}

		if (layout == Layout.BODY)		
			for (IWailaDataProvider dataProvider : bodyProviders)
				try{				
					currenttip = dataProvider.getWailaBody(itemStack, currenttip, accessor, ConfigHandler.instance());
				} catch (Throwable e){
					currenttip = WailaExceptionHandler.handleErr(e, dataProvider.getClass().toString(), currenttip);
				}
		
		if (layout == Layout.FOOTER)	
			for (IWailaDataProvider dataProvider : tailProviders)
				try{				
					currenttip = dataProvider.getWailaTail(itemStack, currenttip, accessor, ConfigHandler.instance());
				} catch (Throwable e){
					currenttip = WailaExceptionHandler.handleErr(e, dataProvider.getClass().toString(), currenttip);
				}		
		
		return currenttip;
	}
}
