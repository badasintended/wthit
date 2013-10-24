package mcp.mobius.waila.overlay;

import java.util.EnumSet;

import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;

public class WailaTickHandler implements ITickHandler {

	private int ticks = 0;
	
	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData) {
	}

	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData) {
		if(type.contains(TickType.RENDER)){
			RayTracing.raytrace();
			OverlayRenderer.renderOverlay();
		}
		
		//if (ticks == 100){
		//	ticks = 0;
		//	mod_Waila.updateColors();
		//}
		//ticks++;
			
	}

    @Override
    public EnumSet<TickType> ticks() 
    {
        return EnumSet.of(TickType.RENDER);
    }

    @Override
    public String getLabel() 
    {
        return "Waila Tick Handler";
    }

}
