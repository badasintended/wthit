package mcp.mobius.waila.overlay;

import java.awt.Dimension;

import codechicken.lib.gui.GuiDraw;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaTooltipRenderer;

public class TooltipRendererString implements IWailaTooltipRenderer{

	final String    data;
	final Dimension size;
	
	public TooltipRendererString(String data){
		this.data = data;
		this.size = new Dimension(DisplayUtil.getDisplayWidth(data), data.equals("") ? 0 : 10);
	}
	
	@Override
	public Dimension getSize(String[] params, IWailaDataAccessor accessor) {
		return size;
	}

	@Override
	public void draw(String[] params, IWailaDataAccessor accessor, int x, int y) {
		GuiDraw.drawString(data, x, y, OverlayConfig.fontcolor, true);		
	}

}
