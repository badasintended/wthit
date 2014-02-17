package mcp.mobius.waila.handlers;

import java.util.List;

import static mcp.mobius.waila.utils.SpecialChars.*;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.impl.ConfigHandler;
import mcp.mobius.waila.utils.Constants;
import mcp.mobius.waila.utils.ModIdentification;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.Configuration;
import codechicken.nei.forge.GuiContainerManager;

public class HUDHandlerBlocks implements IWailaDataProvider {

	@Override
	public ItemStack getWailaStack(IWailaDataAccessor accessor,	IWailaConfigHandler config) {
		return null;
	}

	@Override
	public List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,	IWailaConfigHandler config) {

        String name = null;
        try
        {
            String s = GuiContainerManager.itemDisplayNameShort(itemStack);
            if(s != null && !s.endsWith("Unnamed"))
                name = s;

            if(name != null)
                currenttip.add(name);
        }
        catch(Exception e)
        {
        }

        if(itemStack.getItem() == Item.redstone)
        {
            int md = accessor.getMetadata();
            String s = ""+md;
            if(s.length() < 2)
                s=" "+s;
            currenttip.set(currenttip.size()-1, name+" "+s);
        }		
		
		if (currenttip.size() == 0)
			currenttip.add("< Unnamed >");
		else{
			if (ConfigHandler.instance().getConfig(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_METADATA, true)){
				name = currenttip.get(0);
				//currenttip.set(0, name + String.format(" %s:%s", accessor.getBlockID(), accessor.getMetadata()));
				currenttip.set(0, name + String.format(" %s:%s", itemStack.itemID, itemStack.getItemDamage()));
			}
		}		
		return currenttip;
	}

	@Override
	public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,	IWailaConfigHandler config) {
		if (ConfigHandler.instance().getConfig(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_SHIFTBLOCK, false) && currenttip.size() > 0 && !accessor.getPlayer().isSneaking()){
			currenttip.clear();
			currenttip.add(ITALIC + "Press shift for more data");
			return currenttip;
		}
		
		return currenttip;
	}

	@Override
	public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,	IWailaConfigHandler config) {
		String modName = ModIdentification.nameFromStack(itemStack);
		if (modName != null && !modName.equals("")){
			currenttip.add(BLUE + ITALIC + modName );
		}
		
		return currenttip;
	}
}
