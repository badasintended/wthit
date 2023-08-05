package mcp.mobius.waila.network.s2c;

import java.nio.file.Path;

import lol.bai.badpackets.api.PacketSender;
import mcp.mobius.waila.Waila;
import mcp.mobius.waila.buildconst.Tl;
import mcp.mobius.waila.debug.DumpGenerator;
import mcp.mobius.waila.network.Packet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class GenerateClientDumpS2CPacket implements Packet.S2C<GenerateClientDumpS2CPacket.Payload> {

    public static final ResourceLocation ID = Waila.id("generate_client_dump");

    @Override
    public ResourceLocation id() {
        return ID;
    }

    @Override
    public Payload read(FriendlyByteBuf buf) {
        return new Payload();
    }

    @Override
    public void receive(Minecraft client, ClientPacketListener handler, Payload payload, PacketSender responseSender) {
        Path path = DumpGenerator.generate(DumpGenerator.CLIENT);
        if (path != null && client.player != null) {
            Component pathComponent = Component.literal(path.toString()).withStyle(style -> style
                .withUnderlined(true)
                .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, path.toString())));
            client.player.displayClientMessage(Component.translatable(Tl.Command.CLIENT_DUMP_SUCCESS, pathComponent), false);
        }
    }

    public record Payload() implements CustomPacketPayload {

        @Override
        public void write(@NotNull FriendlyByteBuf buf) {
        }

        @Override
        public @NotNull ResourceLocation id() {
            return ID;
        }

    }

}
