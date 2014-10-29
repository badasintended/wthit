package mcp.mobius.waila.addons.statues;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.utils.WailaExceptionHandler;
import static mcp.mobius.waila.api.SpecialChars.*;

public class HUDHandlerStatues implements IWailaDataProvider {

	@Override
	public ItemStack getWailaStack(IWailaDataAccessor accessor,	IWailaConfigHandler config) {
		return null;
	}

	@Override
	public List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,	IWailaConfigHandler config) {
		try {
			String skinName = (String)StatuesModule.skinName.get(accessor.getTileEntity());
			if (skinName.equals("")){
				TileEntity belowEnt = accessor.getWorld().getTileEntity(accessor.getPosition().blockX, accessor.getPosition().blockY - 1, accessor.getPosition().blockZ);
				if (StatuesModule.TileEntityStatue.isInstance(belowEnt))
					skinName = (String)StatuesModule.skinName.get(belowEnt);
			}
			
			
			
			if (skinName.equals("")){
				currenttip.clear();
				currenttip.add(WHITE + "Statue : Unknown");
			} else {
				currenttip.clear();
				currenttip.add(WHITE + "Statue : " + skinName);				
			}
			
			
		} catch (Exception e) {
			currenttip = WailaExceptionHandler.handleErr(e, accessor.getTileEntity().getClass().getName(), currenttip);
		}
		
		/*
		String skinName = accessor.getNBTData().getString("skin");
		if (skinName.equals("")){
			if (accessor.getWorld().getBlockTileEntity(accessor.getPosition().blockX, accessor.getPosition().blockY - 1, accessor.getPosition().blockZ) !=  null){
				
			}
		}
		*/
		
		return currenttip;
	}

	@Override
	public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,	IWailaConfigHandler config) {
		return currenttip;
	}

	@Override
	public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,	IWailaConfigHandler config) {
		currenttip.add(BLUE + ITALIC + "Statues" );
		return currenttip;
	}

}
