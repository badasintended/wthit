package mcp.mobius.waila.network;

import lol.bai.badpackets.api.PacketReceiver;
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

    void register();

    non-sealed interface C2S<P extends CustomPacketPayload> extends Packet<P>, PacketReceiver.C2S<P> {

        @Override
        default void register() {
            PacketReceiver.registerC2S(id(), this, this);
        }

    }

    non-sealed interface S2C<P extends CustomPacketPayload> extends Packet<P>, PacketReceiver.S2C<P> {

        @Override
        default void register() {
            PacketReceiver.registerS2C(id(), this, this);
        }

    }

    interface Common<P extends CustomPacketPayload> extends C2S<P>, S2C<P> {

        @Override
        default void register() {
            C2S.super.register();
            S2C.super.register();
        }

    }

}
