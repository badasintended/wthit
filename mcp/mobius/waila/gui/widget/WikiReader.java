package mcp.mobius.waila.gui.widget;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.GuiScreen;

public class WikiReader extends BaseWidget {

	private VerticalScrollBar scrollbar = new VerticalScrollBar();
	private String text = "";
	int     textHeight         = 0;
	int     lineHeight         = 8;
	int     scrollWidth        = 8;
	int     scrollButtonHeight = 16;
	boolean isScrollActive = false;
	
	
	public WikiReader(GuiScreen parent){
    	this.parent = parent;
    	this.addWidget("VScrollBar", this.scrollbar);
	}

	@Override
	public int getWidth() {
		if (!this.isScrollActive)
			return this.width;
		else
			return this.width - this.scrollWidth;
	}

	@Override
	public int getHeight() {
		return this.height;
	}

	@Override
	public void draw() {
		GL11.glPushMatrix();
		GL11.glTranslatef(0, this.scrollbar.getCurrentValue() * -1.0F, 0);
		this.startScissorFilter(this.posX, this.posY, this.getWidth(), this.getViewportHeight());
		
		int offset = this.posY;

		for (String s : this.getTailoredText()){
			this.fontRenderer.drawString(s, this.posX, offset, 0xFFFFFFFF);
			offset += this.lineHeight;
		}
		
    	this.stopScissorFilter();
		GL11.glPopMatrix();
		
		if (this.isScrollActive){
	        this.updateScrollBar();
			this.scrollbar.draw();
		}		
	}

	public void setText(String text){
		this.text = text;
		this.textHeight = this.getTailoredText().size() * this.lineHeight;
		if (this.textHeight > this.getHeight()){
			this.isScrollActive = true;
			//this.setWidth(this.getWidth() - this.scrollWidth);
			this.textHeight = this.getTailoredText().size() * this.lineHeight;
		}
	}
	
	public int getTextHeight(){
		return this.getTailoredText().size() * this.lineHeight;
	}
	
	public String getText(){
		return this.text;
	}
	
	public ArrayList<String> getTailoredText(){
		String[] splittedText = this.text.split("\r?\n");
		ArrayList<String>  lines = new ArrayList<String>();
		for (String s : splittedText){		
			ArrayList<Integer> whiteSpaces = this.getWhiteSpaces(s);
			int lastCutIndex = 0;
			for (int i = 0; i < whiteSpaces.size(); i++){
				String substring = s.substring(lastCutIndex, whiteSpaces.get(i));
				if (this.fontRenderer.getStringWidth(substring) > this.getWidth()){
					lines.add(s.substring(lastCutIndex, whiteSpaces.get(i-1)));
					lastCutIndex = whiteSpaces.get(i-1) + 1;
				}
			}
			lines.add(s.substring(lastCutIndex));
		}
		return lines;
	}

	private ArrayList<Integer> getWhiteSpaces(String input){
		ArrayList<Integer> indexes = new ArrayList<Integer>();
		int    index = 0;
		while (index != -1){
			index = input.indexOf(" ", index + 1);
			if (index != -1)
				indexes.add(index);
		}		
		return indexes;
	}
	
	@Override
	public boolean mouseWheel(int mouseX, int mouseY, int mouseZ){
		this.scrollbar.addCurrentValue((mouseZ / -120) * this.scrollbar.getStep());
		return true;
	}
	
	public int getViewportHeight(){
		return this.getHeight();
	}	
	
	public void updateScrollBar(){
		int scrollbarX = this.posX + this.getWidth();
		int scrollbarY = this.posY;
		int scrollbarW = this.scrollWidth;
		int scrollbarH = this.getViewportHeight();
		int maxvalue   = this.getTextHeight() - this.getViewportHeight();
		
		this.scrollbar.setup(this.parent, scrollbarX, scrollbarY, scrollbarW, scrollbarH, this.scrollButtonHeight, maxvalue);	
	}	
}
