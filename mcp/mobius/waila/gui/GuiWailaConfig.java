package mcp.mobius.waila.gui;

import mcp.mobius.waila.Constants;
import mcp.mobius.waila.gui.widget_old.ButtonChangeScreen;
import mcp.mobius.waila.gui.widget_old.ButtonConfigNEI;
import mcp.mobius.waila.gui.widget_old.ButtonConfigOption;
import mcp.mobius.waila.gui.widget_old.ContainerButtons;
import net.minecraft.client.gui.GuiScreen;

public class GuiWailaConfig extends BaseWailaScreen {

	public GuiWailaConfig(GuiScreen _parentGui) {
		super(_parentGui);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void initGui()
    {
        buttonList.add(new ButtonChangeScreen(200, width / 2 - 100, height / 6 + 168, "Back", this.parentGui));
        
        ContainerButtons container = new ContainerButtons(this, 5, 0, width - 10, height);
        container.addButton(new ButtonConfigOption(-1, "Hidden",     "Shown",   "Show/Hide Waila",   Constants.CFG_WAILA_SHOW ));
        container.addButton(new ButtonConfigOption(-1, "Maintained", "Toggled", "Toggle/Maintained", Constants.CFG_WAILA_MODE ));
        container.addButton(new ButtonConfigOption(-1, "Hidden",     "Visible", "Show ID:Metadata",  "waila.showmetadata", false ));
       
    }
	
}
