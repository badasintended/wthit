package mcp.mobius.waila.gui_old;

import java.util.Collection;
import java.util.HashMap;

import org.lwjgl.input.Mouse;

import mcp.mobius.waila.gui_old.widget.IWidget;
import mcp.mobius.waila.gui_old.widget_old.Button2States;
import mcp.mobius.waila.gui_old.widget_old.ButtonChangeScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;

public class BaseWailaScreen extends GuiScreen{
 
    protected GuiScreen parentGui;	// GUI we will return to in the case we are call from a GUI
	public    ScaledResolution res;
    protected HashMap<String, IWidget> widgets = new HashMap<String, IWidget>();

    /* Custom mouse handling */
    private int  lastMouseButton = -1;
    private long lastMouseEvent = -1;
    private int  lastMousePosX  = -1;
    private int  lastMousePosY  = -1;
    private static int buttonCount  = Mouse.getButtonCount();
    private boolean[]  lastButtonStates = new boolean[buttonCount];
    private IWidget[] focusWidget = new IWidget[buttonCount]; 
    private IWidget   mainFocusWidget = null;
    
    
    
	public BaseWailaScreen(GuiScreen _parentGui) {
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
	   if (widget != null && widget.mouseClicked(mouseX, mouseY, buttonID)){
		   if (buttonID == 0)
			   this.mainFocusWidget = widget;		   
		   this.focusWidget[buttonID] = widget;
	   }
	   else
		   super.mouseClicked(mouseX, mouseY, buttonID);
   }   
   
   @Override
   public void mouseMovedOrUp(int mouseX, int mouseY, int buttonID)
   {
	   if (buttonID == 0)
		   this.mainFocusWidget = null;		   
	   
	   if (this.focusWidget[buttonID] != null){
		   this.focusWidget[buttonID].mouseReleased(mouseX, mouseY, buttonID);
		   this.focusWidget[buttonID] = null;
	   } else {
		   IWidget widget = this.getWidgetAtCoordinates(mouseX, mouseY);
		   if (widget == null || !widget.mouseReleased(mouseX, mouseY, buttonID))		   
			   super.mouseMovedOrUp(mouseX, mouseY, buttonID);
	   }
   }   

   public void mouseWheel(int mouseX, int mouseY, int mouseZ){
	   IWidget widget = this.getWidgetAtCoordinates(mouseX, mouseY);
	   if (widget != null)
		   widget.mouseWheel(mouseX, mouseY, mouseZ);
   
   }
   
   public void mouseMoved(int mouseX, int mouseY){
		if (this.mainFocusWidget != null)
			this.mainFocusWidget.mouseMoved(mouseX, mouseY);
		else{
		   IWidget widget = this.getWidgetAtCoordinates(mouseX, mouseY);
		   if (widget != null)
			   widget.mouseMoved(mouseX, mouseY);
		}
   }   
   
   /*
   public void mouseDragged(int mouseX, int mouseY, int buttonID, long deltaTime){
	   IWidget widget = this.getWidgetAtCoordinates(mouseX, mouseY);
	   if (widget != null)
		   widget.mouseDragged(mouseX, mouseY, buttonID, deltaTime);	   	   
   }
   */
   
   @Override
   public void handleMouseInput()
   {
       int mouseX = Mouse.getEventX() * this.width / this.mc.displayWidth;
       int mouseY = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;
       int mouseZ = Mouse.getEventDWheel();
	   boolean[]  buttonChanged  = new boolean[buttonCount];       
       
       if (mouseZ != 0)
    	   this.mouseWheel(mouseX, mouseY, mouseZ);
       
       int     changedButton = -1;
       boolean hasStateChanged = false;
       if (Mouse.getEventButtonState()){
    	   hasStateChanged = true;
    	   changedButton   = Mouse.getEventButton();   
       }
       for (int i = 0; i < buttonCount; i++){
    	   buttonChanged[i] = Mouse.isButtonDown(i) != this.lastButtonStates[i];
    	   this.lastButtonStates[i] = Mouse.isButtonDown(i);
    	   
	       if  (buttonChanged[i] && Mouse.isButtonDown(i))
	    	   this.mouseClicked(mouseX, mouseY, i);
	       else if  (buttonChanged[i] && !Mouse.isButtonDown(i))
	    	   this.mouseMovedOrUp(mouseX, mouseY, i);
       }

       if ((mouseX != this.lastMousePosX) || (mouseY != this.lastMousePosY)){
    	   this.mouseMoved(mouseX, mouseY);
    	   this.lastMousePosX = mouseX;
    	   this.lastMousePosY = mouseY;
       }       
       
       /*
       if (changedButton != -1){
	       if  (Mouse.isButtonDown(changedButton)){
	    	   this.mouseClicked(mouseX, mouseY, changedButton);
	    	   this.lastMouseButton = changedButton;
	       }
	       else if (!Mouse.isButtonDown(changedButton)){
	    	   this.mouseMovedOrUp(mouseX, mouseY, changedButton);
	    	   this.lastMouseButton = -1;    	   
	       }
       }
       
       if ((mouseX != this.lastMousePosX) || (mouseY != this.lastMousePosY)){
    	   this.mouseMoved(mouseX, mouseY);
    	   this.lastMousePosX = mouseX;
    	   this.lastMousePosY = mouseY;
       }
       */
       
       /*
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
       */
       /*
       else if (this.lastMouseButton != -1 && this.lastMouseEvent > 0L)
       {
           long deltaTime = Minecraft.getSystemTime() - this.lastMouseEvent;
           this.mouseDragged(mouseX, mouseY, this.lastMouseButton, deltaTime);
       }
       */
       /*
       else if ((mouseX != this.lastMousePosX) || (mouseY != this.lastMousePosY)){
    	   this.mouseMoved(mouseX, mouseY);
    	   this.lastMousePosX = mouseX;
    	   this.lastMousePosY = mouseY;
       }
       */
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