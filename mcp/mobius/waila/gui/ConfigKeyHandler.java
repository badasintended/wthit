package mcp.mobius.waila.gui;

import java.util.EnumSet;

import org.lwjgl.input.Keyboard;

import codechicken.nei.NEIClientConfig;

import mcp.mobius.waila.addons.ConfigHandler;
import mcp.mobius.waila.handlers.DataAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.nbt.NBTTagByteArray;
import net.minecraft.nbt.NBTTagCompound;
import cpw.mods.fml.client.registry.KeyBindingRegistry.KeyHandler;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.common.registry.LanguageRegistry;

public class ConfigKeyHandler extends KeyHandler {

    public ConfigKeyHandler()
    {
        super(new KeyBinding[]{
                new KeyBinding("key.wailaconfig", Keyboard.KEY_NUMPAD0),
                new KeyBinding("key.wailadisplay", Keyboard.KEY_NUMPAD1),
                new KeyBinding("key.wailatedump", Keyboard.KEY_NUMPAD1),
            }, new boolean[]{
                false,
                false,
                false
            });
        
        LanguageRegistry.instance().addStringLocalization("key.wailaconfig", "[Waila] Config screen");
        LanguageRegistry.instance().addStringLocalization("key.wailadisplay", "[Waila] Show/Hide");
        LanguageRegistry.instance().addStringLocalization("key.wailatedump", "[Waila] Dump server TE");        
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
		else if (kb.keyDescription == "key.wailatedump"){
			this.dumpTag(DataAccessor.instance.remoteNbt, 0);
		}
			
	}

	private void dumpTag(NBTTagCompound nbt, int tab){
		if (nbt == null) return;
		for (Object s : nbt.tagMap.keySet()){
			for (int i = 0; i < tab; i++)
				System.out.printf("\t");
			
			System.out.printf("[%s]%s : %s\n", nbt.getTag((String)s).getClass().getName(), s, nbt.getTag((String)s));
			if (nbt.getTag((String)s) instanceof NBTTagCompound){
				this.dumpTag((NBTTagCompound)nbt.getTag((String)s), tab + 1);
			}
			
			if (nbt.getTag((String)s) instanceof NBTTagByteArray){
				byte[] array = nbt.getByteArray((String)s);
				for (int i = 0; i < array.length; i++)
					System.out.printf("%s ", array[i]);
				System.out.printf("\n");
			}
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
