package mcp.mobius.waila.api;

import java.awt.Dimension;
import java.awt.Point;


public interface IWailaTooltipRenderer {
	Dimension getSize(String[] params, IWailaCommonAccessor accessor);
	void      draw   (String[] params, IWailaCommonAccessor accessor, int x, int y);
}
