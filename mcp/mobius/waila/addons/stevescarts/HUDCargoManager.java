package mcp.mobius.waila.addons.stevescarts;

import java.util.ArrayList;
import java.util.List;

import codechicken.lib.math.MathHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.utils.WailaExceptionHandler;
import static mcp.mobius.waila.utils.SpecialChars.*;

public class HUDCargoManager implements IWailaDataProvider {

	private static String colors[] = {"NA", "Red", "Blue", "Yellow", "Green", "Dis."};
	private static String sides[]  = {"Yellow", "Blue", "Green", "Red"};
	
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
		NBTTagCompound tag = accessor.getNBTData();

		int side = accessor.getSide().ordinal() - 2;
		if (side >= 0)
			currenttip.add("Looking at side " + WHITE + sides[side]);		
		
		int layout = accessor.getNBTInteger(tag, "layout");
		switch(layout){
		case 0:
			currenttip.add("Slots : "+ WHITE + "Shared");
			break;
		case 1:
			currenttip.add("Slots : "+ WHITE + "By side");
			break;
		case 2:
			currenttip.add("Slots : "+ WHITE + "By color");
			break;						
		}
		
		try{
			ArrayList<Object> itemSelection = (ArrayList<Object>) StevesCartsModule.ItemSelections.get(accessor.getTileEntity());

			int toCart   = accessor.getNBTInteger(tag, "tocart");
			int doReturn = accessor.getNBTInteger(tag, "doReturn");
			
			for (int i = 0; i < 4; i++){
				int target = accessor.getNBTInteger(tag, "target" + String.valueOf(i));
				int color  = accessor.getNBTInteger(tag, "color"  + String.valueOf(i));
				int amount = accessor.getNBTInteger(tag, "amount" + String.valueOf(i));
				if (color == 5) continue;
				
				
				String direction    = (toCart   & (1 << i)) != 0 ? "Load" : "Unload";
				String shouldReturn = (doReturn & (1 << i)) != 0 ? "Ret." : "Cont.";
				String sAmount      = amount == 0 ? "All" : String.valueOf(this.getAmount(amount) + " " + this.getAmountType(amount));
				
				String selection =  (String) StevesCartsModule.GetSelectionName.invoke(StevesCartsModule.CargoItemSelection.cast(itemSelection.get(target)));
				currenttip.add(String.format("Side %s %s[ %s ]%s[ %s , %s]%s[ %s ]", WHITE + colors[color] + GRAY, 
																					 TAB + ALIGNRIGHT,  WHITE + selection + GRAY, 
						                                                             TAB + ALIGNRIGHT,  WHITE + direction + GRAY, 
						                                                             TAB + ALIGNRIGHT + WHITE + shouldReturn + GRAY,
						                                                             TAB + ALIGNRIGHT,  WHITE + sAmount + GRAY));
			}
			
		} catch (Exception e) {
			currenttip = WailaExceptionHandler.handleErr(e, accessor.getTileEntity().getClass().getName(), currenttip);
		}
		
		return currenttip;
	}

	@Override
	public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
		return currenttip;
	}

    public int getAmount(int id)
    {
        switch (id)
        {
            case 1:
                return 1;

            case 2:
                return 3;

            case 3:
                return 8;

            case 4:
                return 16;

            case 5:
                return 32;

            case 6:
                return 64;

            case 7:
                return 1;

            case 8:
                return 2;

            case 9:
                return 3;

            case 10:
                return 5;

            default:
                return 0;
        }
    }

    //0 - MAX
    //1 - Items
    //2 - Stacks
    public String getAmountType(int id)
    {
    	if (id <= 6)
        {
            return "I";
        }
        else
        {
            return "S";
        }
    }	
	
}
