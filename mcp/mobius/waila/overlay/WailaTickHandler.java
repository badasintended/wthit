package mcp.mobius.waila.overlay;

import java.util.EnumSet;

import codechicken.lib.lang.LangUtil;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;

public class WailaTickHandler implements ITickHandler {

    //public static LangUtil lang = LangUtil.loadLangDir("waila");
	
	private int ticks = 0;
	
	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData) {
	}

	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData) {
		if(type.contains(TickType.RENDER)){
			OverlayRenderer.renderOverlay();
		}
		
		if(type.contains(TickType.CLIENT)){
			RayTracing.raytrace();
		}		
	}

    @Override
    public EnumSet<TickType> ticks() 
    {
        return EnumSet.of(TickType.RENDER, TickType.CLIENT);
    }

    @Override
    public String getLabel() 
    {
        return "Waila Tick Handler";
    }

}
