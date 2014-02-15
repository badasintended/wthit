package mcp.mobius.waila.api.impl;

import java.util.ArrayList;
import java.util.List;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.IWailaBlock;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.IWailaEntityProvider;
import mcp.mobius.waila.network.Packet0x01TERequest;
import mcp.mobius.waila.utils.WailaExceptionHandler;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import codechicken.nei.api.ItemInfo.Layout;
import cpw.mods.fml.common.network.PacketDispatcher;

public class MetaDataProvider{
	
	private ArrayList<IWailaDataProvider>   headBlockProviders = new ArrayList<IWailaDataProvider>();
	private ArrayList<IWailaDataProvider>   bodyBlockProviders = new ArrayList<IWailaDataProvider>();
	private ArrayList<IWailaDataProvider>   tailBlockProviders = new ArrayList<IWailaDataProvider>();
	
	private ArrayList<IWailaEntityProvider>   headEntityProviders = new ArrayList<IWailaEntityProvider>();
	private ArrayList<IWailaEntityProvider>   bodyEntityProviders = new ArrayList<IWailaEntityProvider>();
	private ArrayList<IWailaEntityProvider>   tailEntityProviders = new ArrayList<IWailaEntityProvider>();
	
	private Class prevBlock = null;
	private Class prevTile  = null;
	
	public ItemStack identifyBlockHighlight(World world, EntityPlayer player, MovingObjectPosition mop, DataAccessorBlock accessor) {
		Block block   = accessor.getBlock();
		int   blockID = accessor.getBlockID();
		
		if (IWailaBlock.class.isInstance(block)){
			try{
				return ((IWailaBlock)block).getWailaStack(accessor, ConfigHandler.instance());
			}catch (Throwable e){
				WailaExceptionHandler.handleErr(e, block.getClass().toString(), null);
			}
		}

		if(ModuleRegistrar.instance().hasStackProviders(block)){
			for (IWailaDataProvider dataProvider : ModuleRegistrar.instance().getStackProviders(block)){
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

	public List<String> handleBlockTextData(ItemStack itemStack, World world, EntityPlayer player, MovingObjectPosition mop, DataAccessorBlock accessor, List<String> currenttip, Layout layout) {
		Block block   = accessor.getBlock();
		int   blockID = accessor.getBlockID();
		
		if (accessor.getTileEntity() != null && Waila.instance.serverPresent && 
				((System.currentTimeMillis() - accessor.timeLastUpdate >= 250))){
			accessor.timeLastUpdate = System.currentTimeMillis();
			PacketDispatcher.sendPacketToServer(Packet0x01TERequest.create(world, mop));
		}

		/* Interface IWailaBlock */
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

		headBlockProviders.clear();
		bodyBlockProviders.clear();
		tailBlockProviders.clear();
		
		/* Lookup by class (for blocks)*/		
		if (layout == Layout.HEADER && ModuleRegistrar.instance().hasHeadProviders(block))
			headBlockProviders.addAll(ModuleRegistrar.instance().getHeadProviders(block));

		else if (layout == Layout.BODY && ModuleRegistrar.instance().hasBodyProviders(block))
			bodyBlockProviders.addAll(ModuleRegistrar.instance().getBodyProviders(block));

		else if (layout == Layout.FOOTER && ModuleRegistrar.instance().hasTailProviders(block))
			tailBlockProviders.addAll(ModuleRegistrar.instance().getTailProviders(block));

		
		/* Lookup by class (for tileentities)*/		
		if (layout == Layout.HEADER && ModuleRegistrar.instance().hasHeadProviders(accessor.getTileEntity()))
			headBlockProviders.addAll(ModuleRegistrar.instance().getHeadProviders(accessor.getTileEntity()));

		else if (layout == Layout.BODY && ModuleRegistrar.instance().hasBodyProviders(accessor.getTileEntity()))
			bodyBlockProviders.addAll(ModuleRegistrar.instance().getBodyProviders(accessor.getTileEntity()));
	
		else if (layout == Layout.FOOTER && ModuleRegistrar.instance().hasTailProviders(accessor.getTileEntity()))
			tailBlockProviders.addAll(ModuleRegistrar.instance().getTailProviders(accessor.getTileEntity()));
	
		/* Apply all collected providers */
		if (layout == Layout.HEADER)
			for (IWailaDataProvider dataProvider : headBlockProviders)
				try{				
					currenttip = dataProvider.getWailaHead(itemStack, currenttip, accessor, ConfigHandler.instance());
				} catch (Throwable e){
					currenttip = WailaExceptionHandler.handleErr(e, dataProvider.getClass().toString(), currenttip);
				}

		if (layout == Layout.BODY)		
			for (IWailaDataProvider dataProvider : bodyBlockProviders)
				try{				
					currenttip = dataProvider.getWailaBody(itemStack, currenttip, accessor, ConfigHandler.instance());
				} catch (Throwable e){
					currenttip = WailaExceptionHandler.handleErr(e, dataProvider.getClass().toString(), currenttip);
				}
		
		if (layout == Layout.FOOTER)	
			for (IWailaDataProvider dataProvider : tailBlockProviders)
				try{				
					currenttip = dataProvider.getWailaTail(itemStack, currenttip, accessor, ConfigHandler.instance());
				} catch (Throwable e){
					currenttip = WailaExceptionHandler.handleErr(e, dataProvider.getClass().toString(), currenttip);
				}		
		
		return currenttip;
	}
	
	public List<String> handleEntityTextData(Entity entity, World world, EntityPlayer player, MovingObjectPosition mop, DataAccessorEntity accessor, List<String> currenttip, Layout layout) {
		
		/*
		Block block   = accessor.getBlock();
		int   blockID = accessor.getBlockID();
		
		if (accessor.getTileEntity() != null && mod_Waila.instance.serverPresent && 
				((System.currentTimeMillis() - accessor.timeLastUpdate >= 250))){
			accessor.timeLastUpdate = System.currentTimeMillis();
			PacketDispatcher.sendPacketToServer(Packet0x01TERequest.create(world, mop));
		}
		*/

		headEntityProviders.clear();
		bodyEntityProviders.clear();
		tailEntityProviders.clear();
		
		/* Lookup by class (for entities)*/		
		if (layout == Layout.HEADER && ModuleRegistrar.instance().hasHeadEntityProviders(entity))
			headEntityProviders.addAll(ModuleRegistrar.instance().getHeadEntityProviders(entity));

		else if (layout == Layout.BODY && ModuleRegistrar.instance().hasBodyEntityProviders(entity))
			bodyEntityProviders.addAll(ModuleRegistrar.instance().getBodyEntityProviders(entity));

		else if (layout == Layout.FOOTER && ModuleRegistrar.instance().hasTailEntityProviders(entity))
			tailEntityProviders.addAll(ModuleRegistrar.instance().getTailEntityProviders(entity));

		/* Apply all collected providers */
		if (layout == Layout.HEADER)
			for (IWailaEntityProvider dataProvider : headEntityProviders)
				try{				
					currenttip = dataProvider.getWailaHead(entity, currenttip, accessor, ConfigHandler.instance());
				} catch (Throwable e){
					currenttip = WailaExceptionHandler.handleErr(e, dataProvider.getClass().toString(), currenttip);
				}

		if (layout == Layout.BODY)		
			for (IWailaEntityProvider dataProvider : bodyEntityProviders)
				try{				
					currenttip = dataProvider.getWailaBody(entity, currenttip, accessor, ConfigHandler.instance());
				} catch (Throwable e){
					currenttip = WailaExceptionHandler.handleErr(e, dataProvider.getClass().toString(), currenttip);
				}
		
		if (layout == Layout.FOOTER)	
			for (IWailaEntityProvider dataProvider : tailEntityProviders)
				try{				
					currenttip = dataProvider.getWailaTail(entity, currenttip, accessor, ConfigHandler.instance());
				} catch (Throwable e){
					currenttip = WailaExceptionHandler.handleErr(e, dataProvider.getClass().toString(), currenttip);
				}		
		
		return currenttip;
	}	
}
