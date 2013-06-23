package mcp.mobius.waila.gui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.lwjgl.input.Mouse;

import mcp.mobius.waila.gui.widget.Button2States;
import mcp.mobius.waila.gui.widget.ButtonChangeScreen;
import mcp.mobius.waila.gui.widget.IWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;

public class GuiBaseWailaScreen extends GuiScreen {
 
    protected GuiScreen parentGui;	// GUI we will return to in the case we are call from a GUI
	public    ScaledResolution res;
    protected HashMap<String, IWidget> widgets = new HashMap<String, IWidget>();
	
    private int  lastMouseButton = -1;
    private long lastMouseEvent = -1;
    
	public GuiBaseWailaScreen(GuiScreen _parentGui) {
		this.parentGui = _parentGui;
        res = new ScaledResolution(Minecraft.getMinecraft().gameSettings, 
        		                   Minecraft.getMinecraft().displayWidth, 
        		                   Minecraft.getMinecraft().displayHeight);	
        Mouse.getDWheel();
	}

   @Override
   /* Here we draw anything special, and super takes care of the buttons for us */
   public void drawScreen(int i, int j, float f)
   {
       drawDefaultBackground();        
       super.drawScreen(i, j, f);
   }   

   @Override
   /* Here we handle key presses. Super will just close the gui on ESC, and we like it like that */
   public void keyTyped(char keyChar, int keyID)
   {
       if (keyID == 1)
       {
    	   if (this.parentGui == null){
    		   this.mc.displayGuiScreen((GuiScreen)null);
    		   this.mc.setIngameFocus();
    	   } else {
    		   this.mc.displayGuiScreen(this.parentGui);
    	   }
       }	   
   }   
   
   @Override
   protected void actionPerformed(GuiButton guibutton)
   {
	   if (guibutton instanceof ButtonChangeScreen)
		   ((ButtonChangeScreen)guibutton).pressButton();
	   
	   else if (guibutton instanceof Button2States)
		   ((Button2States)guibutton).pressButton();	   
   }

   protected int getXCentered(int width){
	   return (this.res.getScaledWidth()  - width)/ 2;
   }
   
   public void addWidget(String name, IWidget widget){
	   this.widgets.put(name, widget);
   }
   
   public Collection<IWidget> getWidgets(){
	   return this.widgets.values();
   }
   
   public IWidget getWidget(String name){
	   return this.widgets.get(name);
   }
   
   @Override
   protected void mouseClicked(int mouseX, int mouseY, int buttonID)
   {
	   IWidget widget = this.getWidgetAtCoordinates(mouseX, mouseY);
	   if (widget == null || !widget.mouseClicked(mouseX, mouseY, buttonID))
		   super.mouseClicked(mouseX, mouseY, buttonID);
   }   
   
   @Override
   protected void mouseMovedOrUp(int mouseX, int mouseY, int buttonID)
   {
	   IWidget widget = this.getWidgetAtCoordinates(mouseX, mouseY);
	   if (widget == null || !widget.mouseMovedOrUp(mouseX, mouseY, buttonID))
		   super.mouseMovedOrUp(mouseX, mouseY, buttonID);   
   }   

   protected void mouseWheel(int mouseX, int mouseY, int mouseZ){
	   IWidget widget = this.getWidgetAtCoordinates(mouseX, mouseY);
	   if (widget != null)
		   widget.mouseWheel(mouseX, mouseY, mouseZ);
   
   }
   
   protected void mouseMoved(int mouseX, int mouseY){
	   IWidget widget = this.getWidgetAtCoordinates(mouseX, mouseY);
	   if (widget != null)
		   widget.mouseMoved(mouseX, mouseY);	   
   }   
   
   @Override
   public void handleMouseInput()
   {
       int mouseX = Mouse.getEventX() * this.width / this.mc.displayWidth;
       int mouseY = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;
       int mouseZ = Mouse.getEventDWheel();
       
       if (mouseZ != 0)
    	   this.mouseWheel(mouseX, mouseY, mouseZ);
       
       if (Mouse.getEventButtonState())
       {
           this.lastMouseButton = Mouse.getEventButton();
           this.lastMouseEvent  = Minecraft.getSystemTime();
           this.mouseClicked(mouseX, mouseY, this.lastMouseButton);
       }
       else if (Mouse.getEventButton() != -1)
       {
           this.lastMouseButton = -1;
           this.mouseMovedOrUp(mouseX, mouseY, Mouse.getEventButton());
       }
       else if (this.lastMouseButton != -1 && this.lastMouseEvent > 0L)
       {
           long deltaTime = Minecraft.getSystemTime() - this.lastMouseEvent;
           this.func_85041_a(mouseX, mouseY, this.lastMouseButton, deltaTime);
       }
       else if (Mouse.getDX() != 0 || Mouse.getDY() != 0)
    	   this.mouseMoved(mouseX, mouseY);
   }   
 
   private IWidget getWidgetAtCoordinates(int posX, int posY){
	   for (IWidget widget : this.getWidgets())
		   if ((posX >=  widget.getPosX()) && 
		       (posX <=  widget.getPosX() + widget.getWidth()) &&
		       (posY >=  widget.getPosY()) &&
		       (posY <=  widget.getPosY() + widget.getHeight()))
		      return widget;
	   return null;
   }
}