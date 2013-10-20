package mcp.mobius.waila.gui.screens;

import mcp.mobius.waila.gui.widgets.LabelFixedFont;
import mcp.mobius.waila.gui.widgets.PictureMovable;
import mcp.mobius.waila.gui.widgets.WidgetGeometry;
import mcp.mobius.waila.gui.widgets.logic.CounterInteger;
import net.minecraft.client.gui.GuiScreen;

public class ScreenHUDConfig extends ScreenBase {

	public ScreenHUDConfig(GuiScreen parent){
		super(parent);
		
		this.getRoot().addWidget("Picture", new PictureMovable(this.getRoot(), "waila:textures/test.png"));
		
		this.getRoot().addWidget("TextX",   new LabelFixedFont(this.getRoot(), "None"));
		this.getRoot().getWidget("TextX").setGeometry(new WidgetGeometry(50.0,0,0,0,true, false, WidgetGeometry.Align.CENTER, WidgetGeometry.Align.TOP));
		this.getRoot().addWidget("TextY",   new LabelFixedFont(this.getRoot(), "None"));
		this.getRoot().getWidget("TextY").setGeometry(new WidgetGeometry(50.0,20,0,0,true, false, WidgetGeometry.Align.CENTER, WidgetGeometry.Align.TOP));
		
		this.getRoot().addWidget("CounterX", new CounterInteger(this.getRoot()));
		this.getRoot().addWidget("CounterY", new CounterInteger(this.getRoot()));

		/* LINKING THE LOGIC */
		this.getRoot().getWidget("Picture").attach(this.getRoot().getWidget("CounterX"), "onDragX", "setValue");
		this.getRoot().getWidget("Picture").attach(this.getRoot().getWidget("CounterY"), "onDragY", "setValue");
		
		this.getRoot().getWidget("CounterX").attach(this.getRoot().getWidget("TextX"), "valueChanged", "setText");
		this.getRoot().getWidget("CounterY").attach(this.getRoot().getWidget("TextY"), "valueChanged", "setText");
	}

}
