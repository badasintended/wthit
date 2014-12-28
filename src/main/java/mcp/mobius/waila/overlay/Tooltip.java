package mcp.mobius.waila.overlay;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.awt.Dimension;
import java.awt.Point;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaTooltipRenderer;
import mcp.mobius.waila.api.impl.ConfigHandler;
import mcp.mobius.waila.api.impl.DataAccessorBlock;
import mcp.mobius.waila.api.impl.ModuleRegistrar;
import mcp.mobius.waila.utils.Constants;
import static mcp.mobius.waila.api.SpecialChars.*;

public class Tooltip {
	public static int TabSpacing = 8;
	public static int IconSize   = 8; 
	
	ArrayList<Line>       lines    = new ArrayList<Line>();
	ArrayList<Renderable> elements = new ArrayList<Renderable>();
	int w,h,x,y,ty;
	int offsetX;
	int maxStringW;
	Point pos;
	boolean hasIcon = false;
	ItemStack stack;
	
	IWailaDataAccessor accessor = DataAccessorBlock.instance;
	
	int[] columnsWidth;
	int   columnsWidthMono;
	int[] columnsPos;
	int   ncolumns = 0;
	
	/////////////////////////////////////Line///////////////////////////////////////
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
					else if (s.startsWith(RENDER)){
						IWailaTooltipRenderer renderer = ModuleRegistrar.instance().getTooltipRenderer(s.substring(3));
						if (renderer != null)
							columnsWidth[i] += renderer.getSize(DataAccessorBlock.instance).width;
					}
					else
						columnsWidth[i] += DisplayUtil.getStringWidth(s);
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

				if (!substring.trim().equals(""))
					retList.add(substring);
				
				String controlStr = s.substring(matcher.start(), matcher.end()); 

				// If we don't have a render type block
				if (controlStr.startsWith(RENDER)){
					int   endOfBlock = s.substring(prevIndex).indexOf("}");
					String renderStr = controlStr + s.substring(matcher.end() + 1, endOfBlock); 
					retList.add(renderStr);
					prevIndex += renderStr.length() + 2;
				} else {
					retList.add(controlStr);
					prevIndex = matcher.end();					
				}
			}
			retList.add(s.substring(prevIndex));
			
			return retList;
		}
	}
	////////////////////////////////////////////////////////////////////////////
	
	/////////////////////////////////////Renderable///////////////////////////////////////
	private class Renderable implements IWailaTooltipRenderer{
		final IWailaTooltipRenderer renderer;
		final Point pos;
		
		public Renderable(IWailaTooltipRenderer renderer, Point pos){
			this.renderer = renderer;
			this.pos      = pos;
		}
		
		//public void setPos(int x, int y){
		//	this.pos = new Point(x, y);
		//}
		
		public Point getPos(){
			return this.pos;
		}
		
		@Override
		public Dimension getSize(IWailaDataAccessor accessor) {
			return this.renderer.getSize(accessor);
		}
		
		@Override
		public void draw(IWailaDataAccessor accessor, int x, int y) {
			this.renderer.draw(accessor, x + this.pos.x, y + this.pos.y);
		}
		
		@Override
		public String toString(){
			return String.format("Renderable@[%d,%d] | %s", pos.x, pos.y, renderer);
		}
	}
	////////////////////////////////////////////////////////////////////////////
	
	
	public Tooltip(List<String> textData, ItemStack stack){
		this(textData, true);
		this.stack = stack;
	}
	
	public Tooltip(List<String> textData, boolean hasIcon){
		for (String s : textData)
			lines.add(new Line(s));		
		this.computeColumns();

		this.computeRenderables();
		this.computePositionAndSize(hasIcon);
	}
	
	private void computeColumns(){
		for (Line l : lines)
			ncolumns = Math.max(ncolumns, l.ncolumns);
		
		columnsWidth     = new int[ncolumns];
		columnsPos       = new int[ncolumns];

		for (Line l : lines){
			for (int i = 0; i < l.ncolumns; i++){
				if (l.ncolumns > 1 && ncolumns != 1)
					columnsWidth[i] = Math.max(columnsWidth[i], l.columnsWidth[i]);
			}
			columnsWidthMono = Math.max(columnsWidthMono, l.columnsWidth[0]);
		}

		maxStringW = 0;
		for (int i = 0; i < ncolumns; i++)
			maxStringW += columnsWidth[i];
		maxStringW += ncolumns * TabSpacing;

		maxStringW = Math.max(maxStringW, columnsWidthMono);
		
		for (int i = 0; i < ncolumns - 1; i++)
			columnsPos[i + 1] = columnsWidth[i] + columnsPos[i];		
	}
	
	private void computeRenderables(){
		int offsetY = 0;
		
		for (int i = 0; i < lines.size(); i++){
			int maxHeight = 0;
			
			for (int c = 0; c < lines.get(i).ncolumns; c++){
				int offsetX = 0;				

				for (int is = 0; is < lines.get(i).columns.get(c).size(); is++){
					String s = lines.get(i).columns.get(c).get(is);
					
					if (s.startsWith(ALIGNRIGHT))
						offsetX += columnsWidth[c] - DisplayUtil.getStringWidth(lines.get(i).columns.get(c).get(is + 1));					
					else{
						Renderable renderable = new Renderable(new TooltipRendererString(s), new Point(offsetX + columnsPos[c] + c*TabSpacing, offsetY)); 
						this.elements.add(renderable);
						offsetX  += renderable.getSize(accessor).width;
						maxHeight = Math.max(maxHeight, renderable.getSize(accessor).height);
					}
				}
			}
			offsetY += maxHeight;
		}
	}
	
	private int  getRenderableTotalHeight(){
		int result = 0;
		for (Renderable r : this.elements)
			result = Math.max(r.getPos().y + r.getSize(accessor).height, result);
		return result;
	}
	
	private void computePositionAndSize(boolean hasIcon){
		this.pos      = new Point(ConfigHandler.instance().getConfig(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_POSX,0), 
                                  ConfigHandler.instance().getConfig(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_POSY,0));
		this.hasIcon  = hasIcon;
	
		int paddingW = hasIcon ? 29 : 13;
		int paddingH = hasIcon ? 24 : 0; 
		offsetX      = hasIcon ? 24 : 6;
	
		w = maxStringW + paddingW;
		
		h = Math.max(paddingH, this.getRenderableTotalHeight() + 10);
		
		Dimension size = DisplayUtil.displaySize();
		x = ((int)(size.width  / OverlayConfig.scale)-w-1)*pos.x/10000;
		y = ((int)(size.height / OverlayConfig.scale)-h-1)*pos.y/10000;	
		
		ty = (h - this.getRenderableTotalHeight())/2;
	}
	
	public void drawStrings(){
		for (Renderable r : this.elements)
			r.draw(accessor, x + offsetX, y + ty);
		
		/*
		for (int i = 0; i < lines.size(); i++){
			for (int c = 0; c < lines.get(i).ncolumns; c++){
				int offX = 0;				

				for (int is = 0; is < lines.get(i).columns.get(c).size(); is++){
					String s = lines.get(i).columns.get(c).get(is);
					if (s.startsWith(WailaStyle + WailaIcon))
						offX += IconSize;
					
					else if (s.startsWith(ALIGNRIGHT))
						offX += columnsWidth[c] - GuiDraw.getStringWidth(lines.get(i).columns.get(c).get(is + 1));
					
					else{
						GuiDraw.drawString(s, x + offsetX + columnsPos[c] + c*TabSpacing + offX, y + ty + 10*i, OverlayConfig.fontcolor, true);
						offX += GuiDraw.getStringWidth(s);
					}
				}				
			}
		}
		*/
	}
	
	public void drawIcons(){
	/*
		for (int i = 0; i < lines.size(); i++){
			for (int c = 0; c < lines.get(i).ncolumns; c++){
				int offX = 0;				
				for (String s : lines.get(i).columns.get(c)){
					if (s.startsWith(WailaStyle + WailaIcon)){
						OverlayRenderer.renderIcon(x + offsetX + columnsPos[c] + c*TabSpacing + offX, y + ty + 10*i, IconSize, IconSize, IconUI.bySymbol(s));
						offX += IconSize;
					} else if (s.startsWith(RENDER)){
						IWailaTooltipRenderer renderer = ModuleRegistrar.instance().getTooltipRenderer(s.substring(3));
						if (renderer != null){
							renderer.draw(DataAccessorBlock.instance, x + offsetX + columnsPos[c] + c*TabSpacing + offX, y + ty + 10*i);
							offX += renderer.getSize(DataAccessorBlock.instance).getWidth();
						}
					}else{
						offX += GuiDraw.getStringWidth(s);
					}
				}
			}
		}
	*/
	}	
}
