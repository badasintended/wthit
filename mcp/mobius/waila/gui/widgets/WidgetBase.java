package mcp.mobius.waila.gui.widgets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.Point;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.texture.TextureManager;
import mcp.mobius.waila.gui.events.MouseEvent;
import mcp.mobius.waila.gui.helpers.ReverseIterator;
import mcp.mobius.waila.gui.interfaces.CType;
import mcp.mobius.waila.gui.interfaces.IWidget;
import mcp.mobius.waila.gui.interfaces.Signal;

public abstract class WidgetBase implements IWidget {

	protected IWidget parent;
	protected WidgetGeometry  geom;
    protected LinkedHashMap<String, IWidget> widgets = new LinkedHashMap<String, IWidget>();;	
	protected Minecraft mc;
    protected TextureManager texManager;
    protected ScaledResolution rez;

	protected boolean hasBlending;
	protected boolean hasLight;
	protected int     boundTexIndex;      
    
	protected boolean isRendering = true;
	
	protected float   alpha = 1.0f;
	
	public WidgetBase(){
		this.setParent(null);
		this.mc  = Minecraft.getMinecraft();	
		this.rez = new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight); 
		this.texManager = this.mc.renderEngine;
		this.setGeometry(new WidgetGeometry(0, 0, 50, 50, CType.ABSXY, CType.ABSXY));
	}
	
	public WidgetBase(IWidget parent){
		this.setParent(parent);
		this.mc  = Minecraft.getMinecraft();
		this.rez = new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight);
		this.texManager = this.mc.renderEngine;
		this.setGeometry(new WidgetGeometry(0, 0, 50, 50, CType.ABSXY, CType.ABSXY));
	}
    
	/////////////////////
	// WIDGET HANDLING //
	/////////////////////	
	
	@Override
	public IWidget addWidget(String name, IWidget widget){
		widget.setParent(this);
		this.widgets.put(name, widget);
		return this.getWidget(name);
	}
	
	@Override
	public IWidget getWidget(String name){
		return this.widgets.get(name);
	}
	
	@Override
	public IWidget delWidget(String name){
		IWidget widget = this.getWidget(name);
		this.widgets.remove(widget);
		return widget;
	}

	@Override
	public IWidget getWidgetAtCoordinates(double posX, double posY){
		for (IWidget widget : new ReverseIterator<IWidget>(this.widgets.values()))
			if ((posX >=  widget.getPos().getX()) && 
					(posX <=  widget.getPos().getX() + widget.getSize().getX()) &&
					(posY >=  widget.getPos().getY()) &&
					(posY <=  widget.getPos().getY() + widget.getSize().getY()))
				return widget.getWidgetAtCoordinates(posX, posY);
		
		if ((posX >=  this.getPos().getX()) && 
				(posX <=  this.getPos().getX() + this.getSize().getX()) &&
				(posY >=  this.getPos().getY()) &&
				(posY <=  this.getPos().getY() + this.getSize().getY()))
			return this;		
		
		return null;
	}		
	
	@Override
	public boolean isWidgetAtCoordinates(double posx, double posy){
		if (this.getLeft()   > posx) return false;
		if (this.getRight()  < posx) return false;
		if (this.getTop()    > posy) return false;
		if (this.getBottom() < posy) return false;
		return true;
	}
	
	///////////////////////
	// IWIDGET INTERFACE //
	///////////////////////
    
	@Override
	public IWidget getParent() { return this.parent; }
	@Override
	public void    setParent(IWidget parent) { this.parent = parent; }
	
	@Override
	public void draw(){
		this.saveGLState();
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA,GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glColor4f(1.0f, 1.0f, 1.0f, this.alpha);
		
		this.draw(this.getPos());
		
		for (IWidget widget: this.widgets.values())
			if (widget.shouldRender())
				widget.draw();		

		this.loadGLState();		
	}

	@Override
	public abstract void draw(Point pos);

	@Override
	public IWidget setGeometry(WidgetGeometry geom) { 
		this.geom = geom; 
		this.emit(Signal.GEOM_CHANGED, this.geom);  
		return this;
	}
	
	public IWidget setGeometry(double x, double y, double sx, double sy, CType fp, CType fs){ 
		this.setGeometry(new WidgetGeometry (x, y, sx, sy, fp, fs));
		this.emit(Signal.GEOM_CHANGED, this.geom);
		return this;
	}
	
	@Override
	public WidgetGeometry getGeometry() { return this.geom;	}
	@Override
	public Point getPos() {	return this.geom.getPos(this.parent); }
	@Override
	public Point getSize() { return this.geom.getSize(this.parent); }
	@Override
	public int getLeft() {	return this.getPos().getX(); }		
	@Override
	public int getRight() {	return this.getPos().getX() + this.getSize().getX(); }	
	@Override
	public int getTop() {	return this.getPos().getY(); }		
	@Override
	public int getBottom() {	return this.getPos().getY() + this.getSize().getY(); }	

	@Override	
	public IWidget setPos(double x, double y){
		return this.setPos(x, y, this.geom.fracPosX, this.geom.fracPosY);
	}
	
	@Override	
	public IWidget setPos(double x, double y, boolean fracX, boolean fracY){
		this.geom.setPos(x, y, fracX, fracY);
		this.emit(Signal.GEOM_CHANGED, this.geom);		
		return this;		
	};	
	
	@Override	
	public IWidget setSize(double sx, double sy){
		return this.setSize(sx, sy, this.geom.fracSizeX, this.geom.fracSizeY);
	}
	
	@Override	
	public IWidget setSize(double sx, double sy, boolean fracX, boolean fracY){
		this.geom.setSize(sx, sy, fracX, fracY);
		this.emit(Signal.GEOM_CHANGED, this.geom);		
		return this;		
	};		
	
	@Override
	public IWidget adjustSize(){
		int minleft   = 9999;
		int maxright  = 0;
		int mintop    = 9999;
		int maxbottom = 0;
		
		for (IWidget widget : this.widgets.values()){
			minleft  = Math.min(minleft, widget.getLeft());
			maxright = Math.max(maxright, widget.getRight());
			
			mintop    = Math.min(mintop,    widget.getTop());
			maxbottom = Math.max(maxbottom, widget.getBottom());			
		}
		
		this.setSize(maxright - minleft, maxbottom - mintop, false, false);
		
		this.emit(Signal.GEOM_CHANGED, this.geom);			
		return this;
	}
	
	////////////////////////////
	// SOME RENDERING HELPERS //
	////////////////////////////
	
    protected void saveGLState(){
		hasBlending   = GL11.glGetBoolean(GL11.GL_BLEND);
		hasLight      = GL11.glGetBoolean(GL11.GL_LIGHTING);
    	boundTexIndex = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);  
    	GL11.glPushAttrib(GL11.GL_CURRENT_BIT);
    	GL11.glPushMatrix();
    }
    
    protected void loadGLState(){
    	if (hasBlending) GL11.glEnable(GL11.GL_BLEND); else GL11.glDisable(GL11.GL_BLEND);
    	if (hasLight) GL11.glEnable(GL11.GL_LIGHTING); else	GL11.glDisable(GL11.GL_LIGHTING);
    	GL11.glBindTexture(GL11.GL_TEXTURE_2D, boundTexIndex);
    	GL11.glPopMatrix();
    	GL11.glPopAttrib();
    	//GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
    }
    
    @Override 
    public void setAlpha(float alpha){this.alpha = alpha;};
    
    @Override
    public float getAlpha(){return this.alpha;};
    
	@Override
	public void  show(){this.isRendering = true;};
	@Override
	public void  hide(){this.isRendering = false;};
	@Override
	public boolean shouldRender(){return this.isRendering;};    
    
    ////////////////////
    // INPUT HANDLING //
    ////////////////////
    
    @Override
    public void handleMouseInput(){}

	@Override
	public void onMouseClick(MouseEvent event) {
		//System.out.printf("%s %s\n", this, event);
		if (this.parent != null)
			this.parent.onMouseClick(event);			
		
		//IWidget widget = this.getWidgetAtCoordinates(event.x, event.y);
		//if (widget != null && widget != this)
		//	widget.onMouseClick(event);		
	}

	@Override
	public void onMouseDrag(MouseEvent event) {
		//System.out.printf("%s %s\n", this, event);
		if (this.parent != null)
			this.parent.onMouseDrag(event);			
		
		//IWidget widget = this.getWidgetAtCoordinates(event.x, event.y);
		//if (widget != null && widget != this)
		//	widget.onMouseDrag(event);		
	}

	@Override
	public void onMouseMove(MouseEvent event) {
		//System.out.printf("%s %s\n", this, event);
		if (this.parent != null)
			this.parent.onMouseMove(event);		
		
		
		//IWidget widget = this.getWidgetAtCoordinates(event.x, event.y);
		//if (widget != null && widget != this)
		//	widget.onMouseMove(event);
	}

	@Override
	public void onMouseRelease(MouseEvent event) {
		//System.out.printf("%s %s\n", this, event);
		if (this.parent != null)
			this.parent.onMouseRelease(event);		
		
		//IWidget widget = this.getWidgetAtCoordinates(event.x, event.y);
		//if (widget != null && widget != this)
		//	widget.onMouseRelease(event);		
	}

	@Override
	public void onMouseWheel(MouseEvent event) {
		//System.out.printf("%s %s\n", this, event);
		if (this.parent != null)
			this.parent.onMouseWheel(event);
	}

	@Override
	public void onMouseEnter(MouseEvent event) {
		//System.out.printf("%s %s\n", this, event);
		if (this.parent != null)
			this.parent.onMouseEnter(event);	
	}
	
	@Override
	public void onMouseLeave(MouseEvent event) {
		//System.out.printf("%s %s\n", this, event);
		if (this.parent != null)
			this.parent.onMouseLeave(event);		
	}

	@Override
	public void emit(Signal signal, Object... params) {
		if(this.parent != null)
			this.parent.onWidgetEvent(this, signal, params);
	}

	@Override
	public void onWidgetEvent(IWidget srcwidget, Signal signal, Object... params) {
		if (this.parent != null)
			this.parent.onWidgetEvent(srcwidget, signal, params);
	}
}
