package mcp.mobius.waila.overlay;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.awt.Dimension;
import java.awt.Point;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.Configuration;
import mcp.mobius.waila.Constants;
import mcp.mobius.waila.api.impl.ConfigHandler;
import static codechicken.core.gui.GuiDraw.*;
import static mcp.mobius.waila.SpecialChars.*;

public class Tooltip {
	List<String> textData = new ArrayList<String>();
	ArrayList<Line> lines = new ArrayList<Line>();
	int w,h,x,y,ty;
	int offsetX;
	int maxStringW;
	Point pos;
	boolean hasIcon = false;
	ItemStack stack;

	private class Line{
		ArrayList<String> elems = new ArrayList<String>();
		int lineWidth = 0;
		int columns   = 1;
		
		
		public Line(String text){
			
			// We split the original line around Waila special chars
			String[] substrings = text.split(WailaStyleEnd);
			for (int i = 0; i < substrings.length - 1; i++){
				if (substrings[i].startsWith(WailaStyle))
					elems.add(substrings[i]);
				else{
					elems.add(substrings[i].substring(0, substrings[i].length() - 2));
					elems.add(substrings[i].substring(substrings[i].length() - 2) + WailaStyleEnd);
					
					if (elems.get(elems.size() - 1).equals(TAB))
						columns += 1;
				}
			}
			elems.add(substrings[substrings.length - 1]);

			// We compute the size of the final rendered string (considering each special char to be 8px large)
			for (String s: this.elems){
				if (!s.startsWith(WailaStyle))
					lineWidth += getStringWidth(s);
				else{
					lineWidth += 8;
				}
			}
		}
	}
	
	public Tooltip(List<String> textData, ItemStack stack){
		this(textData, true);
		this.stack = stack;
	}
	
	public Tooltip(List<String> textData, boolean hasIcon){
		for (String s : textData)
			lines.add(new Line(s));
		
		this.textData = textData;
		this.pos      = new Point(ConfigHandler.instance().getConfig(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_POSX,0), 
				                  ConfigHandler.instance().getConfig(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_POSY,0));
		this.hasIcon  = hasIcon;
		
		int paddingW = hasIcon ? 29 : 13;
		int paddingH = hasIcon ? 24 : 0; 
		offsetX      = hasIcon ? 24 : 6;
		
		
		maxStringW = 0;
        for (Line s : lines)
        	maxStringW = Math.max(maxStringW, s.lineWidth);
        w = maxStringW + paddingW;
        
        h = Math.max(paddingH, 10 + 10*textData.size());

        Dimension size = displaySize();
        x = ((int)(size.width / OverlayConfig.scale)-w-1)*pos.x/10000;
        y = ((int)(size.height / OverlayConfig.scale)-h-1)*pos.y/10000;	
        
        ty = (h-10*textData.size())/2;        
	}
	
	public void drawStrings(){
        for (int i = 0; i < textData.size(); i++){
    		drawString(textData.get(i), x + offsetX, y + ty + 10*i, OverlayConfig.fontcolor, true);
        }
	}
}
