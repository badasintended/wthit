package mcp.mobius.waila.addons.thermalexpansion;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import mcp.mobius.waila.cbcore.LangUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
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
		if (!config.getConfig("thermalexpansion.fluidtype")) return currenttip;
		
		try {
			FluidStack fluid = (FluidStack)ThermalExpansionModule.TileTank_getTankFluid.invoke(accessor.getTileEntity());
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
		try {
			if (config.getConfig("thermalexpansion.fluidamount")){
				int amount = 0;
				if (accessor.getNBTData().hasKey("Amount"))
					amount = accessor.getNBTInteger(accessor.getNBTData(), "Amount");
				
				Integer capacity = (Integer)ThermalExpansionModule.TileTank_getTankCapacity.invoke(accessor.getTileEntity());
				
				currenttip.add(String.format("%d / %d mB", amount, capacity));
			}
			
			if (config.getConfig("thermalexpansion.tankmode")){
				Byte mode = (Byte)ThermalExpansionModule.TileTank_mode.get(accessor.getTileEntity());
				if (mode == 0)
					currenttip.add(String.format("%s : \u00a7a%s", LangUtil.translateG("hud.msg.mode"), LangUtil.translateG("hud.msg.input")));
				else if (mode == 1)
					currenttip.add(String.format("%s : \u00a7c%s", LangUtil.translateG("hud.msg.mode"), LangUtil.translateG("hud.msg.output")));
				else
					currenttip.add(String.format("Mode : Unknown (%d)", mode));
			}
			
			
			
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
	public NBTTagCompound getNBTData(TileEntity te, NBTTagCompound tag, World world, int x, int y, int z) {
		try {
			int amount = (Integer) ThermalExpansionModule.TileTank_getTankAmount.invoke(te);
			tag.setInteger("Amount", amount);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tag;
	}	
	
}
