package mcp.mobius.waila.handlers;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.network.MessageServerPing;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;

public class NetworkHandler {

    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerLoggedInEvent event) {
        Waila.LOGGER.info(String.format("Player %s connected. Sending ping", event.player));
        Waila.NETWORK_WRAPPER.sendTo(new MessageServerPing(null), (EntityPlayerMP) event.player);
    }
}
