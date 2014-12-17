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

	List<ISearchProvider> searchProviders = null;
	
	@Override
	public ItemFilter getFilter(String searchText) {
		if(searchProviders == null)
		{
			searchProviders = new LinkedList<ISearchProvider>(SearchField.searchProviders);
			searchProviders.remove(this);		
		}
		return new Filter(searchText, this.searchProviders);
	}
	
	public static class Filter implements ItemFilter {
	
		String  searchText;
		Pattern pattern;
		boolean oreDictSearch = false;
		List<ISearchProvider> searchProviders = new LinkedList<ISearchProvider>();
		
	    public Filter(String searchText, List<ISearchProvider> providers) {
	    	this.searchProviders = new ArrayList<ISearchProvider>(providers);
	    	this.searchText      = searchText;
	    	
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
	    	boolean result = true;
	    	
	    	try{
		    	for (ISearchProvider provider : searchProviders){
		    		ItemFilter filter = provider.getFilter(searchText);
		    		if (filter != null)
		    			result &= provider.getFilter(searchText).matches(item);
		    	}
		    	
		        if (pattern == null || pattern.toString().equals("")){
		            result &= true;
		        } else if (pattern.toString().startsWith("@")){ // If it starts with @, we skip it
		        } else if (oreDictSearch){
		        	int[] ids = OreDictionary.getOreIDs(item);
		        	boolean found = false;
		        	for (int id : ids){
		        		if (pattern.matcher(OreDictionary.getOreName(id).toLowerCase()).find()){
		        			found = true;
		        		}
		        	}
		        	result &= found;
		        } else {
			        result &= pattern.matcher(ItemInfo.getSearchName(item)).find() || pattern.matcher(ModIdentification.nameFromStack(item).toLowerCase()).find();	        	
		        }
	    	} catch (Exception e){
	    		Waila.log.warn(String.format("Error while filtering items : %s : %s", e, e.getMessage()));
	    	}
	        return result;
	    }
	}
}
