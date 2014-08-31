package mcp.mobius.waila.handlers.nei;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import mcp.mobius.waila.utils.ModIdentification;
import net.minecraft.item.ItemStack;
import codechicken.nei.NEIClientConfig;
import codechicken.nei.ItemList.EverythingItemFilter;
import codechicken.nei.ItemList.PatternItemFilter;
import codechicken.nei.SearchField.ISearchProvider;
import codechicken.nei.api.ItemFilter;
import codechicken.nei.api.ItemInfo;
import codechicken.nei.api.ItemFilter.ItemFilterProvider;

public class ModNameFilter implements ISearchProvider{
	
	@Override
	public ItemFilter getFilter(String searchText) {
		return new Filter(searchText);
	}
	
	public static class Filter implements ItemFilter {
	
		Pattern pattern;
	
	    public Filter(String searchText) {
	        switch(NEIClientConfig.getIntSetting("inventory.searchmode")) {
            case 0://plain
            	searchText = "\\Q"+searchText+"\\E";
                break;
            case 1:
            	searchText = searchText
                        .replace(".", "")
                        .replace("?", ".")
                        .replace("*", ".+?");
                break;
	        }
	        
	        try {
	            pattern = Pattern.compile(searchText);
	        } catch (PatternSyntaxException ignored) {}
	    }
	
	    @Override
	    public boolean matches(ItemStack item) {
	        if (pattern == null || pattern.toString().equals(""))
	            return true;
	        return pattern.matcher(ItemInfo.getSearchName(item)).find() || pattern.matcher(ModIdentification.nameFromStack(item).toLowerCase()).find(); 
	    }
	}
}
