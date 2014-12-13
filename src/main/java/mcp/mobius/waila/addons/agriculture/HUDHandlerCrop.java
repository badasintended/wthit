package mcp.mobius.waila.addons.agriculture;

import java.util.List;

import mcp.mobius.waila.cbcore.LangUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;

public class HUDHandlerCrop implements IWailaDataProvider {

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
		if (!config.getConfig("general.showcrop")) return currenttip;
		
		int growthStage = accessor.getMetadata();

		float growthValue = (growthStage / 6.0F) * 100.0F;
		if (growthValue < 100.0)
			currenttip.add(String.format("%s : %.0f %%", LangUtil.translateG("hud.msg.growth"), growthValue));
		else
			currenttip.add(String.format("%s : %s", LangUtil.translateG("hud.msg.growth"), LangUtil.translateG("hud.msg.mature")));
		return currenttip;
	}

	@Override
	public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,	IWailaConfigHandler config) {
		return currenttip;
	}

	@Override
	public NBTTagCompound getNBTData(TileEntity te, NBTTagCompound tag, World world, int x, int y, int z) {
		return tag;
	}	
	
}
