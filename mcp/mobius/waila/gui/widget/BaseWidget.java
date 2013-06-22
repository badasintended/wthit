package mcp.mobius.waila.gui.widget;

public abstract class BaseWidget implements IWidget {

	protected int width, height;
	protected String label;	
	
	@Override
	public int getWidth()  { return this.width;}	
	@Override
	public int getHeight() { return this.height; }
	@Override
	public String getLabel(){ return this.label;}

	@Override
	public void setWidth(int width)   { this.width = width; }
	@Override
	public void setHeight(int height) { this.height = height; }
	@Override
	public void setLabel(String label){ this.label = label; }
	
	@Override
	public abstract void draw(int x, int y, int z);

}
