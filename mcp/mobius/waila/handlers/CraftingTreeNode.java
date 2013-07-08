package mcp.mobius.waila.handlers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

public class CraftingTreeNode {

	CraftingTreeNode topNode = null;
	LinkedHashMap<ItemStack, ArrayList<CraftingTreeNode>> bottomNodes = new LinkedHashMap<ItemStack, ArrayList<CraftingTreeNode>>();
	
	//Item stacks we have in the node.
	public ArrayList<ItemStack> elements = new ArrayList<ItemStack>();		     														   
	//Item stacks we are filtering the recipes with. (Previous stacks in the tree)
	ArrayList<IRecipe> excRecipes = new ArrayList<IRecipe>();  														   
	//List of recipes available for a given stack
	HashMap<ItemStack, ArrayList<IRecipe>> recipesMap = new HashMap<ItemStack, ArrayList<IRecipe>>(); 							   
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

	public CraftingTreeNode(CraftingTreeNode topNode, ArrayList<ItemStack> elements, ArrayList<IRecipe> excluded, int layer, LinkedHashMap<Integer, ArrayList<CraftingTreeNode>> layers) {
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
		for (IRecipe recipe: this.recipesMap.get(stack)){
			this.excRecipes.add(recipe);
			ArrayList<ItemStack> subComposants = this.getRecipeComponents(recipe);
			this.componentsMap.get(stack).add(this.getRecipeComponents(recipe));
			if (subComposants.size() > 0){
				if (!this.bottomNodes.containsKey(stack))
					this.bottomNodes.put(stack, new ArrayList<CraftingTreeNode>());
				this.bottomNodes.get(stack).add(new CraftingTreeNode(this, subComposants, this.excRecipes, this.layer+1, this.layers));
			}
		}
	}

	public void setElements(ArrayList<ItemStack> elements){
		for (ItemStack is : elements)
			this.addElement(is);
	}
	
	public ArrayList<IRecipe> getRecipes(ItemStack stack, ArrayList<IRecipe> excluded){
		ArrayList<IRecipe> recipes = new ArrayList<IRecipe>();
		for (IRecipe recipe : (ArrayList<IRecipe>)CraftingManager.getInstance().getRecipeList())
			if (recipe != null && recipe.getRecipeOutput() != null)
				if (recipe.getRecipeOutput().isItemEqual(stack))
					if (excluded.indexOf(recipe) == -1)
						recipes.add(recipe);
		return recipes;
	}
	
	public ArrayList<ItemStack> getRecipeComponents(IRecipe recipe){
		ArrayList<ItemStack> components = new ArrayList<ItemStack>();
		if (recipe instanceof ShapedRecipes){
			for (ItemStack stack : ((ShapedRecipes)recipe).recipeItems)
				components.add(stack);
		} else if (recipe instanceof ShapelessRecipes){
			for (ItemStack stack : (List<ItemStack>)((ShapelessRecipes)recipe).recipeItems)
				components.add(stack);
		}else if (recipe instanceof ShapedOreRecipe){
			for (Object entry : ((ShapedOreRecipe)recipe).getInput()){
				if (entry instanceof ArrayList<?>)
					for (ItemStack is : (ArrayList<ItemStack>)entry)
						components.add(is);
				else if (entry instanceof ItemStack)
					components.add((ItemStack)entry);
			}
		}else if (recipe instanceof ShapelessOreRecipe){
			for (Object entry : ((ShapelessOreRecipe)recipe).getInput()){
				if (entry instanceof ArrayList<?>)
					for (ItemStack is : (ArrayList<ItemStack>)entry)
						components.add(is);
				else if (entry instanceof ItemStack)
					components.add((ItemStack)entry);
			}
		}else
			System.out.printf("%s\n", recipe);
		
		ArrayList<ItemStack> curratedComponents = new ArrayList<ItemStack>();
		for (ItemStack is : components){
			boolean found = false;
			for (ItemStack isc : curratedComponents){
				if (isc.isItemEqual(is)){
					found = true;
					break;
				}
			}
			if (!found)
				curratedComponents.add(is);
		}
		return curratedComponents;
	}
	
	public boolean hasExcludedStack(IRecipe recipe, ArrayList<ItemStack> excluded){
		if ((excluded == null) || (excluded.size() == 0))
			return false;

		for (ItemStack exc : excluded)
			if (recipe.getRecipeOutput().isItemEqual(exc))
				return true;
		
		//ArrayList<ItemStack> components = this.getRecipeComponents(recipe);
		//for (ItemStack stack : components)
		//	for (ItemStack exc : excluded)
		//		if (stack.isItemEqual(exc))
		//			return true;
		
		return false;
	}
	
	public String toString(){
		return this.elements.toString();
	}
}
