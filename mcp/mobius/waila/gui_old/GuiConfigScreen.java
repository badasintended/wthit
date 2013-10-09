package mcp.mobius.waila.gui_old;

import mcp.mobius.waila.gui_old.widget_old.ButtonChangeScreen;
import mcp.mobius.waila.gui_old.widget_old.ContainerButtons;
import net.minecraft.client.gui.GuiScreen;

public class GuiConfigScreen extends BaseWailaScreen {

	public GuiConfigScreen(GuiScreen _parentGui) {
		super(_parentGui);
	}

   @Override
   /* We init the GUI data here (buttons, etc) */
	public void initGui()
    {
        buttonList.add(new ButtonChangeScreen(200, width / 2 - 100, height / 6 + 168, "Done", null));
        ContainerButtons container = new ContainerButtons(this, 5, height/2-10, width - 10, height);
        container.addButton(new ButtonChangeScreen(201, "Waila",   new GuiWailaConfig(this) ));
        container.addButton(new ButtonChangeScreen(202, "Modules", new GuiModules(this)));
    }	
	
}
