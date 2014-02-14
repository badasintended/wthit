package mcp.mobius.waila.overlay;

import java.util.ArrayList;
import java.util.List;
import java.awt.Dimension;
import java.awt.Point;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.Configuration;
import mcp.mobius.waila.Constants;
import mcp.mobius.waila.api.impl.ConfigHandler;
import static codechicken.core.gui.GuiDraw.*;

public class Tooltip {
	public List<String> textData = new ArrayList<String>();
	int w,h,x,y,ty;
	int offsetX;
	Point pos;
	boolean hasIcon = false;
	ItemStack stack;

	public Tooltip(List<String> textData, ItemStack stack){
		this(textData, true);
		this.stack = stack;
	}
	
	public Tooltip(List<String> textData, boolean hasIcon){
		this.textData = textData;
		this.pos      = new Point(ConfigHandler.instance().getConfig(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_POSX,0), 
				                  ConfigHandler.instance().getConfig(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_POSY,0));
		this.hasIcon  = hasIcon;
		
		int offsetW = hasIcon ? 29 : 13;
		int offsetH = hasIcon ? 24 : 0; 
		offsetX     = hasIcon ? 24 : 6;
		
		
        w = 0;
        for (String s : textData)
        	w = Math.max(w, getStringWidth(s) + offsetW);
        h = Math.max(offsetH, 10 + 10*textData.size());

        Dimension size = displaySize();
        x = ((int)(size.width / OverlayConfig.scale)-w-1)*pos.x/10000;
        y = ((int)(size.height / OverlayConfig.scale)-h-1)*pos.y/10000;	
        
        ty = (h-10*textData.size())/2;        
	}
}
