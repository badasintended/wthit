package mcp.mobius.waila.network;

import java.util.function.Supplier;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.impl.WailaRegistrar;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

public class MessageRequestEntity {

    public int entityId;

    public MessageRequestEntity(Entity entity) {
        this.entityId = entity.getEntityId();
    }

    private MessageRequestEntity(int entityId) {
        this.entityId = entityId;
    }

    public static MessageRequestEntity read(PacketByteBuf buffer) {
        return new MessageRequestEntity(buffer.readInt());
    }

    public static void write(MessageRequestEntity message, PacketByteBuf buffer) {
        buffer.writeInt(message.entityId);
    }

    public static class Handler {

        public static void onMessage(final MessageRequestEntity message, Supplier<NetworkEvent.Context> context) {
            final MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
            if (server == null)
                return;

            server.execute(() -> {
                ServerPlayerEntity player = context.get().getSender();
                World world = player.world;
                Entity entity = world.getEntityById(message.entityId);

                if (entity == null)
                    return;

                CompoundTag tag = new CompoundTag();
                if (WailaRegistrar.INSTANCE.hasNBTEntityProviders(entity)) {
                    WailaRegistrar.INSTANCE.getNBTEntityProviders(entity).values().forEach(l -> l.forEach(p -> p.appendServerData(tag, player, world, (LivingEntity) entity)));
                } else {
                    entity.toTag(tag);
                }

                tag.putInt("WailaEntityID", entity.getEntityId());

                Waila.NETWORK.sendTo(new MessageReceiveData(tag), player.networkHandler.connection, NetworkDirection.PLAY_TO_CLIENT);
            });
            context.get().setPacketHandled(true);
        }

    }

}
