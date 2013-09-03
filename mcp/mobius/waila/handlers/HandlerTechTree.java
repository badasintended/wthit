package mcp.mobius.waila.handlers;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import mcp.mobius.waila.Constants;
import mcp.mobius.waila.gui.GuiIngameWiki;
import mcp.mobius.waila.gui.GuiTechTree;
import mcp.mobius.waila.gui.widget.ComponentDisplay;
import mcp.mobius.waila.gui.widget.StackDisplay;
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

	public HandlerTechTree() {}

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
	public boolean keyTyped(GuiContainer gui, char keyChar, int keyCode) {	return false; }
	@Override
	public void onKeyTyped(GuiContainer gui, char keyChar, int keyID) {	}
	@Override
	public boolean mouseClicked(GuiContainer gui, int mousex, int mousey, int button) {	return false; }
	@Override
	public void onMouseClicked(GuiContainer gui, int mousex, int mousey,  int button) {	}
	@Override
	public void onMouseUp(GuiContainer gui, int mousex, int mousey, int button) { }
	@Override
	public boolean mouseScrolled(GuiContainer gui, int mousex, int mousey, int scrolled) { return false; }
	@Override
	public void onMouseScrolled(GuiContainer gui, int mousex, int mousey, int scrolled) { }
	@Override
	public void onMouseDragged(GuiContainer gui, int mousex, int mousey, int button, long heldTime) { }

}
