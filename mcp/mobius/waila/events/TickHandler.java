package mcp.mobius.waila.events;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.network.FMLOutboundHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.impl.DataAccessor;
import mcp.mobius.waila.api.impl.Layout;
import mcp.mobius.waila.api.impl.MetaDataProvider;
import mcp.mobius.waila.network.Message0x00ServerPing;
import mcp.mobius.waila.network.WailaPacketHandler;
import mcp.mobius.waila.overlay.OverlayRenderer;
import mcp.mobius.waila.overlay.RayTracing;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemBlockWithMetadata;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

public class TickHandler {

    //public static LangUtil lang = LangUtil.loadLangDir("waila");
	
	private int ticks = 0;
	public ItemStack identifiedHighlight;// = new ItemStack(Blocks.dirt);
	public List<String> currenttip       = new ArrayList<String>(); 	
	public MetaDataProvider handler    = new MetaDataProvider();
	private Minecraft mc = Minecraft.getMinecraft();
	
	private static TickHandler _instance;
	private TickHandler(){}
	
	public static TickHandler instance(){
		if(_instance == null)
			_instance = new TickHandler();
		return _instance;
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void tickRender(TickEvent.RenderTickEvent event) {
		OverlayRenderer.instance().renderOverlay(); 
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)	
	public void tickClient(TickEvent.ClientTickEvent event) {		
		World world                 = mc.theWorld;
		EntityPlayer player         = mc.thePlayer;			
		if (world != null && player != null){
			RayTracing.instance().fire();
			MovingObjectPosition target = RayTracing.instance().getTarget();
			
			if (target != null){
				DataAccessor accessor = DataAccessor.instance;
				accessor.set(world, player, target);
				ItemStack targetStack = RayTracing.instance().getTargetStack();
				
				if (targetStack != null){
					this.currenttip.clear();
					
					//this.identifiedHighlight = handler.identifyHighlight(world, player, target);
					this.currenttip      = handler.handleTextData(targetStack, world, player, target, accessor, currenttip, Layout.HEADER);
					this.currenttip      = handler.handleTextData(targetStack, world, player, target, accessor, currenttip, Layout.BODY);
					this.currenttip      = handler.handleTextData(targetStack, world, player, target, accessor, currenttip, Layout.FOOTER);
				}
			}
		}
	}
}
