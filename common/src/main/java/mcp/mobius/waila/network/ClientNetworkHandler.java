package mcp.mobius.waila.network;

import mcp.mobius.waila.Waila;
import me.shedaniel.architectury.annotations.ExpectPlatform;
import net.minecraft.util.Identifier;

public class ClientNetworkHandler {

    public static final Identifier RECEIVE_DATA = new Identifier(Waila.MODID, "receive_data");
    public static final Identifier GET_CONFIG = new Identifier(Waila.MODID, "send_config");

    @ExpectPlatform
    public static void init() {
    }

}
