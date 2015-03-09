package mcp.mobius.waila.overlay.tooltiprenderers;

import java.awt.Dimension;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.IWailaCommonAccessor;
import mcp.mobius.waila.api.IWailaTooltipRenderer;
import mcp.mobius.waila.gui.truetyper.FontHelper;
import mcp.mobius.waila.gui.truetyper.TrueTypeFont;
import mcp.mobius.waila.overlay.DisplayUtil;

public class TTRenderTrueTyper implements IWailaTooltipRenderer{

	final String    data;
	final Dimension size;
	
	public TTRenderTrueTyper(String data){
		this.data = data;
		this.size = new Dimension(DisplayUtil.getDisplayWidth(data), data.equals("") ? 0 : (int)((TrueTypeFont)Waila.proxy.getFont()).getHeight()/2);
	}
	
	@Override
	public Dimension getSize(String[] params, IWailaCommonAccessor accessor) {
		return size;
	}

	@Override
	public void draw(String[] params, IWailaCommonAccessor accessor) {
		FontHelper.drawString(data, 0f, 0f, (TrueTypeFont)Waila.proxy.getFont(), 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f);
	}	
	
}
