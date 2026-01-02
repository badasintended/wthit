package mcp.mobius.waila;

public abstract class WailaDedicatedServer {

    protected static void onDedicatedServerTick() {
        Waila.onAnyTick();
    }

}
