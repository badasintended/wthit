package mcp.mobius.waila.handlers;

import java.util.ArrayList;
import java.util.List;

import static mcp.mobius.waila.SpecialChars.*;
import mcp.mobius.waila.Constants;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.tools.ModIdentification;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class HUDHandlerWaila implements IWailaDataProvider {

	//TODO : Redo id:metadata ??
	
	@Override
	public ItemStack getWailaStack(IWailaDataAccessor accessor,	IWailaConfigHandler config) {
		return null;
	}

	@Override
	public List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,	IWailaConfigHandler config) {

        String name = null;
        try
        {
            String s = itemDisplayNameShort(itemStack);
            if(s != null && !s.endsWith("Unnamed"))
                name = s;

            if(name != null)
                currenttip.add(name);
        }
        catch(Exception e)
        {
        }

        if(itemStack.getItem() == Items.redstone)
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
			if (config.getConfig(Constants.CFG_WAILA_METADATA)){
				name = currenttip.get(0);
				//currenttip.set(0, name + String.format(" %s:%s", accessor.getBlockID(), accessor.getMetadata()));
			}
		}		
		return currenttip;
	}

	@Override
	public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,	IWailaConfigHandler config) {
		return currenttip;
	}

	@Override
	public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,	IWailaConfigHandler config) {
		String modName = ModIdentification.nameFromStack(itemStack);
		if (modName != null && !modName.equals(""))
			currenttip.add(BLUE + ITALIC + modName);
		
		return currenttip;
	}
	
    public static String itemDisplayNameShort(ItemStack itemstack)
    {
        List<String> namelist = null;
        try
        {
            namelist = itemstack.getTooltip(Minecraft.getMinecraft().thePlayer, false);
        }
        catch(Throwable t) {}

        if(namelist == null)
            namelist = new ArrayList<String>();

        if(namelist.size() == 0)
            namelist.add("Unnamed");

        if(namelist.get(0) == null || namelist.get(0).equals(""))
            namelist.set(0, "Unnamed");

        namelist.set(0, "\247"+Integer.toHexString(itemstack.getRarity().rarityColor.ordinal())+namelist.get(0));
        for(int i = 1; i < namelist.size(); i++)
            namelist.set(i, "\u00a77"+namelist.get(i));

        
        return namelist.get(0);
    }	
}
