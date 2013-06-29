package mcp.mobius.waila.gui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.lwjgl.input.Mouse;

import mcp.mobius.waila.gui.widget.IWidget;
import mcp.mobius.waila.gui.widget_old.Button2States;
import mcp.mobius.waila.gui.widget_old.ButtonChangeScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;

public class GuiBaseWailaScreen extends GuiScreen{
 
    protected GuiScreen parentGui;	// GUI we will return to in the case we are call from a GUI
	public    ScaledResolution res;
    protected HashMap<String, IWidget> widgets = new HashMap<String, IWidget>();

    /* Custom mouse handling */
    private int  lastMouseButton = -1;
    private long lastMouseEvent = -1;
    private static int buttonCount  = Mouse.getButtonCount();
    private boolean[]  buttonStates = new boolean[buttonCount];
    
    
    
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
   public void mouseClicked(int mouseX, int mouseY, int buttonID)
   {
	   IWidget widget = this.getWidgetAtCoordinates(mouseX, mouseY);
	   if (widget == null || !widget.mouseClicked(mouseX, mouseY, buttonID))
		   super.mouseClicked(mouseX, mouseY, buttonID);
   }   
   
   @Override
   public void mouseMovedOrUp(int mouseX, int mouseY, int buttonID)
   {
	   IWidget widget = this.getWidgetAtCoordinates(mouseX, mouseY);
	   if (widget == null || !widget.mouseMovedOrUp(mouseX, mouseY, buttonID))
		   super.mouseMovedOrUp(mouseX, mouseY, buttonID);   
   }   

   public void mouseWheel(int mouseX, int mouseY, int mouseZ){
	   IWidget widget = this.getWidgetAtCoordinates(mouseX, mouseY);
	   if (widget != null)
		   widget.mouseWheel(mouseX, mouseY, mouseZ);
   
   }
   
   public void mouseMoved(int mouseX, int mouseY){
	   IWidget widget = this.getWidgetAtCoordinates(mouseX, mouseY);
	   if (widget != null)
		   widget.mouseMoved(mouseX, mouseY);	   
   }   
   
   public void mouseDragged(int mouseX, int mouseY, int buttonID, long deltaTime){
	   //System.out.printf("%s %s %s %s %s %s\n", mouseX, mouseY, buttonID, deltaTime);
   }
   
   @Override
   public void handleMouseInput()
   {
       int mouseX = Mouse.getEventX() * this.width / this.mc.displayWidth;
       int mouseY = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;
       int mouseZ = Mouse.getEventDWheel();
       
       if (mouseZ != 0)
    	   this.mouseWheel(mouseX, mouseY, mouseZ);
       
       int     changedButton = -1;
       boolean hasStateChanged = false;
       if (Mouse.getEventButtonState()){
    	   hasStateChanged = true;
    	   changedButton   = Mouse.getEventButton();   
       }
       for (int i = 0; i < this.buttonStates.length; i++)
    	   this.buttonStates[i] = Mouse.isButtonDown(i);
       
       if (hasStateChanged)
       {
           this.lastMouseButton = changedButton;
           this.lastMouseEvent  = Minecraft.getSystemTime();
           this.mouseClicked(mouseX, mouseY, this.lastMouseButton);
       }
       else if (changedButton != -1)
       {
           this.lastMouseButton = -1;
           this.mouseMovedOrUp(mouseX, mouseY, changedButton);
       }
       else if (this.lastMouseButton != -1 && this.lastMouseEvent > 0L)
       {
           long deltaTime = Minecraft.getSystemTime() - this.lastMouseEvent;
           this.mouseDragged(mouseX, mouseY, this.lastMouseButton, deltaTime);
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