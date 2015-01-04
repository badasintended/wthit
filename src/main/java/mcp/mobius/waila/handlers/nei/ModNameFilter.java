package mcp.mobius.waila.handlers.nei;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.utils.ModIdentification;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import codechicken.nei.NEIClientConfig;
import codechicken.nei.SearchField;
import codechicken.nei.SearchField.ISearchProvider;
import codechicken.nei.api.ItemFilter;
import codechicken.nei.api.ItemInfo;

public class ModNameFilter implements ISearchProvider{

	@Override
	public ItemFilter getFilter(String searchText) {
		Pattern pattern = SearchField.getPattern(searchText);
		return pattern == null ? null : new Filter(pattern);
	}

	@Override
	public boolean isPrimary() {
		return false;
	}

	public static class Filter implements ItemFilter {

		Pattern pattern;
		
	    public Filter(Pattern pattern) {
	    	this.pattern = pattern;
	    }
		
		@Override
		public boolean matches(ItemStack itemstack) {
			return this.pattern.matcher(ModIdentification.nameFromStack(itemstack).toLowerCase()).find();
		}
		
	}
	
}