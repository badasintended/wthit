package mcp.mobius.waila.network.forge;

import mcp.mobius.waila.network.ClientNetworkHandler;
import net.minecraft.nbt.CompoundTag;

public class ClientNetworkHandlerImpl extends ClientNetworkHandler {

    public static void init() {
    }

    public static class ReceiveData {

        public CompoundTag tag;

        public ReceiveData(CompoundTag tag) {
            this.tag = tag;
        }

    }

}
