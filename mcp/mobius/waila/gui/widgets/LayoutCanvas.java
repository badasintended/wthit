package mcp.mobius.waila.gui.widgets;

import net.minecraft.client.gui.ScaledResolution;

import org.lwjgl.input.Mouse;
import org.lwjgl.util.Point;

import mcp.mobius.waila.gui.events.MouseEvent;
import mcp.mobius.waila.gui.events.MouseEvent.EventType;
import mcp.mobius.waila.gui.interfaces.CType;
import mcp.mobius.waila.gui.interfaces.IWidget;
import mcp.mobius.waila.gui.interfaces.Signal;

public class LayoutCanvas extends WidgetBase{ 

	private MouseEvent lastMouseEvent;
	private IWidget draggedWidget = null;
	
	public LayoutCanvas(){
		super();
		this.setGeometry(0, 0, this.rez.getScaledWidth(), this.rez.getScaledHeight(), CType.ABSXY, CType.ABSXY);
        Mouse.getDWheel();	// This is to "calibrate" the DWheel
        this.lastMouseEvent = new MouseEvent(this);
	}

	@Override
	public void draw(){
		this.rez = new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight); 		
		this.setGeometry(0, 0, this.rez.getScaledWidth(), this.rez.getScaledHeight(), CType.ABSXY, CType.ABSXY);
		
		this.draw(this.getPos());
		
		for (IWidget widget: this.widgets.values())
			if (widget.shouldRender())
				widget.draw();
		
		this.handleMouseInput();
	}	
	
	@Override
	public void draw(Point pos) {}
	
    @Override
    public void handleMouseInput(){
    	// Here we are going to generate the require mouse events we will pass down to all the other widgets
    	// This is more or less where the magic happens. If you want mouse support for a widget, it should be
    	// attached to a canvas like this one, or any inheriting one.
    	
    	MouseEvent event    = new MouseEvent(this);
    	EventType  type     = event.getEventType(this.lastMouseEvent);
    	IWidget    targetWidget = this.getWidgetAtCoordinates(event.x, event.y);
    	
    	switch (type){
			case CLICK:
				if (targetWidget != null)
					targetWidget.onMouseClick(event);
				//this.onMouseClick(event);
				break;				
			case DRAG:
				if (targetWidget != null)
					targetWidget.onMouseDrag(event);				
				//this.onMouseDrag(event);
				break;				
			case MOVE:
				if (targetWidget != null)
					targetWidget.onMouseMove(event);				
				//this.onMouseMove(event);
				break;
			case RELEASED:
				if (targetWidget != null)
					targetWidget.onMouseRelease(event);
				//this.onMouseRelease(event);
				break;
			case WHEEL:
				if (targetWidget != null)
					targetWidget.onMouseWheel(event);
				//this.onMouseWheel(event);
				break;
			case ENTER:
				if (event.trgwidget != null)
					event.trgwidget.onMouseEnter(event);
				if (this.lastMouseEvent.trgwidget != null){
					event.type = EventType.LEAVE;
					this.lastMouseEvent.trgwidget.onMouseLeave(event);
				}
				break;
			case NONE:
				break;
			default:
				break;
    	}
    	
    	this.lastMouseEvent = event;
    }	

    @Override
    public void onWidgetEvent(IWidget srcwidget, Signal signal, Object... params){
    	if (signal == Signal.DRAGGED)
    		this.draggedWidget = srcwidget;
    	else
    		super.onWidgetEvent(srcwidget, signal, params);
    }
    
    @Override
    public void onMouseRelease(MouseEvent event){
    	if(event.button == 0)
    		this.draggedWidget = null;
    	super.onMouseRelease(event);
    }
    
    @Override
    public void onMouseDrag(MouseEvent event){
    	if (this.draggedWidget != null)
    		this.draggedWidget.onMouseDrag(event);
    	else
    		super.onMouseDrag(event);
    }
}
