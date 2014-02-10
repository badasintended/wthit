package mcp.mobius.waila.events;

import mcp.mobius.waila.network.Message0x00ServerPing;
import mcp.mobius.waila.network.WailaPacketHandler;
import net.minecraft.entity.player.EntityPlayerMP;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;

public class NetworkHandler {
	@SubscribeEvent
	public void onPlayerLoggedIn(PlayerLoggedInEvent event){
		WailaPacketHandler.INSTANCE.sendTo(new Message0x00ServerPing(), (EntityPlayerMP)event.player);
	}
}
