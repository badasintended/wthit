package mcp.mobius.waila.gui.widgets.buttons;

import net.minecraft.client.gui.GuiScreen;
import mcp.mobius.waila.gui.events.MouseEvent;
import mcp.mobius.waila.gui.interfaces.CType;
import mcp.mobius.waila.gui.interfaces.IWidget;
import mcp.mobius.waila.gui.interfaces.WAlign;
import mcp.mobius.waila.gui.screens.ScreenBase;
import mcp.mobius.waila.gui.widgets.LabelFixedFont;
import mcp.mobius.waila.gui.widgets.WidgetGeometry;

public class ButtonScreenChange extends ButtonBase {
	
	GuiScreen linkedScreen;
	
	public ButtonScreenChange(IWidget parent, String text, GuiScreen linkedscreen){
		super(parent);
		this.linkedScreen = linkedscreen;
		
		this.addWidget("Label", new LabelFixedFont(this, text));
		this.getWidget("Label").setGeometry(new WidgetGeometry(50.0D, 50.0D, 100.0D, 20.0D, CType.RELXY, CType.ABSXY, WAlign.CENTER, WAlign.CENTER));		
	}
	
	@Override
	public void onMouseClick(MouseEvent event){
		super.onMouseClick(event);			
		
		if (event.button == 0)
			this.mc.displayGuiScreen(this.linkedScreen);
	}
}
