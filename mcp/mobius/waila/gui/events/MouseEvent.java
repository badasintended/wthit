package mcp.mobius.waila.gui.events;

import net.minecraft.client.Minecraft;

import org.lwjgl.input.Mouse;

import mcp.mobius.waila.gui.interfaces.IWidget;

public class MouseEvent {

	public enum EventType {NONE, MOVE, CLICK, RELEASED, DRAG, WHEEL, ENTER, LEAVE};
	
	public long timestamp;
	public Minecraft mc;
	public IWidget srcwidget;
	public IWidget trgwidget;
	public int x,y,z;
	public static int buttonCount = Mouse.getButtonCount();
    public boolean[]  buttonState = new boolean[buttonCount];
	public EventType type;
	public int button = -1;
    
    
	public MouseEvent(IWidget widget){
		this.srcwidget = widget;
		this.timestamp = System.nanoTime();
		
		this.mc  = Minecraft.getMinecraft();
		
        this.x = Mouse.getEventX() * this.srcwidget.getSize().getX() / this.mc.displayWidth;
        this.y = this.srcwidget.getSize().getY() - Mouse.getEventY() * this.srcwidget.getSize().getY() / this.mc.displayHeight - 1;
		
		//this.x = Mouse.getEventX();
		//this.y = Mouse.getEventY();
		
		//System.out.printf("%s %s\n", this.x, this.y);
		
        this.z = Mouse.getEventDWheel();
        
        for (int i = 0; i < buttonCount; i++)
        	buttonState[i] = Mouse.isButtonDown(i);
        
        this.trgwidget = this.srcwidget.getWidgetAtCoordinates(this.x, this.y);
	}
	
	public String toString(){
		String retstring = String.format("MOUSE %s :  [%s] [ %d %d %d ] [", this.type, this.timestamp, this.x, this.y, this.z);
		if (this.buttonCount < 5)
			for (int i = 0; i < this.buttonCount; i++)
				retstring += String.format(" %s ", this.buttonState[i]);
		else
			for (int i = 0; i < 5; i++)
				retstring += String.format(" %s ", this.buttonState[i]);			
		retstring += "]";
		
		if (this.button != -1)
			retstring += String.format(" Button %s", this.button);
		
		return retstring;
	}
	
	// Returns the event type based on the previous mouse event.
	public EventType getEventType(MouseEvent me){
		
		this.type = EventType.NONE;		
		
		if (this.trgwidget != me.trgwidget){
			this.type = EventType.ENTER;
			return this.type;
		}
		
		if (this.z != 0){
			this.type = EventType.WHEEL;
			return this.type;
		}
		
		for (int i = 0; i < this.buttonCount; i++){
			if (this.buttonState[i] != me.buttonState[i]){
				if (this.buttonState[i] == true)
					this.type = EventType.CLICK;
				else
					this.type = EventType.RELEASED;
				this.button = i;
				return this.type;
			}
		}
		
		//MOVE & DRAG EVENTS (we moved the mouse and button 0 was clicked or not)
		if ((this.x != me.x) || (this.y != me.y)){
			if (this.buttonState[0] == true)
				this.type = EventType.DRAG;
			else
				this.type = EventType.MOVE;
			return this.type;
		}
		
		return this.type;
	}
}
