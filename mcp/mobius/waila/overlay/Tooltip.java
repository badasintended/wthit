package mcp.mobius.waila.overlay;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.awt.Dimension;
import java.awt.Point;

import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraftforge.common.Configuration;
import mcp.mobius.waila.Constants;
import mcp.mobius.waila.api.impl.ConfigHandler;
import static codechicken.core.gui.GuiDraw.*;
import static mcp.mobius.waila.SpecialChars.*;

public class Tooltip {
	public static int TabSpacing = 8;
	
	ArrayList<Line> lines = new ArrayList<Line>();
	int w,h,x,y,ty;
	int offsetX;
	int maxStringW;
	Point pos;
	boolean hasIcon = false;
	ItemStack stack;
	
	int[] columnsWidth;
	int[] columnsPos;
	int   ncolumns = 0;
	
	private class Line{
		String[] columns;
		int      ncolumns;
		int[]    columnsWidth;
		int      lineWidth;
		
		public Line(String text){
			columns      = text.split(TAB);
			ncolumns     = columns.length;
			columnsWidth = new int[ncolumns];
			lineWidth    = 0;			
			
			for (int i = 0; i < ncolumns; i++){
				columnsWidth[i] = getStringWidth(columns[i]);
				lineWidth      += columnsWidth[i];
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
		
		for (Line l : lines)
			ncolumns = Math.max(ncolumns, l.ncolumns);
		
		columnsWidth = new int[ncolumns];
		columnsPos   = new int[ncolumns];

		for (Line l : lines){
			if (l.ncolumns > 1)
				for (int i = 0; i < l.ncolumns; i++)
					columnsWidth[i] = Math.max(columnsWidth[i], l.columnsWidth[i]);
					
			maxStringW = Math.max(maxStringW, l.lineWidth + (l.ncolumns - 1) * TabSpacing);
		}
		
		for (int i = 0; i < ncolumns - 1; i++)
			columnsPos[i + 1] = columnsWidth[i] + columnsPos[i];
		
		this.pos      = new Point(ConfigHandler.instance().getConfig(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_POSX,0), 
				                  ConfigHandler.instance().getConfig(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_POSY,0));
		this.hasIcon  = hasIcon;
		
		int paddingW = hasIcon ? 29 : 13;
		int paddingH = hasIcon ? 24 : 0; 
		offsetX      = hasIcon ? 24 : 6;
		
        w = maxStringW + paddingW;
        
        h = Math.max(paddingH, 10 + 10*lines.size());

        Dimension size = displaySize();
        x = ((int)(size.width  / OverlayConfig.scale)-w-1)*pos.x/10000;
        y = ((int)(size.height / OverlayConfig.scale)-h-1)*pos.y/10000;	
        
        ty = (h-10*lines.size())/2;        
	}
	
	public void drawStrings(){
		for (int i = 0; i < lines.size(); i++){
			for (int c = 0; c < lines.get(i).ncolumns; c++)
				drawString(lines.get(i).columns[c], x + offsetX + columnsPos[c] + c*TabSpacing, y + ty + 10*i, OverlayConfig.fontcolor, true);
		}
		
		//OverlayRenderer.renderIcon(0, 0, 16, 16, IconUI.HEART);
	}
}
