package mcp.mobius.waila.handlers;

import java.util.LinkedHashMap;

import mcp.mobius.waila.mod_Waila;
import mcp.mobius.waila.addons.ExternalModulesHandler;
import mcp.mobius.waila.gui.GuiEnchantScreen;
import mcp.mobius.waila.gui.GuiIngameWiki;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import codechicken.nei.NEIClientConfig;
import codechicken.nei.forge.IContainerInputHandler;

public class WikiHandler implements IContainerInputHandler {

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
		 
		if(keyID == NEIClientConfig.getKeyBinding("showwiki")){
			Minecraft mc = Minecraft.getMinecraft();
			GuiIngameWiki wikiScreen = new GuiIngameWiki(mc.currentScreen);
			
			wikiScreen.widReader.setText(this.getDescriptionString(mod_Waila.instance.getModID(stackover), stackover.getItemName()));
			
			//Item item = Item.itemsList[stackover.itemID];
			//String itemCanonical = item.getClass().getCanonicalName();
			
			//if (item instanceof ItemBlock){
			//	Block block = Block.blocksList[((ItemBlock)item).getBlockID()];
			//	wikiScreen.widReader.setText(this.getDescriptionString(block.getClass().getCanonicalName(), stackover.getItemDamage()));
			//} else {
			//	wikiScreen.widReader.setText(this.getDescriptionString(itemCanonical, stackover.getItemDamage()));
			//}
			mc.displayGuiScreen(wikiScreen);		
			
		}
		return false;
	}

	public String getDescriptionString(String modid, String name){
		if (ExternalModulesHandler.instance().hasDocText(modid, name)){
			return ExternalModulesHandler.instance().getDocText(modid, name);
		} else {
			LinkedHashMap <String, String> entries = ExternalModulesHandler.instance().wikiDescriptions.get(modid);
			for (String s: entries.keySet()){
				String regexed = s;
				regexed = regexed.replace(".", "\\.");
				regexed = regexed.replace("*", ".*");
				
				if (name.matches(s))
					return ExternalModulesHandler.instance().getDocText(modid, s);
			}
			return String.format("No description for %s$$%s",modid, name);
		}
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
