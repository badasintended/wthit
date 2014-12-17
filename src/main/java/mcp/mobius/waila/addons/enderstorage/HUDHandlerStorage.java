package mcp.mobius.waila.addons.enderstorage;

import java.util.List;

import mcp.mobius.waila.cbcore.LangUtil;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.utils.WailaExceptionHandler;

public class HUDHandlerStorage implements IWailaDataProvider {
	
	private static String[] colors = {
		LangUtil.translateG("hud.msg.white"),
		LangUtil.translateG("hud.msg.orange"),
		LangUtil.translateG("hud.msg.magenta"),
		LangUtil.translateG("hud.msg.lblue"),
		LangUtil.translateG("hud.msg.yellow"),
		LangUtil.translateG("hud.msg.lime"),
		LangUtil.translateG("hud.msg.pink"),
		LangUtil.translateG("hud.msg.gray"),
		LangUtil.translateG("hud.msg.lgray"),
		LangUtil.translateG("hud.msg.cyan"),
		LangUtil.translateG("hud.msg.purple"),
		LangUtil.translateG("hud.msg.blue"),
		LangUtil.translateG("hud.msg.brown"),
		LangUtil.translateG("hud.msg.green"),
		LangUtil.translateG("hud.msg.red"),
		LangUtil.translateG("hud.msg.black")		
	};
	
	@Override
	public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config) {
		return null;
	}

	@Override
	public List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
		return currenttip;
	}

	@Override
	public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
		if(config.getConfig("enderstorage.colors")){
			try{
				
				int freq = EnderStorageModule.TileFrequencyOwner_Freq.getInt(accessor.getTileEntity());
				int freqLeft   = (Integer)EnderStorageModule.GetColourFromFreq.invoke(null, freq, 0); 
				int freqCenter = (Integer)EnderStorageModule.GetColourFromFreq.invoke(null, freq, 1);
				int freqRight  = (Integer)EnderStorageModule.GetColourFromFreq.invoke(null, freq, 2);
				
				if (!EnderStorageModule.TileEnderTank.isInstance(accessor.getTileEntity()))
					currenttip.add(String.format("%s/%s/%s", colors[freqLeft], colors[freqCenter], colors[freqRight]));
				else
					currenttip.add(String.format("%s/%s/%s", colors[freqRight], colors[freqCenter], colors[freqLeft]));
				
				
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
	
	@Override
	public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, int x, int y, int z) {
		if (te != null)
			te.writeToNBT(tag);
		return tag;
	}	
	
}
