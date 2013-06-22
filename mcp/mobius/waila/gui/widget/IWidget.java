package mcp.mobius.waila.gui.widget;

public interface IWidget {
	int  getWidth();
	int  getHeight();
	String getLabel();

	void setWidth(int width);
	void setHeight(int height);
	void setLabel(String label);

	void draw(int x, int y, int z);
}
