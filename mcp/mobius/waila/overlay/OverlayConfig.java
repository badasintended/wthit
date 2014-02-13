package mcp.mobius.waila.overlay;

import net.minecraftforge.common.Configuration;
import mcp.mobius.waila.Constants;
import mcp.mobius.waila.api.impl.ConfigHandler;

public class OverlayConfig {
	public static int posX;
	public static int posY;
	public static int alpha;
	public static int bgcolor;
	public static int gradient1;
	public static int gradient2;
	public static int fontcolor;
	public static float scale;
	
	public static void updateColors(){
		OverlayConfig.alpha     = (int)(ConfigHandler.instance().getConfig(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_ALPHA,0) / 100.0f * 256) << 24;
		OverlayConfig.bgcolor   = OverlayConfig.alpha + ConfigHandler.instance().getConfig(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_BGCOLOR,0);
		OverlayConfig.gradient1 = OverlayConfig.alpha + ConfigHandler.instance().getConfig(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_GRADIENT1,0);
		OverlayConfig.gradient2 = OverlayConfig.alpha + ConfigHandler.instance().getConfig(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_GRADIENT2,0);
		OverlayConfig.fontcolor = OverlayConfig.alpha + ConfigHandler.instance().getConfig(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_FONTCOLOR,0);
	}	
}
