package mcp.mobius.waila.overlay;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.awt.Dimension;
import java.awt.Point;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.Configuration;
import mcp.mobius.waila.api.impl.ConfigHandler;
import mcp.mobius.waila.utils.Constants;
import static codechicken.core.gui.GuiDraw.*;
import static mcp.mobius.waila.utils.SpecialChars.*;

public class Tooltip {
	public static int TabSpacing = 8;
	public static int IconSize   = 8; 
	
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
		String[] columnsRaw;
		ArrayList<ArrayList<String>> columns = new ArrayList<ArrayList<String>>(); 
		int      ncolumns;
		int[]    columnsWidth;
		int      lineWidth;
		
		public Line(String text){
			columnsRaw   = text.split(TAB);
			ncolumns     = columnsRaw.length;
			columnsWidth = new int[ncolumns];
			lineWidth    = 0;			
			
			for (String s : columnsRaw)
				columns.add(parseString(s));

			for (int i = 0; i < ncolumns; i++){
				for (String s : columns.get(i)){
					if (s.startsWith(WailaStyle + WailaIcon))
						columnsWidth[i] += IconSize;
					else if (s.startsWith(WailaStyle + WailaStyle))
						columnsWidth[i] += 0;
					else
						columnsWidth[i] += getStringWidth(s);
				}
				lineWidth      += columnsWidth[i];
			}
		}
		
		ArrayList<String> parseString(String s){
			ArrayList<String> retList = new ArrayList<String>();
			Pattern pattern = Pattern.compile(WailaStyle + "..");
			Matcher matcher = pattern.matcher(s);
			
			int prevIndex = 0;
			while (matcher.find()){
				String substring = s.substring(prevIndex, matcher.start());
				if (!substring.equals(""))
					retList.add(substring);
				retList.add(s.substring(matcher.start(), matcher.end()));
				prevIndex = matcher.end();
			}
			retList.add(s.substring(prevIndex));
			return retList;
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
		
		columnsWidth    = new int[ncolumns];
		columnsPos      = new int[ncolumns];

		for (Line l : lines){
			for (int i = 0; i < l.ncolumns; i++){
				if (l.ncolumns > 1 && ncolumns != 1)
					columnsWidth[i] = Math.max(columnsWidth[i], l.columnsWidth[i]);
				else if (ncolumns == 1)
					columnsWidth[i] = Math.max(columnsWidth[i], l.columnsWidth[i]);
			}
		}

		maxStringW = 0;
		for (int i = 0; i < ncolumns; i++)
			maxStringW += columnsWidth[i];
		maxStringW += ncolumns * TabSpacing;

		
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
			for (int c = 0; c < lines.get(i).ncolumns; c++){
				int offX = 0;				

				for (int is = 0; is < lines.get(i).columns.get(c).size(); is++){
					String s = lines.get(i).columns.get(c).get(is);
					if (s.startsWith(WailaStyle + WailaIcon))
						offX += IconSize;
					
					else if (s.startsWith(ALIGNRIGHT))
						offX += columnsWidth[c] - getStringWidth(lines.get(i).columns.get(c).get(is + 1));
					
					else{
						drawString(s, x + offsetX + columnsPos[c] + c*TabSpacing + offX, y + ty + 10*i, OverlayConfig.fontcolor, true);
						offX += getStringWidth(s);
					}
				}				
				
			}
		}
	}
	
	public void drawIcons(){
		for (int i = 0; i < lines.size(); i++){
			for (int c = 0; c < lines.get(i).ncolumns; c++){
				int offX = 0;				
				for (String s : lines.get(i).columns.get(c)){
					if (s.startsWith(WailaStyle + WailaIcon)){
						OverlayRenderer.renderIcon(x + offsetX + columnsPos[c] + c*TabSpacing + offX, y + ty + 10*i, IconSize, IconSize, IconUI.bySymbol(s));
						offX += IconSize;
					}else{
						offX += getStringWidth(s);
					}
				}
			}
		}
	}	
}
