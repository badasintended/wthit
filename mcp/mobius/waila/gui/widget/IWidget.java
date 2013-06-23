package mcp.mobius.waila.gui.widget;

public interface IWidget {
	int  getWidth();
	int  getHeight();
	String getLabel();
	int  getPosX();
	int  getPosY();
	
	void setWidth(int width);
	void setHeight(int height);
	void setLabel(String label);
    void setPos(int posX, int posY, int posZ);
	
	boolean mouseClicked(int mouseX, int mouseY, int buttonID);
    boolean mouseWheel(int mouseX, int mouseY, int mouseZ);
    boolean mouseMovedOrUp(int mouseX, int mouseY, int buttonID);
    boolean mouseMoved(int mouseX, int mouseY);
    
	void draw();
	void draw(int x, int y, int z);
}
