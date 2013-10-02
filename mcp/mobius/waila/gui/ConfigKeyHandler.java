package mcp.mobius.waila.gui;

import java.util.EnumSet;

import org.lwjgl.input.Keyboard;

import mcp.mobius.waila.Constants;
import mcp.mobius.waila.addons.ConfigHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import cpw.mods.fml.client.registry.KeyBindingRegistry.KeyHandler;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.common.registry.LanguageRegistry;

public class ConfigKeyHandler extends KeyHandler {

    public ConfigKeyHandler()
    {
        super(new KeyBinding[]{
                new KeyBinding(Constants.BIND_WAILA_CFG,  Keyboard.KEY_NUMPAD0),
                new KeyBinding(Constants.BIND_WAILA_SHOW, Keyboard.KEY_NUMPAD1),
                //new KeyBinding("key.wailatedump", Keyboard.KEY_NUMPAD1),
            }, new boolean[]{
                false,
                false,
                //false
            });
        
        LanguageRegistry.instance().addStringLocalization(Constants.BIND_WAILA_CFG, "[Waila] Config screen");
        LanguageRegistry.instance().addStringLocalization(Constants.BIND_WAILA_SHOW, "[Waila] Show/Hide");
        //LanguageRegistry.instance().addStringLocalization("key.wailatedump", "[Waila] Dump server TE");        
    }	

	@Override
	public void keyDown(EnumSet<TickType> types, KeyBinding kb,	boolean tickEnd, boolean isRepeat) {
		if (!tickEnd) return;
		Minecraft mc = Minecraft.getMinecraft();
		if (kb.keyDescription == Constants.BIND_WAILA_CFG){
			if(mc.currentScreen == null)
				mc.displayGuiScreen(new GuiConfigScreen(mc.currentScreen));
		}
		
		else if (kb.keyDescription == Constants.BIND_WAILA_SHOW && ConfigHandler.instance().getConfig(Constants.CFG_WAILA_MODE)){
			//boolean status = NEIClientConfig.getSetting(Constants.CFG_NEI_SHOW).getBooleanValue();
			//NEIClientConfig.getSetting(Constants.CFG_NEI_SHOW).setBooleanValue(!status);
			boolean status = ConfigHandler.instance().getConfig(Constants.CFG_WAILA_SHOW);
			ConfigHandler.instance().setConfig(Constants.CFG_WAILA_SHOW, !status);
		}
		
		else if (kb.keyDescription == Constants.BIND_WAILA_SHOW && !ConfigHandler.instance().getConfig(Constants.CFG_WAILA_MODE)){
			//NEIClientConfig.getSetting(Constants.CFG_NEI_SHOW).setBooleanValue(true);
			ConfigHandler.instance().setConfig(Constants.CFG_WAILA_SHOW, true);			
		}
	}

	@Override
	public void keyUp(EnumSet<TickType> types, KeyBinding kb, boolean tickEnd) {
		if (!tickEnd) return;
		if (kb.keyDescription == Constants.BIND_WAILA_SHOW && !ConfigHandler.instance().getConfig(Constants.CFG_WAILA_MODE)){
			//NEIClientConfig.getSetting(Constants.CFG_NEI_SHOW).setBooleanValue(false);
			ConfigHandler.instance().setConfig(Constants.CFG_WAILA_SHOW, false);
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
