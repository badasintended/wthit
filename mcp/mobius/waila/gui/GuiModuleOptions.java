package mcp.mobius.waila.gui;

import mcp.mobius.waila.addons.ConfigHandler;
import mcp.mobius.waila.gui.widget.ButtonChangeScreen;
import mcp.mobius.waila.gui.widget.ButtonConfigOption;
import mcp.mobius.waila.gui.widget.ContainerButtons;
import net.minecraft.client.gui.GuiScreen;

public class GuiModuleOptions extends GuiBaseConfigScreen {

	private String modName;
	
	public GuiModuleOptions(GuiScreen _parentGui, String _modName) {
		super(_parentGui);
		this.modName = _modName;
	}

	public void initGui()
    {
        buttonList.add(new ButtonChangeScreen(200, width / 2 - 100, height / 6 + 168, "Back", this.parentGui));
        ContainerButtons container = new ContainerButtons(this, 5, 0, width - 10, height);
        
        for (String key:ConfigHandler.instance().getConfigKeys(this.modName).keySet()){
        	container.addButton(new ButtonConfigOption(-1, "No", "Yes", ConfigHandler.instance().getConfigKeys(this.modName).get(key), key ));
        }
    }
	
}
