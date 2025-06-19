package mcp.mobius.waila.forge;

import mcp.mobius.waila.WailaDedicatedServer;
import mcp.mobius.waila.api.WailaConstants;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

public class ForgeWailaDedicatedServer extends WailaDedicatedServer {

    @Mod.EventBusSubscriber(modid = WailaConstants.WAILA, value = Dist.DEDICATED_SERVER)
    static class Subscriber {

        @SubscribeEvent
        static void clientTick(TickEvent.PlayerTickEvent.Post event) {
            onDedicatedServerTick();
        }

    }

}
