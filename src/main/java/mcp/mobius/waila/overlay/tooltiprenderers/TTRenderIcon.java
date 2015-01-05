package mcp.mobius.waila.overlay.tooltiprenderers;

import static mcp.mobius.waila.api.SpecialChars.WailaIcon;
import static mcp.mobius.waila.api.SpecialChars.WailaStyle;

import java.awt.Dimension;

import org.lwjgl.opengl.GL11;

import mcp.mobius.waila.api.IWailaCommonAccessor;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaTooltipRenderer;
import mcp.mobius.waila.overlay.DisplayUtil;
import mcp.mobius.waila.overlay.OverlayRenderer;
import mcp.mobius.waila.overlay.IconUI;

public class TTRenderIcon implements IWailaTooltipRenderer {

	final String type;
	final int IconSize = 8;
	
	public TTRenderIcon(String type){
		this.type = type;
	}
	
	@Override
	public Dimension getSize(String[] params, IWailaCommonAccessor accessor) {
		return new Dimension(IconSize, IconSize);
	}

	@Override
	public void draw(String[] params, IWailaCommonAccessor accessor) {
		DisplayUtil.renderIcon(0, 0, IconSize, IconSize, IconUI.bySymbol(WailaStyle + WailaIcon + type));		
	}

}
