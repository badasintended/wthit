package mcp.mobius.waila.client;

import java.util.EnumSet;
import java.util.List;

import mcp.mobius.waila.api.impl.ConfigHandler;
import mcp.mobius.waila.cbcore.LangUtil;
import mcp.mobius.waila.gui.screens.config.ScreenConfig;
import mcp.mobius.waila.gui.screens.info.ScreenEnchants;
import mcp.mobius.waila.handlers.nei.HandlerEnchants;
import mcp.mobius.waila.overlay.RayTracing;
import mcp.mobius.waila.utils.Constants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import org.lwjgl.input.Keyboard;

import codechicken.nei.api.API;
import codechicken.nei.guihook.GuiContainerManager;
import codechicken.nei.recipe.GuiCraftingRecipe;
import codechicken.nei.recipe.GuiUsageRecipe;
import cpw.mods.fml.client.registry.KeyBindingRegistry.KeyHandler;
import cpw.mods.fml.common.TickType;

public class ConfigKeyHandler extends KeyHandler {

	static boolean firstInventory = true;
	
    public ConfigKeyHandler()
    {
        super(new KeyBinding[]{
                new KeyBinding(Constants.BIND_WAILA_CFG,     Keyboard.KEY_NUMPAD0),
                new KeyBinding(Constants.BIND_WAILA_SHOW,    Keyboard.KEY_NUMPAD1),
                new KeyBinding(Constants.BIND_WAILA_LIQUID,  Keyboard.KEY_NUMPAD2),
                new KeyBinding(Constants.BIND_WAILA_RECIPE,  Keyboard.KEY_NUMPAD3),
                new KeyBinding(Constants.BIND_WAILA_USAGE,   Keyboard.KEY_NUMPAD4),
                //new KeyBinding(Constants.BIND_WAILA_TESTING, Keyboard.KEY_NUMPAD9),
            }, new boolean[]{
                false,
                false,
                false,
                false,
                false,
                //false
            });
        
        /*
        LanguageRegistry.instance().addStringLocalization(Constants.BIND_WAILA_CFG,     LangUtil.translateG("keybind.configscreen"));
        LanguageRegistry.instance().addStringLocalization(Constants.BIND_WAILA_SHOW,    LangUtil.translateG("keybind.showhide"));
        LanguageRegistry.instance().addStringLocalization(Constants.BIND_WAILA_LIQUID,  LangUtil.translateG("keybind.showliquid"));
        LanguageRegistry.instance().addStringLocalization(Constants.BIND_WAILA_RECIPE,  LangUtil.translateG("keybind.showrecipe"));
        LanguageRegistry.instance().addStringLocalization(Constants.BIND_WAILA_USAGE,   LangUtil.translateG("keybind.showusage"));
        LanguageRegistry.instance().addStringLocalization(Constants.BIND_WAILA_TESTING, LangUtil.translateG("keybind.testing"));
        */
        //LanguageRegistry.instance().addStringLocalization(Constants.BIND_SCREEN_ENCH,   "[Waila] Show enchant screen");
        //LanguageRegistry.instance().addStringLocalization("key.wailatedump", "[Waila] Dump server TE");      
        
		GuiContainerManager.addInputHandler(new HandlerEnchants());
		API.addKeyBind(Constants.BIND_SCREEN_ENCH, Keyboard.KEY_I);        
    }

	@Override
	public void keyDown(EnumSet<TickType> types, KeyBinding kb,	boolean tickEnd, boolean isRepeat) {
		if (!tickEnd) return;
		Minecraft mc = Minecraft.getMinecraft();
		if (kb.keyDescription == Constants.BIND_WAILA_CFG){
			if(mc.currentScreen == null)
				mc.displayGuiScreen(new ScreenConfig(mc.currentScreen));
		}

		if (kb.keyDescription == Constants.BIND_WAILA_TESTING){
			if(mc.currentScreen == null)
				mc.displayGuiScreen(new ScreenEnchants(mc.currentScreen));
		}		
		
		if (mc.currentScreen != null)
			return;
		
		if (kb.keyDescription == Constants.BIND_WAILA_SHOW && ConfigHandler.instance().getConfig(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_MODE, true)){
			boolean status = ConfigHandler.instance().getConfig(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_SHOW, true);
			ConfigHandler.instance().setConfig(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_SHOW, !status);
		}
		
		else if (kb.keyDescription == Constants.BIND_WAILA_SHOW && !ConfigHandler.instance().getConfig(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_MODE, true)){
			ConfigHandler.instance().setConfig(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_SHOW, true);			
		}
		
		else if (kb.keyDescription == Constants.BIND_WAILA_LIQUID){
			boolean status = ConfigHandler.instance().getConfig(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_LIQUID, true);
			ConfigHandler.instance().setConfig(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_LIQUID, !status);
		}		

		else if (kb.keyDescription == Constants.BIND_WAILA_RECIPE){
			this.openRecipeGUI(true);
		}			

		else if (kb.keyDescription == Constants.BIND_WAILA_USAGE){ 
			this.openRecipeGUI(false);
		}
			
		
	}

	public void openRecipeGUI(boolean recipe){
		Minecraft mc = Minecraft.getMinecraft();
		boolean   uiResult;
		String    msg;
		
		if ((RayTracing.instance().getTarget() != null) && (RayTracing.instance().getTarget().typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK)){
			List<ItemStack> stacks = RayTracing.instance().getIdentifierItems();
			if (stacks.size() > 0){
				mc.displayGuiScreen(new GuiInventory(mc.thePlayer));
				if (firstInventory){
					try {Thread.sleep(1000);} catch (Exception e){};
					firstInventory = false;
				}					
		
				if(recipe)
					if(!GuiCraftingRecipe.openRecipeGui("item", stacks.get(0).copy())){
						ItemStack target = stacks.get(0).copy();
						target.setItemDamage(0);
						if(!GuiCraftingRecipe.openRecipeGui("item", target)){
							mc.thePlayer.addChatMessage("\u00a7f\u00a7o" + LangUtil.translateG("client.msg.norecipe"));
							mc.displayGuiScreen((GuiScreen)null);
							mc.setIngameFocus();
						}
					}				
				
				if(!recipe)
					if(!GuiUsageRecipe.openRecipeGui("item", stacks.get(0).copy())){
						ItemStack target = stacks.get(0).copy();
						target.setItemDamage(0);						
						if(!GuiUsageRecipe.openRecipeGui("item", target)){
							mc.thePlayer.addChatMessage("\u00a7f\u00a7o" + LangUtil.translateG("client.msg.nousage"));
							mc.displayGuiScreen((GuiScreen)null);
							mc.setIngameFocus();
						}
					}
			}
		}
	}
	
	@Override
	public void keyUp(EnumSet<TickType> types, KeyBinding kb, boolean tickEnd) {
		if (!tickEnd) return;
		if (kb.keyDescription == Constants.BIND_WAILA_SHOW && !ConfigHandler.instance().getConfig(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_MODE, true)){
			ConfigHandler.instance().setConfig(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_SHOW, false);
		}		
	}

	@Override
	public EnumSet<TickType> ticks() {
        return EnumSet.of(TickType.CLIENT);
	}

	@Override
	public String getLabel() {
		return "Waila Config Screen Keybind";
	}	
	
}
