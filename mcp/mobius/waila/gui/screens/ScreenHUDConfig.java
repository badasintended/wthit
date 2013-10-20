package mcp.mobius.waila.gui.screens;

import mcp.mobius.waila.gui.events.Signal;
import mcp.mobius.waila.gui.events.Slot;
import mcp.mobius.waila.gui.widgets.LabelFixedFont;
import mcp.mobius.waila.gui.widgets.PictureMovable;
import mcp.mobius.waila.gui.widgets.WidgetGeometry;
import mcp.mobius.waila.gui.widgets.buttons.ButtonLabel;
import mcp.mobius.waila.gui.widgets.logic.CounterInteger;
import net.minecraft.client.gui.GuiScreen;

public class ScreenHUDConfig extends ScreenBase {

	public ScreenHUDConfig(GuiScreen parent){
		super(parent);
		
		this.getRoot().addWidget("Picture", new PictureMovable(this.getRoot(), "waila:textures/test.png"));
		
		this.getRoot().addWidget("ButtonXAdd", new ButtonLabel(this.getRoot(), "+"));
		this.getRoot().getWidget("ButtonXAdd").setGeometry(new WidgetGeometry(50.0,60,20,20,true, false, false, false, WidgetGeometry.Align.CENTER, WidgetGeometry.Align.TOP));
		this.getRoot().addWidget("ButtonXSub", new ButtonLabel(this.getRoot(), "-"));
		this.getRoot().getWidget("ButtonXSub").setGeometry(new WidgetGeometry(50.0,80,20,20,true, false, false, false, WidgetGeometry.Align.CENTER, WidgetGeometry.Align.TOP));

		this.getRoot().addWidget("ButtonYAdd", new ButtonLabel(this.getRoot(), "+"));
		this.getRoot().getWidget("ButtonYAdd").setGeometry(new WidgetGeometry(55.0,60,20,20,true, false, false, false, WidgetGeometry.Align.CENTER, WidgetGeometry.Align.TOP));
		this.getRoot().addWidget("ButtonYSub", new ButtonLabel(this.getRoot(), "-"));
		this.getRoot().getWidget("ButtonYSub").setGeometry(new WidgetGeometry(55.0,80,20,20,true, false, false, false, WidgetGeometry.Align.CENTER, WidgetGeometry.Align.TOP));		
		
		this.getRoot().addWidget("TextX",   new LabelFixedFont(this.getRoot(), "None"));
		this.getRoot().getWidget("TextX").setGeometry(new WidgetGeometry(50.0,0,0,0,true, false, WidgetGeometry.Align.CENTER, WidgetGeometry.Align.TOP));
		this.getRoot().addWidget("TextY",   new LabelFixedFont(this.getRoot(), "None"));
		this.getRoot().getWidget("TextY").setGeometry(new WidgetGeometry(50.0,20,0,0,true, false, WidgetGeometry.Align.CENTER, WidgetGeometry.Align.TOP));
		
		this.getRoot().addWidget("CounterX", new CounterInteger(this.getRoot(), 0, 5));
		this.getRoot().addWidget("CounterY", new CounterInteger(this.getRoot(), 0, 5));

		/* LINKING THE LOGIC */
		this.getRoot().getWidget("ButtonXAdd").attach(this.getRoot().getWidget("CounterX"), Signal.CLICKED, Slot.ADD_STEP);
		this.getRoot().getWidget("ButtonXSub").attach(this.getRoot().getWidget("CounterX"), Signal.CLICKED, Slot.SUB_STEP);

		this.getRoot().getWidget("ButtonYAdd").attach(this.getRoot().getWidget("CounterY"), Signal.CLICKED, Slot.ADD_STEP);
		this.getRoot().getWidget("ButtonYSub").attach(this.getRoot().getWidget("CounterY"), Signal.CLICKED, Slot.SUB_STEP);		
		
		this.getRoot().getWidget("Picture").attach(this.getRoot().getWidget("CounterX"), Signal.DRAGGED_X, Slot.SET_VALUE);
		this.getRoot().getWidget("Picture").attach(this.getRoot().getWidget("CounterY"), Signal.DRAGGED_Y, Slot.SET_VALUE);
		
		this.getRoot().getWidget("CounterX").attach(this.getRoot().getWidget("TextX"),   Signal.VALUE_CHANGED, Slot.SET_VALUE);
		this.getRoot().getWidget("CounterY").attach(this.getRoot().getWidget("TextY"),   Signal.VALUE_CHANGED, Slot.SET_VALUE);
		this.getRoot().getWidget("CounterX").attach(this.getRoot().getWidget("Picture"), Signal.VALUE_CHANGED, Slot.SET_POS_X);
		this.getRoot().getWidget("CounterY").attach(this.getRoot().getWidget("Picture"), Signal.VALUE_CHANGED, Slot.SET_POS_Y);
		
	}

}
