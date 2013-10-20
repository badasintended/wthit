package mcp.mobius.waila.gui.widgets.logic;

import org.lwjgl.util.Point;

import mcp.mobius.waila.gui.events.Signal;
import mcp.mobius.waila.gui.events.Slot;
import mcp.mobius.waila.gui.interfaces.IWidget;
import mcp.mobius.waila.gui.widgets.WidgetBase;

public class CounterInteger extends WidgetBase {

	public int value;
	public int step = 1;
	
	public CounterInteger(IWidget parent){
		this(parent, 0);
	}
	
	public CounterInteger(IWidget parent, int value){
		this(parent, 0, 1);
	}

	public CounterInteger(IWidget parent, int value, int step){
		this.value = value;
		this.step  = step;
	}	
	
	@Override
	public void draw(Point pos) {}

	public void setValue(int value_){
		if (value_ == this.value) return;
		
		this.value = value_;
		this.emit(Signal.VALUE_CHANGED, this.value);
	}
	
	public void add(int value_){
		if (value_ == 0) return;
		
		this.value += value_;
		this.emit(Signal.VALUE_CHANGED, this.value);		
	}
	
	public void sub(int value_){
		if (value_ == 0) return;
		
		this.value -= value_;
		this.emit(Signal.VALUE_CHANGED, this.value);
	}
	
	public void add1(){
		this.value += 1;
		this.emit(Signal.VALUE_CHANGED, this.value);
	}
	
	public void sub1(){
		this.value -= 1;
		this.emit(Signal.VALUE_CHANGED, this.value);
	}

	public void addStep(){
		this.value += this.step;
		this.emit(Signal.VALUE_CHANGED, this.value);
	}
	
	public void subStep(){
		this.value -= this.step;
		this.emit(Signal.VALUE_CHANGED, this.value);
	}	
	
	public int  getValue()         {return this.value;}
	
	@Override
	public void onWidgetEvent(IWidget srcwidget, Signal signal,	Slot slot, Object... params) {
		switch(slot){
		case SET_VALUE:
			this.setValue((Integer) params[0]);
			break;
		
		case ADD_VALUE:
			this.add((Integer) params[0]);
			break;
		case ADD_ONE:
			this.add1();
			break;
		case ADD_STEP:
			this.addStep();
			break;
			
		case SUB_VALUE:
			this.sub((Integer) params[0]);
			break;			
		case SUB_ONE:
			this.sub1();
			break;
		case SUB_STEP:
			this.subStep();
			break;

		default:
			break;
		
		}
	}
	
}
