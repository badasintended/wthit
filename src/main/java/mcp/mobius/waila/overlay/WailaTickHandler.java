package mcp.mobius.waila.overlay;

import java.util.List;

import org.lwjgl.input.Keyboard;

import mcp.mobius.waila.api.impl.ConfigHandler;
import mcp.mobius.waila.api.impl.DataAccessorBlock;
import mcp.mobius.waila.api.impl.DataAccessorEntity;
import mcp.mobius.waila.api.impl.MetaDataProvider;
import mcp.mobius.waila.api.impl.TipList;
import mcp.mobius.waila.client.KeyEvent;
import mcp.mobius.waila.utils.Constants;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import codechicken.nei.api.ItemInfo.Layout;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import static mcp.mobius.waila.api.SpecialChars.*;

public class WailaTickHandler{

    //public static LangUtil lang = LangUtil.loadLangDir("waila");
	
	private int ticks = 0;
	public ItemStack identifiedHighlight = new ItemStack(Blocks.dirt);
	private List<String> currenttip      = new TipList<String, String>();
	private List<String> currenttipHead  = new TipList<String, String>();
	private List<String> currenttipBody  = new TipList<String, String>();
	private List<String> currenttipTail  = new TipList<String, String>();
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

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void tickRender(TickEvent.RenderTickEvent event) {
		OverlayRenderer.renderOverlay(); 
	}	

	@SubscribeEvent
	@SideOnly(Side.CLIENT)	
	public void tickClient(TickEvent.ClientTickEvent event) {
		
		if (!Keyboard.isKeyDown(KeyEvent.key_show.getKeyCode()) && !ConfigHandler.instance().getConfig(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_MODE, false)){
			ConfigHandler.instance().setConfig(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_SHOW, false);	
		}		
		
		
		World world                 = mc.theWorld;
		EntityPlayer player         = mc.thePlayer;			
		if (world != null && player != null){
			RayTracing.instance().fire();
			MovingObjectPosition target = RayTracing.instance().getTarget();
			
			if (target != null && target.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK){
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
			else if (target != null && target.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY){
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
