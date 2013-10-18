package mcp.mobius.waila.gui.widgets;

import java.util.HashMap;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.Point;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.texture.TextureManager;
import mcp.mobius.waila.gui.events.MouseEvent;
import mcp.mobius.waila.gui.interfaces.IWidget;

public abstract class WidgetBase implements IWidget {

	protected IWidget parent;
	protected WidgetGeometry  geom;
    protected HashMap<String, IWidget> widgets = new HashMap<String, IWidget>();;	
	protected Minecraft mc;
    protected TextureManager texManager;
    protected ScaledResolution rez;

	protected boolean hasBlending;
	protected boolean hasLight;
	protected int     boundTexIndex;      
    
	protected boolean isRendering = true;
	
	public WidgetBase(){
		this.setParent(null);
		this.mc  = Minecraft.getMinecraft();	
		this.rez = new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight); 
		this.texManager = this.mc.renderEngine;
		this.setGeometry(new WidgetGeometry(0,0,50,50,false,false));		
	}
	
	public WidgetBase(IWidget parent){
		this.setParent(parent);
		this.mc  = Minecraft.getMinecraft();
		this.rez = new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight);
		this.texManager = this.mc.renderEngine;
		this.setGeometry(new WidgetGeometry(0,0,50,50,false,false));
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
	public IWidget getWidgetAtCoordinates(int posX, int posY){
		for (IWidget widget : this.widgets.values())
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
	
	///////////////////////
	// IWIDGET INTERFACE //
	///////////////////////
    
	@Override
	public IWidget getParent() { return this.parent; }
	@Override
	public void    setParent(IWidget parent) { this.parent = parent; }
	
	@Override
	public void draw(){
		this.draw(this.getPos());
		
		for (IWidget widget: this.widgets.values())
			if (widget.shouldRender())
				widget.draw();		
	}

	@Override
	public abstract void draw(Point pos);

	@Override
	public void setGeometry(WidgetGeometry geom) { this.geom = geom; }
	public void setGeometry(double x, double y, double sx, double sy, boolean fp, boolean fs){ this.setGeometry(new WidgetGeometry (x, y, sx, sy, fp, fs)) ;}
	
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
	
	////////////////////////////
	// SOME RENDERING HELPERS //
	////////////////////////////
	
    protected void saveGLState(){
		hasBlending   = GL11.glGetBoolean(GL11.GL_BLEND);
		hasLight      = GL11.glGetBoolean(GL11.GL_LIGHTING);
    	boundTexIndex = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);    	
    }
    
    protected void loadGLState(){
    	if (hasBlending) GL11.glEnable(GL11.GL_BLEND); else GL11.glDisable(GL11.GL_BLEND);
    	if (hasLight) GL11.glEnable(GL11.GL_LIGHTING); else	GL11.glDisable(GL11.GL_LIGHTING);
    	GL11.glBindTexture(GL11.GL_TEXTURE_2D, boundTexIndex);    	
    }
    
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
		
		IWidget widget = this.getWidgetAtCoordinates(event.x, event.y);
		if (widget != null && widget != this)
			widget.onMouseClick(event);		
	}

	@Override
	public void onMouseDrag(MouseEvent event) {
		//System.out.printf("%s %s\n", this, event);
		
		IWidget widget = this.getWidgetAtCoordinates(event.x, event.y);
		if (widget != null && widget != this)
			widget.onMouseDrag(event);		
	}

	@Override
	public void onMouseMove(MouseEvent event) {
		//System.out.printf("%s %s\n", this, event);
		
		IWidget widget = this.getWidgetAtCoordinates(event.x, event.y);
		if (widget != null && widget != this)
			widget.onMouseMove(event);
	}

	@Override
	public void onMouseRelease(MouseEvent event) {
		//System.out.printf("%s %s\n", this, event);
		
		IWidget widget = this.getWidgetAtCoordinates(event.x, event.y);
		if (widget != null && widget != this)
			widget.onMouseRelease(event);		
	}

	@Override
	public void onMouseWheel(MouseEvent event) {
		//System.out.printf("%s %s\n", this, event);
		
		IWidget widget = this.getWidgetAtCoordinates(event.x, event.y);
		if (widget != null && widget != this)
			widget.onMouseWheel(event);		
	}

	@Override
	public void onMouseEnter(MouseEvent event) {
		//System.out.printf("%s %s\n", this, event);
		/*
		IWidget widget = this.getWidgetAtCoordinates(event.x, event.y);
		if (widget != null && widget != this)
			widget.onMouseEnter(event);
		*/	
	}
	
	@Override
	public void onMouseLeave(MouseEvent event) {
		//System.out.printf("%s %s\n", this, event);
		/*
		IWidget widget = this.getWidgetAtCoordinates(event.x, event.y);
		if (widget != null && widget != this)
			widget.onMouseLeave(event);
		*/	
	}	
}
