package mcp.mobius.waila.addons.stevescarts;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaEntityAccessor;
import mcp.mobius.waila.api.IWailaEntityProvider;
import mcp.mobius.waila.utils.WailaExceptionHandler;
import static mcp.mobius.waila.utils.SpecialChars.*;

public class HUDMinecartModular implements IWailaEntityProvider {

	@Override
	public Entity getWailaOverride(IWailaEntityAccessor accessor, IWailaConfigHandler config) {
		return null;
	}

	@Override
	public List<String> getWailaHead(Entity entity, List<String> currenttip, IWailaEntityAccessor accessor, IWailaConfigHandler config) {
		if (currenttip.get(0).contains("entity.StevesCarts.Minecart")){
			currenttip.remove(0);
			currenttip.add(WHITE + "Modular Minecart");
		}
		
		return currenttip;
	}

	@Override
	public List<String> getWailaBody(Entity entity, List<String> currenttip, IWailaEntityAccessor accessor, IWailaConfigHandler config) {
		if (!config.getConfig("stevescarts.showall")) return currenttip;
		if (config.getConfig("stevescarts.shifttoggle") && !accessor.getPlayer().isSneaking()){
			currenttip.add(ITALIC + "Press shift for more data");
			return currenttip;			
		}
		
		Item ItemCartModule = null;
		try {
			ItemCartModule = (Item)StevesCartsModule.ItemCartModule.get(null);
		} catch (Exception e) {
			currenttip = WailaExceptionHandler.handleErr(e, accessor.getEntity().getClass().getName(), currenttip);
			return currenttip;
		}		
		
		NBTTagCompound tag = accessor.getNBTData();
		if (tag.hasKey("Modules")){
			byte[] metas = tag.getByteArray("Modules"); 
			
			for (int i = 0; i < metas.length; i++){
				if (tag.hasKey("module" + String.valueOf(i) + "Fuel")){
					
					int fuel = accessor.getNBTInteger(tag, "module" + String.valueOf(i) + "Fuel");
					currenttip.add(new ItemStack(ItemCartModule, 1, metas[i]).getDisplayName() + TAB + ALIGNRIGHT + " [ " + WHITE + fuel + GRAY + " POW ]");
					
				} else if (tag.hasKey("module" + String.valueOf(i) + "Fluid")){
					
					NBTTagCompound subtag = tag.getCompoundTag("module" + String.valueOf(i) + "Fluid");
					if (subtag.hasKey("amount")){
						int amount = accessor.getNBTInteger(subtag, "Amount");
						String fluid = subtag.getString("FluidName"); 
						currenttip.add(new ItemStack(ItemCartModule, 1, metas[i]).getDisplayName() + TAB + ALIGNRIGHT + " [ " + WHITE + String.valueOf(amount) + GRAY + " mB of " + WHITE + fluid + GRAY + " ]");
					} else {
						currenttip.add(new ItemStack(ItemCartModule, 1, metas[i]).getDisplayName() + TAB + ALIGNRIGHT + " < " + WHITE + "Empty" + GRAY + " > ");
					}
				
				}else{
					
					currenttip.add(new ItemStack(ItemCartModule, 1, metas[i]).getDisplayName());
					
				}
			}			
		}
		
		return currenttip;
	}

	@Override
	public List<String> getWailaTail(Entity entity, List<String> currenttip, IWailaEntityAccessor accessor, IWailaConfigHandler config) {
		return currenttip;
	}

}
