package mcp.mobius.waila.gui_old.widget;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.GuiScreen;

public class DraggableViewport extends BaseWidget {

	//public StackDisplay  widStack = null;;
	private int offsetX = 0;
	private int offsetY = 0;
	private int prevMouseX = 0;
	private int prevMouseY = 0;
	private float scale = 1.0f;
	private boolean dragging = false;
	
	
	public DraggableViewport(GuiScreen parent){
    	this.parent = parent;
    	/*
    	widStack = new StackDisplay(parent);
    	widStack.setStack(new ItemStack(Item.axeDiamond));
    	widStack.setPos(80, 80, 0);
    	
		this.addWidget("stackdisplay",    this.widStack);
		*/    	
	}

	public void addWidget(IWidget widget, int posX, int posY, int posZ){
		this.addWidget(String.format("widget_%s", this.widgets.size()), widget);
		widget.setPos(posX, posY, posZ);
	}
	
	@Override
	public int getWidth() {
		return this.width;
	}

	@Override
	public int getHeight() {
		return this.height;
	}

	@Override
	public void draw() {
		this.drawBackground();
		
		this.drawBackground();
		GL11.glPushMatrix();
		GL11.glTranslatef(offsetX, offsetY, 0);
		GL11.glScalef(this.scale, this.scale, 1.0f);
		this.startScissorFilter(this.posX, this.posY, this.getWidth(), this.getHeight());

		for (IWidget widget : this.getWidgets())
			widget.drawBackground();		
		
		for (IWidget widget : this.getWidgets())
			//widget.draw(this.scale);
			widget.draw();

	
    	this.stopScissorFilter();
		GL11.glPopMatrix();		
	}

	@Override
	public boolean mouseClicked(int mouseX, int mouseY, int buttonID){
		if (buttonID == 0){
			this.dragging = true;
			this.prevMouseX = mouseX;
			this.prevMouseY = mouseY;
			return true;
		}
		else
			return false;
	}	
	
	@Override
	public boolean mouseReleased(int mouseX, int mouseY, int buttonID){
		if (buttonID == 0)
			this.dragging = false;
		else
			return false;
		return true;
	}
	

	@Override 
	public boolean mouseMoved(int mouseX, int mouseY){
		if (this.dragging){
			this.offsetX -= (this.prevMouseX - mouseX);
			this.offsetY -= (this.prevMouseY - mouseY);			
			this.prevMouseX = mouseX;
			this.prevMouseY = mouseY;
			
			return true;
		}
		return false;
	}	

	@Override
	public boolean mouseWheel(int mouseX, int mouseY, int mouseZ){
		if (mouseZ > 0)
			this.scale *= Math.abs((mouseZ / -120)*2);
		else
			this.scale /= Math.abs((mouseZ / -120)*2);
		return true;
	}	
	
}
