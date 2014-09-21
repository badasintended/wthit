package mcp.mobius.waila.gui_old.widget;

import java.util.Collection;

public interface IWidget {
	int    getWidth();
	int    getHeight();
	String getLabel();
	int    getPosX();
	int    getPosY();
	
	void setWidth(int width);
	void setHeight(int height);
	void setLabel(String label);
    void setPos(int posX, int posY, int posZ);
	
	boolean mouseClicked(int mouseX, int mouseY, int buttonID);
    boolean mouseReleased(int mouseX, int mouseY, int buttonID);
	boolean mouseWheel(int mouseX, int mouseY, int mouseZ);
    boolean mouseMoved(int mouseX, int mouseY);
    //boolean mouseDragged(int mouseX, int mouseY, int buttonID, long deltaTime);
    
	void draw();
	void draw(int x, int y, int z);
	void draw(float scale);
	
	IWidget getWidgetAtCoordinates(int posX, int posY);
	public void addWidget(String name, IWidget widget);
	public Collection<IWidget> getWidgets();
	public IWidget getWidget(String name);	
	
	void drawBackground();
	void setBackgroundColor(int color);
}
