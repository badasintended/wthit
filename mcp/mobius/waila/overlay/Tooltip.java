package mcp.mobius.waila.overlay;

import java.util.ArrayList;
import java.util.List;
import java.awt.Dimension;
import java.awt.Point;

import net.minecraftforge.common.Configuration;
import mcp.mobius.waila.Constants;
import mcp.mobius.waila.api.impl.ConfigHandler;
import static codechicken.core.gui.GuiDraw.*;

public class Tooltip {
	public List<String> textData = new ArrayList<String>();
	int w,h,x,y,ty;
	Point pos;
	
	public Tooltip(List<String> textData){
		this.textData = textData;
		this.pos      = new Point(ConfigHandler.instance().getConfig(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_POSX,0), ConfigHandler.instance().getConfig(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_POSY,0));
		
        w = 0;
        for (String s : textData)
            w = Math.max(w, getStringWidth(s)+29);
        h = Math.max(24, 10 + 10*textData.size());

        Dimension size = displaySize();
        x = ((int)(size.width / OverlayConfig.scale)-w-1)*pos.x/10000;
        y = ((int)(size.height / OverlayConfig.scale)-h-1)*pos.y/10000;	
        
        ty = (h-10*textData.size())/2;        
	}
}
