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
		this.size = new Dimension(GuiDraw.getStringWidth(data), 10);
	}
	
	@Override
	public Dimension getSize(IWailaDataAccessor accessor) {
		return size;
	}

	@Override
	public void draw(IWailaDataAccessor accessor, int x, int y) {
		GuiDraw.drawString(data, x, y, OverlayConfig.fontcolor, true);		
	}

}
