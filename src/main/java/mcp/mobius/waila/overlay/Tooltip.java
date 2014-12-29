package mcp.mobius.waila.overlay;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.awt.Dimension;
import java.awt.Point;

import scala.actors.threadpool.Arrays;
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
	
	ArrayList<ArrayList<String>>  lines = new ArrayList<ArrayList<String>>();
	ArrayList<ArrayList<Integer>> sizes = new ArrayList<ArrayList<Integer>>();
	ArrayList<Integer>          columns = new ArrayList<Integer>();
	ArrayList<Integer>       columnsPos = new ArrayList<Integer>();	
	
	ArrayList<Renderable> elements = new ArrayList<Renderable>();
	int w,h,x,y,ty;
	int offsetX;
	int maxStringW;
	Point pos;
	boolean hasIcon = false;
	ItemStack stack;
	
	IWailaDataAccessor accessor = DataAccessorBlock.instance;
	
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

		columns.add(0);		// Small init of the arrays to have at least one element
		columnsPos.add(0);		
		
		for (String s : textData){
			ArrayList<String>  line = new ArrayList(Arrays.asList(patternTab.split(s)));
			ArrayList<Integer> size = new ArrayList<Integer>();
			for (String ss : line)
				size.add(DisplayUtil.getDisplayWidth(ss));
			
			if (line.size() > 1){
				while (columns.size() < line.size()){
					columns.add(0);
					columnsPos.add(0);
				}

				for (int i = 0; i < line.size(); i++)
					columns.set(i, Math.max(columns.get(i), size.get(i)));
			}
			
			maxStringW = Math.max(maxStringW, DisplayUtil.getDisplayWidth(s) + TabSpacing * (line.size() - 1));
			
			lines.add(line);
			sizes.add(size);
		}
		
		// We compute the position of the columns to be able to align the renderable later on
		for (int i = 1; i < columns.size(); i++)
			columnsPos.set(i, columns.get(i - 1) + columnsPos.get(i - 1) + TabSpacing);
		
		this.computeRenderables();
		this.computePositionAndSize(hasIcon);
	}
	
	private void computeRenderables(){
		int offsetY = 0;
		for (int i = 0; i < lines.size(); i++){				// We check all the lines, one by one
			int maxHeight = 0;	// Maximum height of this line
			for (int c = 0; c < lines.get(i).size(); c++){	// We check all the columns for this line
				String cs   = lines.get(i).get(c);
				offsetX     = columnsPos.get(c);
				
				Renderable renderable = new Renderable(new TooltipRendererString(DisplayUtil.stripWailaSymbols(cs)), new Point(offsetX, offsetY));
				this.elements.add(renderable);
				offsetX  += renderable.getSize(accessor).width;
				maxHeight = Math.max(maxHeight, renderable.getSize(accessor).height);				
			}
			offsetY += maxHeight;
		}
		
		/*
		int offsetY = 0;
		
		for (int i = 0; i < lines.size(); i++){
			int maxHeight = 0;
			
			for (int c = 0; c < lines.get(i).ncolumns; c++){
				int offsetX = 0;				

				for (int is = 0; is < lines.get(i).columns.get(c).size(); is++){
					String s = lines.get(i).columns.get(c).get(is);
					
					if (s.startsWith(ALIGNRIGHT))
						offsetX += columnsWidth[c] - DisplayUtil.getDisplayWidth(lines.get(i).columns.get(c).get(is + 1));					
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
		*/
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
