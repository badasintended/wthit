package mcp.mobius.waila.gui.widgets.logic;

import org.lwjgl.util.Point;

import mcp.mobius.waila.gui.interfaces.IWidget;
import mcp.mobius.waila.gui.widgets.WidgetBase;

public class CounterInteger extends WidgetBase {

	public int value;
	
	public CounterInteger(IWidget parent){
		this(parent, 0);
	}
	
	public CounterInteger(IWidget parent, int value){
		this.value = value;
	}
	
	@Override
	public void draw(Point pos) {}

	public void setValue(int value_){
		if (value_ == this.value) return;
		
		this.value = value_;
		this.emit("valueChanged", this.value);
	}
	
	public void add(int value_){
		if (value_ == 0) return;
		
		this.value += value_;
		this.emit("valueChanged", this.value);		
	}
	
	public void sub(int value_){
		if (value_ == 0) return;
		
		this.value -= value_;
		this.emit("valueChanged", this.value);
	}
	
	public void add1(){
		this.value += 1;
		this.emit("valueChanged", this.value);
	}
	
	public void sub1(){
		this.value -= 1;
		this.emit("valueChanged", this.value);
	}
	
	public int  getValue()         {return this.value;}
	
	@Override
	public void onWidgetEvent(IWidget srcwidget, String eventname,	String slotname, Object... params) {
		if (slotname.equals("setValue"))
			this.setValue((Integer) params[0]);
		
		if (slotname.equals("addValue"))
			this.add((Integer) params[0]);
		
		if (slotname.equals("subValue"))
			this.sub((Integer) params[0]);
		
		if (slotname.equals("addOne"))
			this.add1();;
		
		if (slotname.equals("subOne"))
			this.sub1();
	}
	
}
