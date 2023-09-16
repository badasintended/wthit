package mcp.mobius.waila.network;

import lol.bai.badpackets.api.config.ClientConfigPacketReceiver;
import lol.bai.badpackets.api.config.ServerConfigPacketReceiver;
import lol.bai.badpackets.api.play.ClientPlayPacketReceiver;
import lol.bai.badpackets.api.play.ServerPlayPacketReceiver;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public sealed interface Packet<P extends CustomPacketPayload> extends FriendlyByteBuf.Reader<P> {

    ResourceLocation id();

    P read(FriendlyByteBuf buf);

    @Override
    default P apply(FriendlyByteBuf buf) {
        return read(buf);
    }

    non-sealed interface ConfigC2S<P extends CustomPacketPayload> extends Packet<P>, ServerConfigPacketReceiver<P> {

    }

    non-sealed interface ConfigS2C<P extends CustomPacketPayload> extends Packet<P>, ClientConfigPacketReceiver<P> {

    }

    non-sealed interface PlayC2S<P extends CustomPacketPayload> extends Packet<P>, ServerPlayPacketReceiver<P> {

    }

    non-sealed interface PlayS2C<P extends CustomPacketPayload> extends Packet<P>, ClientPlayPacketReceiver<P> {

    }

}
