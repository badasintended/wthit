package mcp.mobius.waila.handlers;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.network.MessageServerPing;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.network.handshake.NetworkDispatcher;

@Mod.EventBusSubscriber
public class NetworkHandler {

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerLoggedInEvent event) {
        Waila.LOGGER.info(String.format("Player %s connected. Sending ping", event.player));
        if (hasWaila(event.player))
            Waila.NETWORK_WRAPPER.sendTo(new MessageServerPing(null), (EntityPlayerMP) event.player);
    }

    private static boolean hasWaila(EntityPlayer player) {
        return NetworkDispatcher.get(((EntityPlayerMP) player).connection.getNetworkManager()).getModList().containsKey(Waila.MODID);
    }
}
