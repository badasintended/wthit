package mcp.mobius.waila.gui_old;

import java.util.HashMap;

import mcp.mobius.waila.addons.ConfigHandler;
import mcp.mobius.waila.gui_old.widget_old.ButtonChangeScreen;
import mcp.mobius.waila.gui_old.widget_old.ContainerButtons;
import net.minecraft.client.gui.GuiScreen;

public class GuiModules extends BaseWailaScreen {

	private HashMap<String, GuiScreen> screens = new HashMap<String, GuiScreen>();
	
	public GuiModules(GuiScreen _parentGui) {
		super(_parentGui);
        for (String key : ConfigHandler.instance().getModuleNames())
        	screens.put(key, new GuiModuleOptions(this, key));
		
	}

	@Override
	public void initGui()
    {
        buttonList.add(new ButtonChangeScreen(200, width / 2 - 100, height / 6 + 168, "Back", this.parentGui));
        ContainerButtons container = new ContainerButtons(this, 5, 0, width - 10, height);

        for (String key : ConfigHandler.instance().getModuleNames())
        	container.addButton(new ButtonChangeScreen(-1, key,   screens.get(key)));
    }	
	
}
