package mcp.mobius.waila.network;

import java.util.function.Supplier;

import mcp.mobius.waila.api.impl.DataAccessor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraftforge.fml.network.NetworkEvent;

public class MessageReceiveData {

    public CompoundTag tag;

    public MessageReceiveData(CompoundTag tag) {
        this.tag = tag;
    }

    public static MessageReceiveData read(PacketByteBuf buffer) {
        return new MessageReceiveData(buffer.readCompoundTag());
    }

    public static void write(MessageReceiveData message, PacketByteBuf buffer) {
        buffer.writeCompoundTag(message.tag);
    }

    public static class Handler {

        public static void onMessage(MessageReceiveData message, Supplier<NetworkEvent.Context> context) {
            context.get().enqueueWork(() -> {
                DataAccessor.INSTANCE.setServerData(message.tag);
            });
            context.get().setPacketHandled(true);
        }

    }

}
