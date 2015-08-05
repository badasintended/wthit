package mcp.mobius.waila.addons.buildcraft;

import java.util.List;

//import buildcraft.factory.TileTank;

import net.minecraft.client.renderer.EnumFaceDirection;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import org.apache.logging.log4j.Level;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.impl.ConfigHandler;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import mcp.mobius.waila.cbcore.LangUtil;

public class HUDHandlerBCTanks implements IWailaDataProvider {

	@Override
	public ItemStack getWailaStack(IWailaDataAccessor accessor,	IWailaConfigHandler config) {
		return null;
	}

	@Override
	public List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,	IWailaConfigHandler config) {
		FluidTankInfo tank  = this.getTank(accessor);
		FluidStack stack = tank != null ? tank.fluid : null; 


		String name = currenttip.get(0); 
		if (stack != null && ConfigHandler.instance().getConfig("bc.tanktype"))
			name = name + " (" + stack.getFluid().getName() + ")";
		else if (stack == null &&  ConfigHandler.instance().getConfig("bc.tanktype"))
			name = name + " " + LangUtil.translateG("hud.msg.empty");
		currenttip.set(0, name);		
		return currenttip;
	}

	@Override
	public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,	IWailaConfigHandler config) {
		FluidTankInfo tank  = this.getTank(accessor);
		FluidStack    stack = tank  != null ? tank.fluid : null;
		int liquidAmount    = stack != null ? stack.amount:0;
		int capacity        = tank  != null ? tank.capacity : 0;		
		
		if (ConfigHandler.instance().getConfig("bc.tankamount"))
			currenttip.add(String.valueOf(liquidAmount) + "/" + String.valueOf(capacity)  + " mB");
		
		return currenttip;
	}		

	@Override
	public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,	IWailaConfigHandler config) {
		return currenttip;
	}	
	
	public FluidTankInfo getTank(IWailaDataAccessor accessor){
		FluidTankInfo tank = null;
		try{
			tank = ((FluidTankInfo[])BCModule.TileTank_getTankInfo.invoke(BCModule.TileTank.cast(accessor.getTileEntity()), EnumFacing.HORIZONTALS))[0];
		} catch (Exception e){
			Waila.log.log(Level.ERROR, "[BC] Unhandled exception trying to access a tank for display !.\n" + String.valueOf(e));
			return null;
		}
		return tank;
	}
	
	@Override
	public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, BlockPos pos) {
		return tag;
	}	
	
}
