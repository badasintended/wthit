package mcp.mobius.waila.overlay;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import mcp.mobius.waila.handlers.hud.HUDHandlerExternal;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import codechicken.lib.lang.LangUtil;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;

public class WailaTickHandler implements ITickHandler {

    //public static LangUtil lang = LangUtil.loadLangDir("waila");
	
	private int ticks = 0;
	public ItemStack identifiedHighlight = new ItemStack(Block.dirt);
	public List<String> currenttipHEAD   = new ArrayList<String>();
	public List<String> currenttipBODY   = new ArrayList<String>(); 
	public List<String> currenttipTAIL   = new ArrayList<String>(); 

	private static WailaTickHandler _instance;
	private WailaTickHandler(){}
	public static WailaTickHandler instance(){
		if(_instance == null)
			_instance = new WailaTickHandler();
		return _instance;
	}
	
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
			
			/*
			World world               = Minecraft.getMinecraft().theWorld;
			EntityPlayer player       = Minecraft.getMinecraft().thePlayer;
			ItemStack identifiedStack = RayTracing.raytracedStack;
			
			if (world != null && player != null && RayTracing.raytracedTarget != null){
				currenttipHEAD.clear();
				currenttipBODY.clear();
				currenttipTAIL.clear();
				
				identifiedHighlight = HUDHandlerExternal.instance().identifyHighlight(world, player, RayTracing.raytracedTarget);
				currenttipHEAD      = HUDHandlerExternal.instance().handleTextData(identifiedStack, world, player, RayTracing.raytracedTarget, currenttipHEAD, Layout.HEADER);
				currenttipBODY      = HUDHandlerExternal.instance().handleTextData(identifiedStack, world, player, RayTracing.raytracedTarget, currenttipBODY, Layout.BODY);
				currenttipTAIL      = HUDHandlerExternal.instance().handleTextData(identifiedStack, world, player, RayTracing.raytracedTarget, currenttipTAIL, Layout.FOOTER);
				
				for(String s : currenttipBODY){
					System.out.printf("%s\n",s);
				}
			}			
			*/
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
