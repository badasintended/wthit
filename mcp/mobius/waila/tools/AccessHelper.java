package mcp.mobius.waila.tools;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mcp.mobius.waila.mod_Waila;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.item.crafting.IRecipe;

public class AccessHelper {
	 public static Field getDeclaredField(String classname, String fieldname){
	    	
			try {
				Class class_ = Class.forName(classname);
		    	Field field_ = class_.getDeclaredField(fieldname);
		    	field_.setAccessible(true);
		    	mod_Waila.log.fine(String.format("++ Found field %s %s\n", classname, fieldname));
		    	return field_;
			} 
			catch (NoSuchFieldException e)   {
				mod_Waila.log.warning(String.format("== Field %s %s not found !\n", classname, fieldname));
				return null;
			} 
			catch (SecurityException e)      {
				mod_Waila.log.warning(String.format("== Field %s %s security exception !\n", classname, fieldname));				
				return null;
			}
			catch (ClassNotFoundException e) {
				mod_Waila.log.warning(String.format("== Class %s not found !\n", classname));				
				return null;
			}
	    }

	    public static Block getBlock(String classname, String fieldname){
	    	Field field_ = getDeclaredField(classname, fieldname);
	    	try{
	    		return (Block)field_.get(Block.class);
	    	} catch (Exception e) {
				System.out.printf("%s\n", e);
				mod_Waila.log.warning(String.format("== ERROR GETTING BLOCK %s %s\n", classname, fieldname));
	    		return null;
	    	}
	    }

	    public static Item getItem(String classname, String fieldname){
	    	Field field_ = getDeclaredField(classname, fieldname);
	    	try{
	    		return (Item)field_.get(Item.class);
	    	} catch (Exception e) {
				System.out.printf("%s\n", e);
				mod_Waila.log.warning(String.format("== ERROR GETTING ITEM %s %s\n", classname, fieldname));				
	    		return null;
	    	}
	    } 
	    
	    public static ArrayList<IRecipe> getCraftingRecipes(ItemStack stack){
	    	ArrayList<IRecipe> recipes = new ArrayList<IRecipe>();
	    	
			for (IRecipe recipe : (ArrayList<IRecipe>)CraftingManager.getInstance().getRecipeList()){
				if (recipe != null && recipe.getRecipeOutput() != null){
					if (recipe.getRecipeOutput().isItemEqual(stack))
						recipes.add(recipe);
				}
			}
				
			return recipes;
	    }
	    
	    public static void cleanCraftingRecipes(ItemStack stack){
			for (IRecipe recipe: getCraftingRecipes(stack)){
				CraftingManager.getInstance().getRecipeList().remove(recipe);
			}	    	
	    }

	    public static void cleanSmeltingRecipes(ItemStack stack){
	    	Map smeltingList = FurnaceRecipes.smelting().getSmeltingList();
	    	Map<List<Integer>, ItemStack> smeltingListMeta = FurnaceRecipes.smelting().getMetaSmeltingList();
	    	
	    	HashMap<Integer, ItemStack> matchingRecipes           = new HashMap<Integer, ItemStack>();
	    	HashMap<List<Integer>, ItemStack> matchingRecipesMeta = new HashMap<List<Integer>, ItemStack>(); 
	    	
	    	// We walk the none meta list to find all the recipes
	    	for (Object id_ : smeltingList.keySet()){
	    		Integer id = (Integer)id_;
	    		ItemStack result = (ItemStack)smeltingList.get(id_);
	    		
	    		if (stack.isItemEqual(result))
	    			matchingRecipes.put(id, result);
	    	}

	    	for (List<Integer> key : smeltingListMeta.keySet()){
	    		ItemStack result = smeltingListMeta.get(key);
	    		
	    		if (stack.isItemEqual(result))
	    			matchingRecipesMeta.put(key, result);	    		
	    	}
	    	
	    	for (Integer key : matchingRecipes.keySet())
	    		FurnaceRecipes.smelting().getSmeltingList().remove(key);

	    	for (List<Integer> key : matchingRecipesMeta.keySet())
	    		FurnaceRecipes.smelting().getMetaSmeltingList().remove(key);	    	
	    }
	    
}
