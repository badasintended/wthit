package mcp.mobius.waila.handlers.nei;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import mcp.mobius.waila.utils.ModIdentification;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
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
		boolean oreDictSearch = false;
		
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
	        	if (searchText.startsWith("#") && searchText.length() > 1){
	        		pattern = Pattern.compile(searchText.substring(1));
	        		oreDictSearch = true;	        		
	        	} else {
	        		pattern = Pattern.compile(searchText);
	        		oreDictSearch = false;
	        	}
	        } catch (PatternSyntaxException ignored) {}
	    }
	
	    @Override
	    public boolean matches(ItemStack item) {
	        if (pattern == null || pattern.toString().equals(""))
	            return true;
	        
	        if (oreDictSearch){
	        	int[] ids = OreDictionary.getOreIDs(item);
	        	for (int id : ids){
	        		if (pattern.matcher(OreDictionary.getOreName(id).toLowerCase()).find())
	        			return true;
	        	}
	        	return false;
	        } else {
		        return pattern.matcher(ItemInfo.getSearchName(item)).find() || pattern.matcher(ModIdentification.nameFromStack(item).toLowerCase()).find();	        	
	        }
	    }
	}
}
