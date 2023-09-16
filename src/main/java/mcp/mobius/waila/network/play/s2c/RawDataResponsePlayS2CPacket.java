package mcp.mobius.waila.network.play.s2c;

import lol.bai.badpackets.api.PacketSender;
import mcp.mobius.waila.Waila;
import mcp.mobius.waila.access.DataReader;
import mcp.mobius.waila.network.Packet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class RawDataResponsePlayS2CPacket implements Packet.PlayS2C<RawDataResponsePlayS2CPacket.Payload> {

    public static final ResourceLocation ID = Waila.id("data");

    @Override
    public ResourceLocation id() {
        return ID;
    }

    @Override
    public Payload read(FriendlyByteBuf buf) {
        return new Payload(buf.readNbt());
    }

    @Override
    public void receive(Minecraft client, ClientPacketListener handler, Payload payload, PacketSender responseSender) {
        DataReader.INSTANCE.reset(payload.data);
    }

    public record Payload(
        CompoundTag data
    ) implements CustomPacketPayload {

        @Override
        public void write(FriendlyByteBuf buf) {
            buf.writeNbt(data);
        }

        @Override
        public @NotNull ResourceLocation id() {
            return ID;
        }

    }

}
