package mcp.mobius.waila.gui;

import mcp.mobius.waila.gui.widget.ButtonChangeScreen;
import mcp.mobius.waila.gui.widget.ButtonConfigNEI;
import mcp.mobius.waila.gui.widget.ButtonConfigOption;
import mcp.mobius.waila.gui.widget.ContainerButtons;
import net.minecraft.client.gui.GuiScreen;

public class GuiWailaConfig extends GuiBaseConfigScreen {

	public GuiWailaConfig(GuiScreen _parentGui) {
		super(_parentGui);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void initGui()
    {
        buttonList.add(new ButtonChangeScreen(200, width / 2 - 100, height / 6 + 168, "Back", this.parentGui));
        
        ContainerButtons container = new ContainerButtons(this, 5, 0, width - 10, height);
        container.addButton(new ButtonConfigNEI(-1, "Hidden",   "Shown", "Show/Hide Waila", "options.inworld tooltips" ));
        container.addButton(new ButtonConfigOption(-1, "Pressed", "Toggled", "Toggle/Maintened", "waila.showmode" ));        
       
    }
	
}
