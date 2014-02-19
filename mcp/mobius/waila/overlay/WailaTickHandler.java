package mcp.mobius.waila.overlay;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import mcp.mobius.waila.api.impl.ConfigHandler;
import mcp.mobius.waila.api.impl.DataAccessorBlock;
import mcp.mobius.waila.api.impl.DataAccessorEntity;
import mcp.mobius.waila.api.impl.MetaDataProvider;
import mcp.mobius.waila.utils.Constants;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumMovingObjectType;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.Configuration;
import codechicken.nei.api.ItemInfo.Layout;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;
import static mcp.mobius.waila.api.SpecialChars.*;

public class WailaTickHandler implements ITickHandler {

    //public static LangUtil lang = LangUtil.loadLangDir("waila");
	
	private int ticks = 0;
	public ItemStack identifiedHighlight = new ItemStack(Block.dirt);
	private List<String> currenttip      = new ArrayList<String>();
	private List<String> currenttipHead  = new ArrayList<String>();
	private List<String> currenttipBody  = new ArrayList<String>();
	private List<String> currenttipTail  = new ArrayList<String>();
	public  Tooltip      tooltip         = null;
	public  MetaDataProvider handler     = new MetaDataProvider();
	private Minecraft mc = Minecraft.getMinecraft();
	
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
			World world                 = mc.theWorld;
			EntityPlayer player         = mc.thePlayer;			
			if (world != null && player != null){
				RayTracing.instance().fire();
				MovingObjectPosition target = RayTracing.instance().getTarget();
				
				if (target != null && target.typeOfHit == EnumMovingObjectType.TILE){
					DataAccessorBlock accessor = DataAccessorBlock.instance;
					accessor.set(world, player, target);
					ItemStack targetStack = RayTracing.instance().getTargetStack();	// Here we get either the proper stack or the override
					
					if (targetStack != null){
						this.currenttip.clear();
						this.currenttipHead.clear();
						this.currenttipBody.clear();
						this.currenttipTail.clear();
						
						
						//this.identifiedHighlight = handler.identifyHighlight(world, player, target);
						this.currenttipHead      = handler.handleBlockTextData(targetStack, world, player, target, accessor, currenttipHead, Layout.HEADER);
						this.currenttipBody      = handler.handleBlockTextData(targetStack, world, player, target, accessor, currenttipBody, Layout.BODY);
						this.currenttipTail      = handler.handleBlockTextData(targetStack, world, player, target, accessor, currenttipTail, Layout.FOOTER);
						
						if (ConfigHandler.instance().getConfig(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_SHIFTBLOCK, false) && currenttipBody.size() > 0 && !accessor.getPlayer().isSneaking()){
							currenttipBody.clear();
							currenttipBody.add(ITALIC + "Press shift for more data");
						}						
						
						this.currenttip.addAll(this.currenttipHead);
						this.currenttip.addAll(this.currenttipBody);
						this.currenttip.addAll(this.currenttipTail);						
						
						this.tooltip         = new Tooltip(this.currenttip, targetStack);
					}
				}
				else if (target != null && target.typeOfHit == EnumMovingObjectType.ENTITY){
					DataAccessorEntity accessor = DataAccessorEntity.instance;
					accessor.set(world, player, target);
					
					Entity targetEnt = RayTracing.instance().getTargetEntity(); // This need to be replaced by the override check.
					
					if (targetEnt != null){
						this.currenttip.clear();
						this.currenttipHead.clear();
						this.currenttipBody.clear();
						this.currenttipTail.clear();
						
						this.currenttipHead      = handler.handleEntityTextData(targetEnt, world, player, target, accessor, currenttipHead, Layout.HEADER);
						this.currenttipBody      = handler.handleEntityTextData(targetEnt, world, player, target, accessor, currenttipBody, Layout.BODY);
						this.currenttipTail      = handler.handleEntityTextData(targetEnt, world, player, target, accessor, currenttipTail, Layout.FOOTER);
						
						if (ConfigHandler.instance().getConfig(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_SHIFTENTS, false) && currenttipBody.size() > 0 && !accessor.getPlayer().isSneaking()){
							currenttipBody.clear();
							currenttipBody.add(ITALIC + "Press shift for more data");
						}							
						
						this.currenttip.addAll(this.currenttipHead);
						this.currenttip.addAll(this.currenttipBody);
						this.currenttip.addAll(this.currenttipTail);						
						
						this.tooltip         = new Tooltip(this.currenttip, false);						
					}
				}
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
