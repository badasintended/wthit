package mcp.mobius.waila.gui.widget;

import java.util.Collection;
import java.util.HashMap;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.RenderEngine;
import net.minecraft.client.renderer.Tessellator;

public abstract class BaseWidget implements IWidget {

	protected int width, height;
	protected String label;	
	protected int posX, posY, posZ;
	protected GuiScreen parent;
	protected FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;;
	protected RenderEngine renderEngine = Minecraft.getMinecraft().renderEngine;
    protected HashMap<String, IWidget> widgets = new HashMap<String, IWidget>();
	protected IWidget[] focusWidget = new IWidget[Mouse.getButtonCount()]; 
    protected IWidget   mainFocusWidget = null;
	protected int backgroundColor = 0;
    
	public BaseWidget(){}
	
	@Override
	public abstract int getWidth();	
	@Override
	public abstract int getHeight();
	@Override
	public void setWidth(int width)   { this.width = width; }
	@Override
	public void setHeight(int height) { this.height = height; }

	@Override
	public void setPos (int posX, int posY, int posZ){
		this.posX = posX;
		this.posY = posY;
		this.posZ = posZ;
	}	
	@Override
	public int getPosX(){return this.posX;}
	@Override
	public int getPosY(){return this.posY;}	
	
	@Override
	public String getLabel(){ return this.label;}
	@Override
	public void setLabel(String label){ this.label = label; }

	@Override
	public abstract void draw();
	@Override
	public void draw(int x, int y, int z){
		this.posX = x;
		this.posY = y;
		this.posZ = z;
		GL11.glPushMatrix();
		GL11.glTranslatef(0.0f,0.0f, this.posZ);
		this.draw();
		GL11.glPopMatrix();
	}
	@Override 
	public void draw(float scale){
		int oldX = this.posX;
		int oldY = this.posY;
		this.posX /= scale;
		this.posY /= scale;
		GL11.glPushMatrix();
		GL11.glTranslatef(0.0f,0.0f, this.posZ);		
		this.draw();
		GL11.glPopMatrix();		
		this.posX = oldX;
		this.posY = oldY;
	}
	
	
	@Override
	public boolean mouseClicked(int mouseX, int mouseY, int buttonID){
		IWidget widget = this.getWidgetAtCoordinates(mouseX, mouseY);
		if (widget != null){
			if (buttonID == 0)
				this.mainFocusWidget = widget;
			this.focusWidget[buttonID] = widget;
			return widget.mouseClicked(mouseX, mouseY, buttonID);
		} else
			return false;
	}
	@Override	
	public boolean mouseReleased(int mouseX, int mouseY, int buttonID){
		if (buttonID == 0)
			this.mainFocusWidget = null;
		
		if (this.focusWidget[buttonID] != null){
			boolean retval = this.focusWidget[buttonID].mouseReleased(mouseX, mouseY, buttonID);
			this.focusWidget[buttonID] = null;
			return retval;
		}
		this.focusWidget[buttonID] = null;		
		return false;
	}
	@Override
	public boolean mouseWheel(int mouseX, int mouseY, int mouseZ){
		IWidget widget = this.getWidgetAtCoordinates(mouseX, mouseY);
		if (widget != null)
			return widget.mouseWheel(mouseX, mouseY, mouseZ);
		return false;		
	}
	@Override		
    public boolean mouseMoved(int mouseX, int mouseY){
		if (this.mainFocusWidget != null)
			return this.mainFocusWidget.mouseMoved(mouseX, mouseY);
			
		IWidget widget = this.getWidgetAtCoordinates(mouseX, mouseY);
		if (widget != null)
			return widget.mouseMoved(mouseX, mouseY);
		return false;		
	}	
	
	/*
    @Override	
    public boolean mouseDragged(int mouseX, int mouseY, int buttonID, long deltaTime){
    	IWidget widget = this.getWidgetAtCoordinates(mouseX, mouseY);
    	if (widget != null)
    		return widget.mouseDragged(mouseX, mouseY, buttonID, deltaTime);
		return false;    	
	}
	*/
	
	public void startScissorFilter(int posX, int posY, int width, int height){
		ScaledResolution res = new ScaledResolution(Minecraft.getMinecraft().gameSettings, 
                									Minecraft.getMinecraft().displayWidth, 
                									Minecraft.getMinecraft().displayHeight);			
		int scaleFactor = res.getScaleFactor();
		
		int glLeft   =  posX * scaleFactor;
		int glBottom =  (res.getScaledHeight() - (posY + height)) * scaleFactor;
		
		GL11.glScissor(posX*scaleFactor, glBottom, width*scaleFactor, height*scaleFactor);
		GL11.glEnable(GL11.GL_SCISSOR_TEST);		
	}
	
	public void stopScissorFilter(){
		GL11.glDisable(GL11.GL_SCISSOR_TEST);
	}

	@Override		
    public IWidget getWidgetAtCoordinates(int posX, int posY){
	    for (IWidget widget : this.getWidgets())
	 	   if ((posX >=  widget.getPosX()) && 
	 	       (posX <=  widget.getPosX() + widget.getWidth()) &&
		       (posY >=  widget.getPosY()) &&
		       (posY <=  widget.getPosY() + widget.getHeight()))
		      return widget;
	   return null;
   }
	@Override		
   public void addWidget(String name, IWidget widget){
	   this.widgets.put(name, widget);
   }
	@Override		
   public Collection<IWidget> getWidgets(){
	   return this.widgets.values();
   }
	@Override		
   public IWidget getWidget(String name){
	   return this.widgets.get(name);
   } 
	
   @Override 
   public void setBackgroundColor(int color){
	   this.backgroundColor = color;
   }
	
   @Override
   public void drawBackground(){
	   if (this.backgroundColor != 0)
		   this.drawGradientRect(this.posX, this.posY, this.posX+this.getWidth(), this.posY+this.getHeight(), 0.0, this.backgroundColor, this.backgroundColor);	   
   }
   
   public void drawGradientRect(int left, int top, int right, int bottom, double zlevel, int colorStart, int colorStop)
   {
       float f = (colorStart >> 24 & 255) / 255.0F;
       float f1 = (colorStart >> 16 & 255) / 255.0F;
       float f2 = (colorStart >> 8 & 255) / 255.0F;
       float f3 = (colorStart & 255) / 255.0F;
       float f4 = (colorStop >> 24 & 255) / 255.0F;
       float f5 = (colorStop >> 16 & 255) / 255.0F;
       float f6 = (colorStop >> 8 & 255) / 255.0F;
       float f7 = (colorStop & 255) / 255.0F;
       GL11.glDisable(GL11.GL_TEXTURE_2D);
       GL11.glEnable(GL11.GL_BLEND);
       GL11.glDisable(GL11.GL_ALPHA_TEST);
       GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
       GL11.glShadeModel(GL11.GL_SMOOTH);
       Tessellator tessellator = Tessellator.instance;
       tessellator.startDrawingQuads();
       tessellator.setColorRGBA_F(f1, f2, f3, f);
       tessellator.addVertex(right, top, zlevel);
       tessellator.addVertex(left, top, zlevel);
       tessellator.setColorRGBA_F(f5, f6, f7, f4);
       tessellator.addVertex(left, bottom, zlevel);
       tessellator.addVertex(right, bottom, zlevel);
       tessellator.draw();
       GL11.glShadeModel(GL11.GL_FLAT);
       GL11.glDisable(GL11.GL_BLEND);
       GL11.glEnable(GL11.GL_ALPHA_TEST);
       GL11.glEnable(GL11.GL_TEXTURE_2D);
   }   
}
