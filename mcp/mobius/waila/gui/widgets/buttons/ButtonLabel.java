package mcp.mobius.waila.gui.widgets.buttons;

import mcp.mobius.waila.gui.events.MouseEvent;
import mcp.mobius.waila.gui.interfaces.CoordType;
import mcp.mobius.waila.gui.interfaces.IWidget;
import mcp.mobius.waila.gui.interfaces.WidgetAlign;
import mcp.mobius.waila.gui.widgets.LabelFixedFont;
import mcp.mobius.waila.gui.widgets.WidgetGeometry;
import net.minecraft.client.gui.GuiScreen;

public class ButtonLabel extends ButtonBase {
	
	public ButtonLabel(IWidget parent, String text){
		super(parent);
		
		this.addWidget("Label", new LabelFixedFont(this, text));
		this.getWidget("Label").setGeometry(new WidgetGeometry(50.0D, 50.0D, 100.0D, 20.0D, CoordType.RELXY, CoordType.ABS, WidgetAlign.CENTER, WidgetAlign.CENTER));		
	}
	
	@Override
	public void onMouseClick(MouseEvent event){
		super.onMouseClick(event);			
	}
}
