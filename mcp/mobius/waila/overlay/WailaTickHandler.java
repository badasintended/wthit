package mcp.mobius.waila.overlay;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import mcp.mobius.waila.mod_Waila;
import mcp.mobius.waila.handlers.hud.HUDHandlerExternal;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import codechicken.lib.lang.LangUtil;
import codechicken.nei.api.ItemInfo.Layout;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;

public class WailaTickHandler implements ITickHandler {

    //public static LangUtil lang = LangUtil.loadLangDir("waila");
	
	private int ticks = 0;
	public ItemStack identifiedHighlight = new ItemStack(Block.dirt);
	public List<String> currenttipHEAD   = new ArrayList<String>();
	public List<String> currenttipBODY   = new ArrayList<String>(); 
	public List<String> currenttipTAIL   = new ArrayList<String>(); 
	public HUDHandlerExternal handler    = new HUDHandlerExternal();
	
	
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
			RayTracing.instance().fire();
			
			World world                 = Minecraft.getMinecraft().theWorld;
			EntityPlayer player         = Minecraft.getMinecraft().thePlayer;
			ItemStack targetStack       = RayTracing.instance().getTargetStack();
			MovingObjectPosition target = RayTracing.instance().getTarget();
			
			if (world != null && player != null && target != null && targetStack != null){
				//this.currenttipHEAD.clear();
				//this.currenttipBODY.clear();
				//this.currenttipTAIL.clear();

				this.identifiedHighlight = handler.identifyHighlight(world, player, target);
				this.currenttipHEAD      = handler.handleTextData(targetStack, world, player, target, new ArrayList<String>(), Layout.HEADER);
				this.currenttipBODY      = handler.handleTextData(targetStack, world, player, target, new ArrayList<String>(), Layout.BODY);
				this.currenttipTAIL      = handler.handleTextData(targetStack, world, player, target, new ArrayList<String>(), Layout.FOOTER);
			}
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
