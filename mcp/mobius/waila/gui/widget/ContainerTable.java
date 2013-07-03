package mcp.mobius.waila.gui.widget;

import java.util.ArrayList;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSlot;
import net.minecraft.client.renderer.Tessellator;

public class ContainerTable extends BaseWidget{
	int rowHeight;
	int ncolumns;
	//int viewportYPos  = 0;
	int tableHeight   = 0;
	int tableWidth    = 0;
	int scrollWidth        = 8;
	int scrollButtonHeight = 16;
	boolean isScrollActive = false;
	FontRenderer fontRender;
	int[]  columnPos;
	int[]  columnWidth;
	ArrayList<IWidget[]> table  = new ArrayList<IWidget[]>();
	VerticalScrollBar scrollbar = new VerticalScrollBar();
	Label[] columnnames;
	boolean autosize = true;
	
    public ContainerTable(GuiScreen parent){
    	this.parent = parent;
    	this.addWidget("VScrollBar", this.scrollbar);
    };
    
	public ContainerTable( GuiScreen parent, int height, int ncolumns, String[] columnnames) {
		this.parent = parent;
        this.height  = height;
        this.rowHeight = 8;
        this.fontRender = Minecraft.getMinecraft().fontRenderer;
        this.ncolumns = ncolumns;
        this.columnPos   = new int[this.ncolumns];
        this.columnWidth = new int[this.ncolumns];
        this.columnnames = new Label[this.ncolumns];
        
        for (int i = 0; i < this.ncolumns; i++){
        	Label labelHeader = new Label(columnnames[i]);
        	this.columnnames[i] = labelHeader;
        	this.addWidget(String.format("header_%s", i), labelHeader);
        }
    	this.addWidget("VScrollBar", this.scrollbar);        
	}

	public void setAutosize(boolean value){
		this.autosize = value;
	}
	
	public void setColumns(int rowHeight, String... header){
		this.rowHeight   = rowHeight;
		this.ncolumns    = header.length;
        this.columnPos   = new int[this.ncolumns];
        this.columnWidth = new int[this.ncolumns];
        this.columnnames = new Label[this.ncolumns];
        
        for (int i = 0; i < this.ncolumns; i++){
        	Label labelHeader = new Label(header[i]);
        	this.columnnames[i] = labelHeader;
        	this.addWidget(String.format("header_%s", i), labelHeader);
        	
			this.columnWidth[i] = Math.max(labelHeader.getWidth() + 8, this.columnWidth[i]);
			this.columnWidth[i] = Math.max(this.columnnames[i].getWidth() + 8, this.columnWidth[i]);	        	
        }
        this.columnPos[0] = 0;
        for (int i = 1; i < this.ncolumns; i++)
        	this.columnPos[i] = this.columnPos[i-1] + this.columnWidth[i-1];        
	}
	
	public int getSize() {
		return this.table.size();
	}

	public void addRow(IWidget... data){
		this.table.add(data);
		for (int i = 0; i < data.length; i++)
			this.addWidget(String.format("row_%s_%s", this.table.size(), i), data[i]);
		
		for (int i = 0; i < ncolumns; i++){
			this.columnWidth[i] = Math.max(data[i].getWidth() + 8, this.columnWidth[i]);
			this.columnWidth[i] = Math.max(this.columnnames[i].getWidth() + 8, this.columnWidth[i]);			
		}
		
        this.columnPos[0] = 0;
        for (int i = 1; i < this.ncolumns; i++)
        	this.columnPos[i] = this.columnPos[i-1] + this.columnWidth[i-1];	
        
		this.isScrollActive = (this.getViewportHeight() < this.getTableHeight());
	}

	@Override
	public void draw() {
		GL11.glPushMatrix();
		GL11.glTranslatef(1.0f, 1.0f, this.posZ);
		this.drawBackground();
		this.drawTitle(this.posX, this.posY);
		
		GL11.glPushMatrix();
		GL11.glTranslatef(0, this.scrollbar.getCurrentValue() * -1.0F, 0);
		this.startScissorFilter(this.posX, this.posY + this.rowHeight, this.getWidth(), this.getViewportHeight());
		
    	for (int i = 0; i < this.table.size(); i++)
    		this.drawRow(i, this.posX, this.posY+(i+1)*this.rowHeight);
    	
    	this.stopScissorFilter();
		GL11.glPopMatrix();
		
		if (this.isScrollActive){
	        this.updateScrollBar();
			this.scrollbar.draw();
		}
		GL11.glPopMatrix();		
	}	

	public void updateScrollBar(){
		int scrollbarX = this.posX + this.getWidth() - this.scrollWidth;
		int scrollbarY = this.posY + this.rowHeight;
		int scrollbarW = this.scrollWidth;
		int scrollbarH = this.getViewportHeight();
		int maxvalue   = this.getTableHeight() - this.getViewportHeight();
		
		this.scrollbar.setup(this.parent, scrollbarX, scrollbarY, scrollbarW, scrollbarH, this.scrollButtonHeight, maxvalue);	
	}
	
	@Override
	public int getWidth(){
		if (!autosize)
			return this.width;
		
		int width = 0;
		for (int i: this.columnWidth)
			width += i;
		
		if (this.isScrollActive)
			width += this.scrollWidth;
		return width;
	}

	public int getTableHeight(){
		return this.rowHeight * this.getSize();
	}	

	public int getViewportHeight(){
		return this.getHeight() - this.rowHeight;
	}
	
	@Override
	public int getHeight(){
		return this.height;
	}	
	
	@Override
	public void setHeight(int height){
		this.height = height;
		this.isScrollActive = (this.getViewportHeight() < this.getTableHeight());
	}
	
	public void drawTitle(int rowleft, int rowtop)
	{
		for (int j = 0; j < this.ncolumns; j++){
			this.columnnames[j].setWidth(this.columnWidth[j]);
			this.columnnames[j].draw(rowleft + this.columnPos[j], rowtop, 0);
		}		
	}
	
	public void drawRow(int index, int rowleft, int rowtop)
	{
		if (index%2 == 0)
			this.parent.drawGradientRect(rowleft, rowtop, rowleft+this.getWidth(), rowtop+this.rowHeight, 0xff333333, 0xff333333);
		else
			this.parent.drawGradientRect(rowleft, rowtop, rowleft+this.getWidth(), rowtop+this.rowHeight, 0xff000000, 0xff000000);			
		
		IWidget[] data = this.table.get(index);
		for (int j = 0; j < this.ncolumns; j++){
			data[j].setWidth(this.columnWidth[j]);
			data[j].draw(rowleft + this.columnPos[j], rowtop, 0);
		}
			
	}
	
	@Override
	public boolean mouseWheel(int mouseX, int mouseY, int mouseZ){
		this.scrollbar.addCurrentValue((mouseZ / -120) * this.scrollbar.getStep());
		return true;
	}
}
