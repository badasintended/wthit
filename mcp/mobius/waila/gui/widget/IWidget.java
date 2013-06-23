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
	
	
	void draw();
	void draw(int x, int y, int z);
}
