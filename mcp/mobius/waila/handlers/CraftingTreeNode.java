package mcp.mobius.waila.handlers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import codechicken.core.ReflectionManager;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.FuelRecipeHandler;
import codechicken.nei.recipe.GuiCraftingRecipe;
import codechicken.nei.recipe.ICraftingHandler;
import codechicken.nei.recipe.TemplateRecipeHandler;
import codechicken.nei.recipe.TemplateRecipeHandler.CachedRecipe;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

public class CraftingTreeNode {

	CraftingTreeNode topNode = null;
	LinkedHashMap<ItemStack, ArrayList<CraftingTreeNode>> bottomNodes = new LinkedHashMap<ItemStack, ArrayList<CraftingTreeNode>>();
	
	//Item stacks we have in the node.
	public ArrayList<ItemStack> elements = new ArrayList<ItemStack>();		     														   
	//Item stacks we are filtering the recipes with. (Previous stacks in the tree)
	ArrayList<CachedRecipe> excRecipes = new ArrayList<CachedRecipe>();  														   
	//List of recipes available for a given stack
	HashMap<ItemStack, ArrayList<CachedRecipe>> recipesMap = new HashMap<ItemStack, ArrayList<CachedRecipe>>(); 							   
	//Components sorted by recipes for a given item stack
	HashMap<ItemStack, ArrayList<ArrayList<ItemStack>>> componentsMap = new HashMap<ItemStack, ArrayList<ArrayList<ItemStack>>>(); 
	
	int layer;
	LinkedHashMap<Integer, ArrayList<CraftingTreeNode>> layers;
	
	public CraftingTreeNode(CraftingTreeNode topNode, ArrayList<ItemStack> elements, int layer, LinkedHashMap<Integer, ArrayList<CraftingTreeNode>> layers) {
		this.topNode = topNode;
		this.layer   = layer;
		this.layers  = layers;
		this.addSelfToLayers();
		this.setElements(elements);
		System.out.printf("%s\n", this.toString());
	}

	public CraftingTreeNode(CraftingTreeNode topNode, ArrayList<ItemStack> elements, ArrayList<CachedRecipe> excluded, int layer, LinkedHashMap<Integer, ArrayList<CraftingTreeNode>> layers) {
		this.topNode = topNode;
		this.layer   = layer;
		this.layers  = layers;
		this.addSelfToLayers();
		this.excRecipes = excluded;
		this.setElements(elements);
		System.out.printf("%s\n", this.toString());		
	}	
	
	public CraftingTreeNode(CraftingTreeNode topNode, ItemStack stack, int layer, LinkedHashMap<Integer, ArrayList<CraftingTreeNode>> layers) {
		this.topNode = topNode;
		this.layer   = layer;
		this.layers  = layers;		
		this.addSelfToLayers();
		this.addElement(stack);
		System.out.printf("%s\n", this.toString());		
	}	
	
	public void addSelfToLayers(){
		if (!(this.layers.containsKey(layer)))
			this.layers.put(this.layer, new ArrayList<CraftingTreeNode>());
		this.layers.get(this.layer).add(this);
	}
	
	public void addElement(ItemStack stack){
		this.elements.add(stack);
		this.recipesMap.put(stack, this.getRecipes(stack, this.excRecipes));
		this.componentsMap.put(stack, new ArrayList<ArrayList<ItemStack>>());
		for (CachedRecipe recipe: this.recipesMap.get(stack)){
			this.excRecipes.add(recipe);
			ArrayList<ItemStack> subComposants = new ArrayList<ItemStack>();
			for (PositionedStack substack : recipe.getIngredients())
				subComposants.add(substack.item);

			if (subComposants.size() > 0){
				if (!this.bottomNodes.containsKey(stack))
					this.bottomNodes.put(stack, new ArrayList<CraftingTreeNode>());
				this.bottomNodes.get(stack).add(new CraftingTreeNode(this, this.cleanupRecipe(subComposants), this.excRecipes, this.layer+1, this.layers));
			}
		}
	}

	public void setElements(ArrayList<ItemStack> elements){
		for (ItemStack is : elements)
			this.addElement(is);
	}
	
	public ArrayList<CachedRecipe> getRecipes(ItemStack stack, ArrayList<CachedRecipe> excluded){
		ArrayList<ICraftingHandler> craftinghandlers = ReflectionManager.getField(GuiCraftingRecipe.class, ArrayList.class, null, "craftinghandlers");
		ArrayList<CachedRecipe>     cachedRecipes = new ArrayList<CachedRecipe>(); 

		for (ICraftingHandler handler : craftinghandlers)
			if (handler instanceof TemplateRecipeHandler && !(handler instanceof FuelRecipeHandler)){
				((TemplateRecipeHandler) handler).arecipes = new ArrayList<CachedRecipe>();
				((TemplateRecipeHandler) handler).loadCraftingRecipes(stack);
				for (CachedRecipe recipe : ((TemplateRecipeHandler)handler).arecipes){
					boolean found = false;
					for (CachedRecipe exc : excluded)
						if (this.areRecipesIdentical(recipe, exc))
							found = true;
					if (found)
						continue;
					
					cachedRecipes.add(recipe);
				}
			}
		return cachedRecipes;
	}
	
	public boolean areRecipesIdentical(CachedRecipe recipe1, CachedRecipe recipe2){
		if (recipe1.getResult().items.length != recipe2.getResult().items.length)
			return false;
		if (recipe1.getIngredients().size() != recipe1.getIngredients().size())
			return false;
		for (int i = 0; i < recipe1.getResult().items.length; i++)
			if (!recipe1.getResult().items[i].isItemEqual(recipe2.getResult().items[i]))
				return false;
		for (int i = 0; i < recipe1.getIngredients().size(); i++)
			for (int j = 0; j < recipe1.getIngredients().get(i).items.length; j++)
				if (!recipe1.getIngredients().get(i).items[j].isItemEqual(recipe2.getIngredients().get(i).items[j]))
					return false;
		return true;
		
	}
	
	public ArrayList<ItemStack> cleanupRecipe(ArrayList<ItemStack> stacks){
		ArrayList<ItemStack> curratedStacks = new ArrayList<ItemStack>();
		for (ItemStack is : stacks){
			boolean found = false;
			for (ItemStack isc : curratedStacks){
				//if (OreDictionary.itemMatches(is, isc, false)){
				if ((isc.isItemEqual(is)) || 
					((OreDictionary.getOreID(isc) != -1) &&
					 (OreDictionary.getOreID(is) != -1) &&
					 (OreDictionary.getOreID(isc) == OreDictionary.getOreID(is))
					)){
					found = true;
					break;
				}
			}
			if (!found){
				ItemStack isc = is.copy();
				isc.stackSize = 0;
				curratedStacks.add(isc);
			}
		}
		
		for (ItemStack is : stacks)
			for (ItemStack isc : curratedStacks)
				if ((isc.isItemEqual(is)) || 
						((OreDictionary.getOreID(isc) != -1) &&
						 (OreDictionary.getOreID(is) != -1) &&
						 (OreDictionary.getOreID(isc) == OreDictionary.getOreID(is))
						))
				//if (isc.isItemEqual(is) || OreDictionary.getOreID(isc) == OreDictionary.getOreID(is))
					isc.stackSize += is.stackSize;

		return curratedStacks;
	}
	
	public String toString(){
		return this.elements.toString();
	}
}
