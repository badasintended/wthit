package mcp.mobius.waila.addons.thermalexpansion;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.fluids.FluidStack;
import mcp.mobius.waila.WailaExceptionHandler;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.cbcore.LangUtil;

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

		if (!config.getConfig("thermalexpansion.fluidtype")) return currenttip;
		
		boolean found   = false;
		FluidStack fluid = null;
		
		if (!accessor.getNBTData().hasKey("parts")) return currenttip;
		NBTTagList parts = accessor.getNBTData().getTagList("parts"); 
		for (int i = 0; i < parts.tagCount(); i++){
			NBTTagCompound subtag = (NBTTagCompound)parts.tagAt(i);
			String id = subtag.getString("id");
			
			if (id.contains("ConduitFluid")){
				found  = true;
				NBTTagCompound fluidTag = subtag.getCompoundTag("ConnFluid");
				fluid = FluidStack.loadFluidStackFromNBT(fluidTag);
			}
		}			

		if (!found) return currenttip;	
		
		if (fluid != null)
			currenttip.add(fluid.getFluid().getLocalizedName());			
		else
			currenttip.add(LangUtil.translateG("hud.msg.empty"));
		
		return currenttip;
	}

	@Override
	public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,	IWailaConfigHandler config) {
		return currenttip;
	}

}
