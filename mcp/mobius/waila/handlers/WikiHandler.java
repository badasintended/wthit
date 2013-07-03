package mcp.mobius.waila.handlers;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;

import mcp.mobius.waila.mod_Waila;
import mcp.mobius.waila.addons.ConfigHandler;
import mcp.mobius.waila.addons.ExternalModulesHandler;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.gui.GuiEnchantScreen;
import mcp.mobius.waila.gui.GuiIngameWiki;
import mcp.mobius.waila.gui.widget.Label;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.item.EnumToolMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import codechicken.nei.NEIClientConfig;
import codechicken.nei.forge.IContainerInputHandler;

public class WikiHandler implements IContainerInputHandler {

	public String getDescriptionString(String modid, String name){
		if (ExternalModulesHandler.instance().wikiDescriptions.containsKey(modid)){
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
		return String.format("No description for %s$$%s",modid, name);
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
			wikiScreen.widStack.setStack(stackover);
			wikiScreen.widItemName.setLabel(stackover.getDisplayName());
			
			LinkedHashMap<String, String> entries = SummaryProvider.instance().getSummary(stackover, (IWailaConfigHandler)(ConfigHandler.instance())); 
			for (String s : entries.keySet())
				if (entries.get(s) != null)
					wikiScreen.widTable.addRow(new Label(s), new Label(entries.get(s)));
				
			mc.displayGuiScreen(wikiScreen);		
			
		}
		return false;
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
