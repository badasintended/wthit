package mcp.mobius.waila.network.common.s2c;

import java.util.HashSet;
import java.util.Set;

import lol.bai.badpackets.api.PacketSender;
import mcp.mobius.waila.Waila;
import mcp.mobius.waila.network.Packet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientConfigurationPacketListenerImpl;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class BlacklistSyncCommonS2CPacket implements
    Packet.ConfigS2C<BlacklistSyncCommonS2CPacket.Payload>,
    Packet.PlayS2C<BlacklistSyncCommonS2CPacket.Payload> {

    public static final ResourceLocation ID = Waila.id("blacklist");

    @Override
    public ResourceLocation id() {
        return ID;
    }

    @Override
    public Payload read(FriendlyByteBuf buf) {
        return new Payload(
            buf.readCollection(HashSet::new, FriendlyByteBuf::readUtf),
            buf.readCollection(HashSet::new, FriendlyByteBuf::readUtf),
            buf.readCollection(HashSet::new, FriendlyByteBuf::readUtf));
    }

    private static void receive(Payload payload) {
        Waila.BLACKLIST_CONFIG.get().getView().sync(payload.blockRules, payload.blockEntityRules, payload.entityRules);
    }

    @Override
    public void receive(Minecraft client, ClientConfigurationPacketListenerImpl handler, Payload payload, PacketSender responseSender) {
        receive(payload);
    }

    @Override
    public void receive(Minecraft client, ClientPacketListener handler, Payload payload, PacketSender responseSender) {
        receive(payload);
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
        public void write(@NotNull FriendlyByteBuf buf) {
            buf.writeCollection(blockRules, FriendlyByteBuf::writeUtf);
            buf.writeCollection(blockEntityRules, FriendlyByteBuf::writeUtf);
            buf.writeCollection(entityRules, FriendlyByteBuf::writeUtf);
        }

        @Override
        public @NotNull ResourceLocation id() {
            return ID;
        }

    }

}
