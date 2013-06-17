package mcp.mobius.waila.addons.buildcraft;

import java.util.List;
import java.util.logging.Level;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.liquids.ILiquidTank;
import mcp.mobius.waila.mod_Waila;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;

public class HUDHandlerBCIPowerReceptor implements IWailaDataProvider {

	public HUDHandlerBCIPowerReceptor() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public ItemStack getWailaStack(IWailaDataAccessor accessor,	IWailaConfigHandler config) {
		return null;
	}

	@Override
	public List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,	IWailaConfigHandler config) {
		return currenttip;
	}

	@Override
	public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,	IWailaConfigHandler config) {
//		int powerRequest = 0;
//		try{
//			powerRequest = (Integer)BCModule.IPowerReceptor_PowerRequest.invoke(BCModule.IPowerReceptor.cast(accessor.getTileEntity()), ForgeDirection.UNKNOWN);
//		}
//		catch (Exception e){
//			mod_Waila.log.log(Level.SEVERE, "[BC] Unhandled exception trying to access a tank for display !.\n" + String.valueOf(e));
//			return currenttip;
//		}
//		currenttip.add(String.valueOf(powerRequest));
		
		NBTTagCompound tag = accessor.getNBTData();
		
		return currenttip;
	}

}
