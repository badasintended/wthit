package mcp.mobius.waila.client;

import java.util.List;

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent.KeyInputEvent;
import mcp.mobius.waila.api.impl.ConfigHandler;
import mcp.mobius.waila.cbcore.LangUtil;
import mcp.mobius.waila.gui.screens.config.ScreenConfig;
import mcp.mobius.waila.overlay.RayTracing;
import mcp.mobius.waila.utils.Constants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.common.config.Configuration;

public class KeyEvent {
	public static KeyBinding key_cfg;
	public static KeyBinding key_show;
	public static KeyBinding key_liquid;
	public static KeyBinding key_recipe;
	public static KeyBinding key_usage;
	
	@SubscribeEvent
	public void onKeyEvent(KeyInputEvent event){
		int key      = Keyboard.getEventKey();
		Minecraft mc = Minecraft.getMinecraft();

		if (Keyboard.getEventKeyState()){		
			if (key == key_cfg.getKeyCode()){
				if(mc.currentScreen == null)
					mc.displayGuiScreen(new ScreenConfig(mc.currentScreen));			
			}
	
			else if (key == key_show.getKeyCode() && ConfigHandler.instance().getConfig(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_MODE, false)){
				boolean status = ConfigHandler.instance().getConfig(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_SHOW, true);
				ConfigHandler.instance().setConfig(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_SHOW, !status);
			}
			
			else if (key == key_show.getKeyCode() && !ConfigHandler.instance().getConfig(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_MODE, false)){
				ConfigHandler.instance().setConfig(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_SHOW, true);
			}
			
			else if (key == key_liquid.getKeyCode()){
				boolean status = ConfigHandler.instance().getConfig(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_LIQUID, true);
				ConfigHandler.instance().setConfig(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_LIQUID, !status);
			}	
			
			else if (key == key_recipe.getKeyCode()){
				if (Loader.isModLoaded("NotEnoughItems")){
					try{
						Class.forName("mcp.mobius.waila.handlers.nei.NEIHandler").getDeclaredMethod("openRecipeGUI", boolean.class).invoke(null, true);
					} catch (Exception e){}				
				}
			}
			
			else if (key == key_usage.getKeyCode()){
				if (Loader.isModLoaded("NotEnoughItems")){
					try{
						Class.forName("mcp.mobius.waila.handlers.nei.NEIHandler").getDeclaredMethod("openRecipeGUI", boolean.class).invoke(null, false);
					} catch (Exception e){}
				}
			}			
		} 
	}
	
	public KeyEvent(){
		KeyEvent.key_cfg    = new KeyBinding(Constants.BIND_WAILA_CFG,     Keyboard.KEY_NUMPAD0, "Waila");
		KeyEvent.key_show   = new KeyBinding(Constants.BIND_WAILA_SHOW,    Keyboard.KEY_NUMPAD1, "Waila");
		KeyEvent.key_liquid = new KeyBinding(Constants.BIND_WAILA_LIQUID,  Keyboard.KEY_NUMPAD2, "Waila");		
		KeyEvent.key_recipe = new KeyBinding(Constants.BIND_WAILA_RECIPE,  Keyboard.KEY_NUMPAD3, "Waila");
		KeyEvent.key_usage  = new KeyBinding(Constants.BIND_WAILA_USAGE,   Keyboard.KEY_NUMPAD4, "Waila");
		
		ClientRegistry.registerKeyBinding(KeyEvent.key_cfg);
		ClientRegistry.registerKeyBinding(KeyEvent.key_show);
		ClientRegistry.registerKeyBinding(KeyEvent.key_liquid);
		ClientRegistry.registerKeyBinding(KeyEvent.key_recipe);
		ClientRegistry.registerKeyBinding(KeyEvent.key_usage);

		/*
        Minecraft.getMinecraft().gameSettings.keyBindings = (KeyBinding[])ArrayUtils.addAll(
        		new KeyBinding[] {KeyHandler.key_cfg,  KeyHandler.key_show, KeyHandler.key_liquid}, 
        		Minecraft.getMinecraft().gameSettings.keyBindings);
        */        
	}
}
