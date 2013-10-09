package mcp.mobius.waila.gui.widgets;



import net.minecraft.client.gui.ScaledResolution;

import org.lwjgl.util.Point;

import mcp.mobius.waila.gui.interfaces.IWidget;
import mcp.mobius.waila.gui.interfaces.IWidgetContainer;

public class LayoutCanvas extends WidgetBase{ 
	
	public LayoutCanvas(){
		super();
		this.setGeometry(0, 0, this.rez.getScaledWidth(), this.rez.getScaledHeight());
	}

	@Override
	public void draw(){
		this.rez = new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight); 		
		this.setGeometry(0, 0, this.rez.getScaledWidth(), this.rez.getScaledHeight());
		
		this.draw(this.getPos());
		
		for (IWidget widget: this.widgets.values())
			widget.draw();		
	}	
	
	@Override
	public void draw(Point pos) {}

}
