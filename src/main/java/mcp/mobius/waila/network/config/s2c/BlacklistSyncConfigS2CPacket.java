package mcp.mobius.waila.network.config.s2c;

import java.util.HashSet;
import java.util.Set;

import lol.bai.badpackets.api.PacketSender;
import mcp.mobius.waila.Waila;
import mcp.mobius.waila.network.Packet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientConfigurationPacketListenerImpl;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class BlacklistSyncConfigS2CPacket implements Packet.ConfigS2C<BlacklistSyncConfigS2CPacket.Payload> {

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

    @Override
    public void receive(Minecraft client, ClientConfigurationPacketListenerImpl handler, Payload payload, PacketSender responseSender) {
        Waila.BLACKLIST_CONFIG.get().getView().sync(payload.blockRules, payload.blockEntityRules, payload.entityRules);
    }

    public record Payload(
        Set<String> blockRules,
        Set<String> blockEntityRules,
        Set<String> entityRules
    ) implements CustomPacketPayload {

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
