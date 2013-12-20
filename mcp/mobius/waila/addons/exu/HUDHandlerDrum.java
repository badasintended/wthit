package mcp.mobius.waila.addons.exu;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;

public class HUDHandlerDrum implements IWailaDataProvider {

	@Override
	public ItemStack getWailaStack(IWailaDataAccessor accessor,	IWailaConfigHandler config) {
		return null;
	}

	@Override
	public List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,	IWailaConfigHandler config) {
		return currenttip;
	}

	@Override
	public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
		
		if (!config.getConfig("extrautilities.fluidamount")) return currenttip;
		
		NBTTagCompound subtag = accessor.getNBTData().getCompoundTag("tank");
		int amount = accessor.getNBTInteger(subtag, "Amount");
		
		IFluidHandler handler = (IFluidHandler)accessor.getTileEntity();
		if (handler == null) return currenttip;
		
		FluidTankInfo[] tanks = handler.getTankInfo(ForgeDirection.UNKNOWN);
		if (tanks.length != 1) return currenttip;
		
		currenttip.add(String.format("%d / %d mB", amount, tanks[0].capacity));
		
		return currenttip;
	}

	@Override
	public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,	IWailaConfigHandler config) {
		return currenttip;
	}

}
