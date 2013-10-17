package mcp.mobius.waila.gui.widgets.buttons;

import mcp.mobius.waila.gui.events.MouseEvent;
import mcp.mobius.waila.gui.interfaces.IWidget;
import mcp.mobius.waila.gui.screens.ScreenBase;
import mcp.mobius.waila.gui.widgets.LabelFixedFont;
import mcp.mobius.waila.gui.widgets.WidgetGeometry;

public class ButtonScreenChange extends ButtonBase {
	
	ScreenBase linkedScreen;
	
	public ButtonScreenChange(IWidget parent, String text, ScreenBase linkedscreen){
		super(parent);
		this.linkedScreen = linkedscreen;
		
		this.addWidget("Label", new LabelFixedFont(this, text));
		this.getWidget("Label").setGeometry(new WidgetGeometry(50.0D, 50.0D, 100.0D, 20.0D, true, false, WidgetGeometry.Align.CENTER, WidgetGeometry.Align.CENTER));		
	}
	
	@Override
	public void onMouseClick(MouseEvent event){
		if (event.button == 0)
			this.mc.displayGuiScreen(this.linkedScreen);
	}
}
