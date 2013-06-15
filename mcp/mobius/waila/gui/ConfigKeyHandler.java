package mcp.mobius.waila.gui;

import java.util.EnumSet;

import org.lwjgl.input.Keyboard;

import codechicken.nei.NEIClientConfig;

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
                new KeyBinding("key.wailaconfig", Keyboard.KEY_NUMPAD0),
                new KeyBinding("key.wailadisplay", Keyboard.KEY_NUMPAD1)
            }, new boolean[]{
                false,
                false
            });
        
        LanguageRegistry.instance().addStringLocalization("key.wailaconfig", "[Waila] Config screen");
        LanguageRegistry.instance().addStringLocalization("key.wailadisplay", "[Waila] Show/Hide");        
    }	

	@Override
	public void keyDown(EnumSet<TickType> types, KeyBinding kb,	boolean tickEnd, boolean isRepeat) {
		if (!tickEnd) return;
		Minecraft mc = Minecraft.getMinecraft();
		if (kb.keyDescription == "key.wailaconfig"){
			if(mc.currentScreen == null)
				mc.displayGuiScreen(new GuiConfigScreen(mc.currentScreen));
		}
		else if (kb.keyDescription == "key.wailadisplay" && ConfigHandler.instance().getConfig("waila.showmode")){
			boolean status = NEIClientConfig.getSetting("options.inworld tooltips").getBooleanValue();
			NEIClientConfig.getSetting("options.inworld tooltips").setBooleanValue(!status);
		}
		else if (kb.keyDescription == "key.wailadisplay" && !ConfigHandler.instance().getConfig("waila.showmode")){
			NEIClientConfig.getSetting("options.inworld tooltips").setBooleanValue(true);
		}
	}

	@Override
	public void keyUp(EnumSet<TickType> types, KeyBinding kb, boolean tickEnd) {
		if (!tickEnd) return;
		if (kb.keyDescription == "key.wailadisplay" && !ConfigHandler.instance().getConfig("waila.showmode")){
			NEIClientConfig.getSetting("options.inworld tooltips").setBooleanValue(false);
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
