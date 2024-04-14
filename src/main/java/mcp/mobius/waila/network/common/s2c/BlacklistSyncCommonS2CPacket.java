package mcp.mobius.waila.network.common.s2c;

import java.util.HashSet;
import java.util.Set;

import lol.bai.badpackets.api.config.ConfigPackets;
import lol.bai.badpackets.api.play.PlayPackets;
import mcp.mobius.waila.Waila;
import mcp.mobius.waila.network.Packet;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public class BlacklistSyncCommonS2CPacket implements Packet {

    public static final CustomPacketPayload.Type<Payload> TYPE = new CustomPacketPayload.Type<>(Waila.id("blacklist"));
    public static final StreamCodec<FriendlyByteBuf, Payload> CODEC = StreamCodec.composite(
        ByteBufCodecs.collection(HashSet::new, ByteBufCodecs.STRING_UTF8), Payload::blockRules,
        ByteBufCodecs.collection(HashSet::new, ByteBufCodecs.STRING_UTF8), Payload::blockEntityRules,
        ByteBufCodecs.collection(HashSet::new, ByteBufCodecs.STRING_UTF8), Payload::entityRules,
        Payload::new);

    @Override
    public void common() {
        ConfigPackets.registerClientChannel(TYPE, CODEC);
        PlayPackets.registerClientChannel(TYPE, CODEC);
    }

    @Override
    public void client() {
        ConfigPackets.registerClientReceiver(TYPE, (context, payload) -> receive(payload));
        PlayPackets.registerClientReceiver(TYPE, (context, payload) -> receive(payload));
    }

    private static void receive(Payload payload) {
        Waila.BLACKLIST_CONFIG.get().getView().sync(payload.blockRules, payload.blockEntityRules, payload.entityRules);
    }

    public record Payload(
        Set<String> blockRules,
        Set<String> blockEntityRules,
        Set<String> entityRules
    ) implements CustomPacketPayload {

        public Payload() {
            this(Waila.BLACKLIST_CONFIG.get().blocks, Waila.BLACKLIST_CONFIG.get().blockEntityTypes, Waila.BLACKLIST_CONFIG.get().entityTypes);
        }

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }

    }

}
