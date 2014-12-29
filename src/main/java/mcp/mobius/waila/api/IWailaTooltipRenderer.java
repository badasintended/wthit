package mcp.mobius.waila.api;

import java.awt.Dimension;
import java.awt.Point;


public interface IWailaTooltipRenderer {
	Dimension getSize(String[] params, IWailaDataAccessor accessor);
	void      draw   (String[] params, IWailaDataAccessor accessor, int x, int y);
}
