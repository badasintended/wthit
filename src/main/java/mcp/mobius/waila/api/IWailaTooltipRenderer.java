package mcp.mobius.waila.api;

import java.awt.Dimension;
import java.awt.Point;


public interface IWailaTooltipRenderer {
	Dimension getSize(IWailaDataAccessor accessor);
	void      draw   (IWailaDataAccessor accessor, int x, int y);
}
