package mcp.mobius.waila.addons.thermalexpansion;

import java.util.List;

import codechicken.lib.lang.LangUtil;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import mcp.mobius.waila.WailaExceptionHandler;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;

public class HUDHandlerConduitFluid implements IWailaDataProvider {

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

		if (config.getConfig("thermalexpansion.fluidtype")){
		
			try {
				FluidStack fluid = (FluidStack)ThermalExpansionModule.TileConduitFluid_getRenderFluid.invoke(accessor.getTileEntity());
				
				try{
					currenttip.add(fluid.getFluid().getLocalizedName());
				} catch (NullPointerException f){
					currenttip.add(LangUtil.translateG("hud.msg.empty"));
				}
				
			} catch (Exception e){    
				currenttip = WailaExceptionHandler.handleErr(e, accessor.getTileEntity().getClass().getName(), currenttip);
			} 	
			
		}
		return currenttip;
	}

	@Override
	public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,	IWailaConfigHandler config) {
		return currenttip;
	}

}
