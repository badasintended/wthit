package mcp.mobius.waila.gui;

import java.util.HashMap;

import mcp.mobius.waila.addons.ConfigHandler;
import mcp.mobius.waila.gui.widget.ButtonChangeScreen;
import mcp.mobius.waila.gui.widget.ContainerButtons;
import net.minecraft.client.gui.GuiScreen;

public class GuiModules extends GuiBaseConfigScreen {

	private HashMap<String, GuiScreen> screens = new HashMap<String, GuiScreen>();
	
	public GuiModules(GuiScreen _parentGui) {
		super(_parentGui);
        for (String key : ConfigHandler.instance().getModuleNames())
        	screens.put(key, new GuiModuleOptions(this, key));
		
	}

	public void initGui()
    {
        buttonList.add(new ButtonChangeScreen(200, width / 2 - 100, height / 6 + 168, "Back", this.parentGui));
        ContainerButtons container = new ContainerButtons(this, 5, 0, width - 10, height);

        for (String key : ConfigHandler.instance().getModuleNames())
        	container.addButton(new ButtonChangeScreen(-1, key,   screens.get(key)));
    }	
	
}
