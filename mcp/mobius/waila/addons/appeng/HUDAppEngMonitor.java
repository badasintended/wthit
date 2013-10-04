package mcp.mobius.waila.addons.appeng;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;

public class HUDAppEngMonitor implements IWailaDataProvider {

	@Override
	public ItemStack getWailaStack(IWailaDataAccessor accessor,	IWailaConfigHandler config) {
		return null;
	}

	@Override
	public List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
		return currenttip;
	}

	@Override
	public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,	IWailaConfigHandler config) {
		TileEntity entity = accessor.getTileEntity();
		if (entity != null){
			if (config.getConfig("appeng.monitorcontent")){
				try {
					ItemStack stack = (ItemStack)AppEngModule.IAEItemStack_getItemStack.invoke(AppEngModule.IAEItemStack.cast(AppEngModule.TileStorageMonitor_getItem.invoke(entity)));
					if (stack != null)
						currenttip.add(stack.getDisplayName());
					else
						currenttip.add("<None>");
				} catch (Exception e) {
					System.out.printf("%s\n", e);
				}			
			}
		}
		
		return currenttip;
	}

	@Override
	public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,	IWailaConfigHandler config) {
		return currenttip;
	}	
	
}
