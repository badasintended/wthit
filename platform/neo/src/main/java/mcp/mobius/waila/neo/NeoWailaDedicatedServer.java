package mcp.mobius.waila.neo;

import mcp.mobius.waila.WailaDedicatedServer;
import mcp.mobius.waila.api.WailaConstants;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

public class NeoWailaDedicatedServer extends WailaDedicatedServer {

    @EventBusSubscriber(modid = WailaConstants.WAILA, value = Dist.DEDICATED_SERVER)
    static class Subscriber {

        @SubscribeEvent
        static void clientTick(ServerTickEvent.Post event) {
            onDedicatedServerTick();
        }

    }

}
