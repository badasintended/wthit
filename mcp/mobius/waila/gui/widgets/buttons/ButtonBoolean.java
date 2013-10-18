package mcp.mobius.waila.gui.widgets.buttons;

import mcp.mobius.waila.gui.events.MouseEvent;
import mcp.mobius.waila.gui.helpers.UIHelper;
import mcp.mobius.waila.gui.interfaces.IWidget;
import mcp.mobius.waila.gui.widgets.LabelFixedFont;
import mcp.mobius.waila.gui.widgets.WidgetGeometry;
import mcp.mobius.waila.gui.widgets.WidgetGeometry.Align;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.util.Point;

public class ButtonBoolean extends ButtonBase {

	protected boolean state     = false;
	
	public ButtonBoolean(IWidget parent, String textFalse, String textTrue){
		super(parent);
		
		this.addWidget("LabelFalse", new LabelFixedFont(this, textFalse));
		this.getWidget("LabelFalse").setGeometry(new WidgetGeometry(50.0D, 50.0D, 100.0D, 20.0D, true, false, WidgetGeometry.Align.CENTER, WidgetGeometry.Align.CENTER));		
		this.addWidget("LabelTrue",  new LabelFixedFont(this, textTrue));
		this.getWidget("LabelTrue").hide();
		this.getWidget("LabelTrue").setGeometry(new WidgetGeometry(50.0D, 50.0D, 100.0D, 20.0D, true, false, WidgetGeometry.Align.CENTER, WidgetGeometry.Align.CENTER));		
	}
	
	@Override
	public void onMouseClick(MouseEvent event){
		super.onMouseClick(event);
		
		if (event.button == 0)
			this.state = !this.state;
		
		if (this.state){
			this.getWidget("LabelTrue").show();
			this.getWidget("LabelFalse").hide();
		} else {
			this.getWidget("LabelTrue").hide();
			this.getWidget("LabelFalse").show();			
		}
	}
}
