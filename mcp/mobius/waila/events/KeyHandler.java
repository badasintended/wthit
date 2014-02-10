package mcp.mobius.waila.events;

import mcp.mobius.waila.Constants;
import mcp.mobius.waila.api.impl.ConfigHandler;
import mcp.mobius.waila.gui.screens.config.ScreenConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;

import org.apache.commons.lang3.ArrayUtils;
import org.lwjgl.input.Keyboard;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent.KeyInputEvent;

public class KeyHandler {
	static KeyBinding key_cfg;
	static KeyBinding key_show;
	static KeyBinding key_liquid;	
		
	@SubscribeEvent
	public void onKeyEvent(KeyInputEvent event){
		int key      = Keyboard.getEventKey();
		Minecraft mc = Minecraft.getMinecraft();

		if (Keyboard.getEventKeyState()){		
			if (key == key_cfg.getKeyCode()){
				if(mc.currentScreen == null)
					mc.displayGuiScreen(new ScreenConfig(mc.currentScreen));			
			}
	
			else if (key == key_show.getKeyCode() && ConfigHandler.instance().getConfig(Constants.CFG_WAILA_MODE)){
				boolean status = ConfigHandler.instance().getConfig(Constants.CFG_WAILA_SHOW);
				ConfigHandler.instance().setConfig(Constants.CFG_WAILA_SHOW, !status);
			}
			
			else if (key == key_show.getKeyCode() && !ConfigHandler.instance().getConfig(Constants.CFG_WAILA_MODE)){
				ConfigHandler.instance().setConfig(Constants.CFG_WAILA_SHOW, true);			
			}
			
			else if (key == key_liquid.getKeyCode()){
				boolean status = ConfigHandler.instance().getConfig(Constants.CFG_WAILA_LIQUID);
				ConfigHandler.instance().setConfig(Constants.CFG_WAILA_LIQUID, !status);
			}		
		} else {
			if (key == key_show.getKeyCode() && !ConfigHandler.instance().getConfig(Constants.CFG_WAILA_MODE)){
				ConfigHandler.instance().setConfig(Constants.CFG_WAILA_SHOW, false);			
			}			
		}
	}
	
	public static void registerKeybinds(){
		KeyHandler.key_cfg    = new KeyBinding(Constants.BIND_WAILA_CFG,     Keyboard.KEY_NUMPAD0, "Waila");
		KeyHandler.key_show   = new KeyBinding(Constants.BIND_WAILA_SHOW,    Keyboard.KEY_NUMPAD1, "Waila");
		KeyHandler.key_liquid = new KeyBinding(Constants.BIND_WAILA_LIQUID,  Keyboard.KEY_NUMPAD2, "Waila");

        Minecraft.getMinecraft().gameSettings.keyBindings = (KeyBinding[])ArrayUtils.addAll(
        		new KeyBinding[] {KeyHandler.key_cfg,  KeyHandler.key_show, KeyHandler.key_liquid}, 
        		Minecraft.getMinecraft().gameSettings.keyBindings);        
	}
}
