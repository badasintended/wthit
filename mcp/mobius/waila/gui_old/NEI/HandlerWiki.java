package mcp.mobius.waila.gui_old.NEI;

import java.util.LinkedHashMap;

import mcp.mobius.waila.Constants;
import mcp.mobius.waila.mod_Waila;
import mcp.mobius.waila.api.impl.ConfigHandler;
import mcp.mobius.waila.api.impl.ModuleRegistrar;
import mcp.mobius.waila.gui_old.GuiIngameWiki;
import mcp.mobius.waila.gui_old.widget.Label;
import mcp.mobius.waila.handlers.SummaryProvider;
import mcp.mobius.waila.tools.ModIdentification;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;
import codechicken.nei.NEIClientConfig;
import codechicken.nei.forge.IContainerInputHandler;

public class HandlerWiki implements IContainerInputHandler {

	public String getDescriptionString(String modid, String name, String meta){
		if (ModuleRegistrar.instance().hasDocTextModID(modid)){
			//Do we have an direct entry for this item ?
			if (ModuleRegistrar.instance().hasDocTextItem(modid, name)){
				//We check if we have an exact match, a wildcard match and finally, an meta 0 match
				if (ModuleRegistrar.instance().hasDocTextMeta(modid, name, meta)){
					return ModuleRegistrar.instance().getDocText(modid, name, meta);
				} else if (ModuleRegistrar.instance().hasDocTextSpecificMeta(modid, name, "*")){
					return ModuleRegistrar.instance().getDocText(modid, name, "*");
				} else if (ModuleRegistrar.instance().hasDocTextSpecificMeta(modid, name, "0")){
					return ModuleRegistrar.instance().getDocText(modid, name, "0");
				}
				
			} else if (ModuleRegistrar.instance().getDoxTextWildcardMatch(modid, name) != null) {
				String key = ModuleRegistrar.instance().getDoxTextWildcardMatch(modid, name);
				if (ModuleRegistrar.instance().hasDocTextMeta(modid, key, meta)){
					return ModuleRegistrar.instance().getDocText(modid, key, meta);
				} else if (ModuleRegistrar.instance().hasDocTextSpecificMeta(modid, key, "*")){
					return ModuleRegistrar.instance().getDocText(modid, key, "*");
				} else if (ModuleRegistrar.instance().hasDocTextSpecificMeta(modid, key, "0")){
					return ModuleRegistrar.instance().getDocText(modid, key, "0");
				}				
			}
			
		}
		return String.format("No description for %s$$%s",modid, name);
	}	
	

	
	@Override
	public boolean lastKeyTyped(GuiContainer gui, char keyChar, int keyID) {
		ItemStack stackover = gui.manager.getStackMouseOver();
		if(stackover == null)
			return false;		
		 
		if(keyID == NEIClientConfig.getKeyBinding(Constants.BIND_SCREEN_WIKI)){
			Minecraft mc = Minecraft.getMinecraft();
			GuiIngameWiki wikiScreen = new GuiIngameWiki(mc.currentScreen);
			
			wikiScreen.widReader.setText(this.getDescriptionString(ModIdentification.idFromStack(stackover), stackover.getItemName(), String.valueOf(stackover.getItemDamage())));
			wikiScreen.widStack.setStack(stackover);
			wikiScreen.widItemName.setLabel(stackover.getDisplayName());
			
			LinkedHashMap<String, String> entries = SummaryProvider.instance().getSummary(stackover, (ConfigHandler.instance()));
			int nvalidentries = 0;
			for (String s : entries.keySet())
				if (entries.get(s) != null)			
					nvalidentries += 1;
			
			if (nvalidentries == 0)
				for (int i = 0; i < 6; i++)
					wikiScreen.widTable.addRow(new Label(""), new Label(entries.get("")));
			else
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
