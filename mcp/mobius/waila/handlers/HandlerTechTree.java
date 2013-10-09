package mcp.mobius.waila.handlers;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import mcp.mobius.waila.Constants;
import mcp.mobius.waila.gui_old.GuiIngameWiki;
import mcp.mobius.waila.gui_old.GuiTechTree;
import mcp.mobius.waila.gui_old.widget.ComponentDisplay;
import mcp.mobius.waila.gui_old.widget.StackDisplay;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import codechicken.nei.NEIClientConfig;
import codechicken.nei.forge.IContainerInputHandler;

public class HandlerTechTree implements IContainerInputHandler {

	public HandlerTechTree() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean keyTyped(GuiContainer gui, char keyChar, int keyCode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onKeyTyped(GuiContainer gui, char keyChar, int keyID) {
		// TODO Auto-generated method stub

	}


	@Override
	public boolean lastKeyTyped(GuiContainer gui, char keyChar, int keyID) {
		ItemStack stackover = gui.manager.getStackMouseOver();
		if(stackover == null)
			return false;		
		 
		if(keyID == NEIClientConfig.getKeyBinding(Constants.BIND_SCREEN_TECH)){
			Minecraft mc = Minecraft.getMinecraft();
			GuiTechTree techScreen = new GuiTechTree(mc.currentScreen);

			LinkedHashMap<Integer, ArrayList<CraftingTreeNode>> layers = new LinkedHashMap<Integer, ArrayList<CraftingTreeNode>> (); 
			CraftingTreeNode tree = new CraftingTreeNode(null, stackover, 0, layers);

			/*
			LinkedHashMap<Integer, ArrayList<CraftingTreeNode>> curratedLayers = new LinkedHashMap<Integer, ArrayList<CraftingTreeNode>> (); 

			for (int layer : layers.keySet())
				curratedLayers.put(layer, new ArrayList<CraftingTreeNode>());
			
			for (int i = 0; i < (layers.size() - 1) ; i++)
				for (CraftingTreeNode node : layers.get(i)){
					if (node.bottomNodes.size() != 0){
						curratedLayers.get(i).add(node);

					}
					else{
						node.layer += 1;
						layers.get(i+1).add(node);
					}
				}

			for (CraftingTreeNode node : layers.get(layers.keySet().size()-1))
				curratedLayers.get(layers.keySet().size()-1).add(node);
				
			System.out.printf("%s\n", curratedLayers);
			*/
			
			/*
			for (int layer = curratedLayers.keySet().size() - 1; layer >= 0; layer--){
				int offsetX = 0;
				for (int icompo = 0; icompo < curratedLayers.get(layer).size(); icompo++){
					ComponentDisplay componentDisplay = new ComponentDisplay(techScreen, curratedLayers.get(layer).get(icompo));
					componentDisplay.setBackgroundColor(0xddeeeed1);
					techScreen.widViewport.addWidget(componentDisplay, offsetX, layer*32, 0);
					offsetX += componentDisplay.getWidth() + 8;
				}
			}
			*/

			for (int layer = layers.keySet().size() - 1; layer >= 0; layer--){
				int offsetX = 0;
				for (int icompo = 0; icompo < layers.get(layer).size(); icompo++){
					ComponentDisplay componentDisplay = new ComponentDisplay(techScreen, layers.get(layer).get(icompo));
					componentDisplay.setBackgroundColor(0xddeeeed1);
					if (layer != 0)
						componentDisplay.setDrawOverlay(true);
					
					techScreen.widViewport.addWidget(componentDisplay, offsetX, layer*32, 0);
					offsetX += componentDisplay.getWidth() + 8;
				}
			}			
			
			mc.displayGuiScreen(techScreen);
		}
		return false;
	}

	@Override
	public boolean mouseClicked(GuiContainer gui, int mousex, int mousey,
			int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onMouseClicked(GuiContainer gui, int mousex, int mousey,
			int button) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onMouseUp(GuiContainer gui, int mousex, int mousey, int button) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean mouseScrolled(GuiContainer gui, int mousex, int mousey,
			int scrolled) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onMouseScrolled(GuiContainer gui, int mousex, int mousey,
			int scrolled) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onMouseDragged(GuiContainer gui, int mousex, int mousey,
			int button, long heldTime) {
		// TODO Auto-generated method stub

	}

}
