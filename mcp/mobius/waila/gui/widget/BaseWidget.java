package mcp.mobius.waila.gui.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.RenderEngine;

public abstract class BaseWidget implements IWidget {

	protected int width, height;
	protected String label;	
	protected int posX, posY, posZ;
	protected GuiScreen parent;
	protected FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;;
	protected RenderEngine renderEngine = Minecraft.getMinecraft().renderEngine;
	
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
		this.draw();
	}

}
