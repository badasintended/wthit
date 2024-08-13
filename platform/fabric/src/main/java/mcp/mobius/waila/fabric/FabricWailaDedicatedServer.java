package mcp.mobius.waila.fabric;

import mcp.mobius.waila.WailaDedicatedServer;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

public class FabricWailaDedicatedServer extends WailaDedicatedServer implements DedicatedServerModInitializer {

    @Override
    public void onInitializeServer() {
        ServerTickEvents.END_SERVER_TICK.register(server -> onDedicatedServerTick());
    }

}
