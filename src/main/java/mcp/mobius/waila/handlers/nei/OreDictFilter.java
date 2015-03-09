package mcp.mobius.waila.handlers.nei;

import java.util.regex.Pattern;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import codechicken.nei.SearchField;
import codechicken.nei.SearchField.ISearchProvider;
import codechicken.nei.api.ItemFilter;

public class OreDictFilter implements ISearchProvider{

	@Override
	public ItemFilter getFilter(String searchText) {
		if (!searchText.startsWith("#") || searchText.length() < 2) return null;
		Pattern pattern = SearchField.getPattern(searchText.substring(1));
		return pattern == null ? null : new Filter(pattern);
	}

	@Override
	public boolean isPrimary() {
		return true;
	}

	public static class Filter implements ItemFilter {

		Pattern pattern;
		
	    public Filter(Pattern pattern) {
	    	this.pattern = pattern;
	    }
		
		@Override
		public boolean matches(ItemStack itemstack) {
        	int[] ids = OreDictionary.getOreIDs(itemstack);
        	boolean found = false;
        	for (int id : ids){
        		if (pattern.matcher(OreDictionary.getOreName(id).toLowerCase()).find()){
        			found = true;
        		}
        	}
        	return found;			
		}
		
	}
}
