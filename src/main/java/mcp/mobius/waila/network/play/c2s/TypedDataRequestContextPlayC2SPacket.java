package mcp.mobius.waila.network.play.c2s;

import lol.bai.badpackets.api.PacketSender;
import mcp.mobius.waila.Waila;
import mcp.mobius.waila.access.DataReader;
import mcp.mobius.waila.api.IData;
import mcp.mobius.waila.network.Packet;
import mcp.mobius.waila.registry.Registrar;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.jetbrains.annotations.NotNull;

public class TypedDataRequestContextPlayC2SPacket implements Packet.PlayC2S<TypedDataRequestContextPlayC2SPacket.Payload> {

    public static final ResourceLocation ID = Waila.id("ctx_typed");

    @Override
    public ResourceLocation id() {
        return ID;
    }

    @Override
    public Payload read(FriendlyByteBuf buf) {
        return new Payload(DataReader.readTypedPacket(buf));
    }

    @Override
    public void receive(MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl handler, Payload payload, PacketSender responseSender) {
        DataReader.SERVER.add(payload.ctx);
    }

    public record Payload(
        IData ctx
    ) implements CustomPacketPayload {

        @Override
        public void write(FriendlyByteBuf buf) {
            buf.writeResourceLocation(Registrar.INSTANCE.dataType2Id.get(ctx.getClass()));
            ctx.write(buf);
        }

        @Override
        public @NotNull ResourceLocation id() {
            return ID;
        }

    }

}
