package lol.bai.badpackets.impl;

import net.minecraft.resources.Identifier;

public class Constants {

    public static final String MOD_ID = "badpackets";

    public static final Identifier CHANNEL_SYNC = id("channel_sync");
    public static final Identifier MC_REGISTER_CHANNEL = Identifier.parse("minecraft:register");
    public static final Identifier MC_UNREGISTER_CHANNEL = Identifier.parse("minecraft:unregister");

    public static final int PING_PONG = 0xBAD4C7_01;

    public static final byte CHANNEL_SYNC_SINGLE = 0;
    public static final byte CHANNEL_SYNC_INITIAL = 1;

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }

}
