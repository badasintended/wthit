package mcp.mobius.waila.api;

import java.awt.Point;


public interface IWailaTooltipRenderer {
	Point getSize(IWailaDataAccessor accessor);
	void  draw   (IWailaDataAccessor accessor);
}
