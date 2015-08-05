package mcp.mobius.waila.addons.railcraft;

import java.util.List;

import mcp.mobius.waila.cbcore.LangUtil;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.utils.WailaExceptionHandler;

public class HUDHandlerTank implements IWailaDataProvider {

	@Override
	public ItemStack getWailaStack(IWailaDataAccessor accessor,	IWailaConfigHandler config) {
		return null;
	}

	@Override
	public List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,	IWailaConfigHandler config) {
		if (!config.getConfig("railcraft.fluidamount")) return currenttip;	
		try {
			IFluidTank tank = (IFluidTank)RailcraftModule.ITankTile_getTank.invoke(RailcraftModule.ITankTile.cast(accessor.getTileEntity()));
			if (tank == null) return currenttip;
			
			FluidStack fluid = tank.getFluid();

			String name = currenttip.get(0);
			
			try{
				name += String.format(" < %s >", fluid.getFluid().getLocalizedName(fluid));
			} catch (NullPointerException f){
				name += " " + LangUtil.translateG("hud.msg.empty");
			}			
			
			currenttip.set(0, name);			
			
		} catch (Exception e){    
			currenttip = WailaExceptionHandler.handleErr(e, accessor.getTileEntity().getClass().getName(), currenttip);
		} 		
		
		return currenttip;
	}

	@Override
	public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,	IWailaConfigHandler config) {
		if (!config.getConfig("railcraft.fluidamount")) return currenttip;	
		
		try {
			IFluidTank tank = (IFluidTank)RailcraftModule.ITankTile_getTank.invoke(RailcraftModule.ITankTile.cast(accessor.getTileEntity()));
			if (tank == null) return currenttip;			
			
			FluidStack fluid = tank.getFluid();
			if (fluid != null)
				currenttip.add(String.format("%d / %d mB", fluid.amount, tank.getInfo().capacity));
			else
				currenttip.add(String.format("0 / %d mB", tank.getInfo().capacity));
			
		} catch (Exception e){    
			currenttip = WailaExceptionHandler.handleErr(e, accessor.getTileEntity().getClass().getName(), currenttip);
		} 		
		
		return currenttip;
	}

	@Override
	public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,	IWailaConfigHandler config) {
		return currenttip;
	}

	@Override
	public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, BlockPos pos) {
		if (te != null)
			te.writeToNBT(tag);
		return tag;
	}	
	
}
