package mcp.mobius.waila.network.play.s2c;

import lol.bai.badpackets.api.PacketSender;
import mcp.mobius.waila.Waila;
import mcp.mobius.waila.access.DataReader;
import mcp.mobius.waila.api.IData;
import mcp.mobius.waila.network.Packet;
import mcp.mobius.waila.registry.Registrar;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class TypedDataResponsePlayS2CPacket implements Packet.PlayS2C<TypedDataResponsePlayS2CPacket.Payload> {

    public static final ResourceLocation ID = Waila.id("data_typed");

    @Override
    public ResourceLocation id() {
        return ID;
    }

    @Override
    public Payload read(FriendlyByteBuf buf) {
        return new Payload(DataReader.readTypedPacket(buf));
    }

    @Override
    public void receive(Minecraft client, ClientPacketListener handler, Payload payload, PacketSender responseSender) {
        DataReader.CLIENT.add(payload.data);
    }

    public record Payload(
        IData data
    ) implements CustomPacketPayload {

        @Override
        public void write(FriendlyByteBuf buf) {
            buf.writeResourceLocation(Registrar.get().dataType2Id.get(data.getClass()));
            data.write(buf);
        }

        @Override
        public @NotNull ResourceLocation id() {
            return ID;
        }

    }

}
