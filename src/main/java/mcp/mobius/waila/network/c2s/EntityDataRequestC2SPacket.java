package mcp.mobius.waila.network.c2s;

import lol.bai.badpackets.api.PacketSender;
import mcp.mobius.waila.Waila;
import mcp.mobius.waila.access.DataWriter;
import mcp.mobius.waila.access.ServerAccessor;
import mcp.mobius.waila.api.IDataProvider;
import mcp.mobius.waila.api.IServerAccessor;
import mcp.mobius.waila.network.Packet;
import mcp.mobius.waila.network.s2c.RawDataResponseS2CPacket;
import mcp.mobius.waila.registry.Registrar;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class EntityDataRequestC2SPacket implements Packet.C2S<EntityDataRequestC2SPacket.Payload> {

    public static final ResourceLocation ID = Waila.id("entity");

    @Override
    public ResourceLocation id() {
        return ID;
    }

    @Override
    public Payload read(FriendlyByteBuf buf) {
        return new Payload(buf.readVarInt(), new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble()));
    }

    @Override
    public void receive(MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl handler, Payload payload, PacketSender responseSender) {
        int entityId = payload.entityId;
        Vec3 hitPos = payload.hitPos;

        Registrar registrar = Registrar.INSTANCE;
        Level world = player.level();
        Entity entity = world.getEntity(entityId);

        if (entity == null) {
            return;
        }

        CompoundTag raw = DataWriter.INSTANCE.reset();
        IServerAccessor<Entity> accessor = ServerAccessor.INSTANCE.set(world, player, new EntityHitResult(entity, hitPos), entity);

        for (IDataProvider<Entity> provider : registrar.entityData.get(entity)) {
            DataWriter.INSTANCE.tryAppendData(provider, accessor);
        }

        raw.putInt("WailaEntityID", entity.getId());
        raw.putLong("WailaTime", System.currentTimeMillis());

        responseSender.send(new RawDataResponseS2CPacket.Payload(raw));
        DataWriter.INSTANCE.sendTypedPackets(responseSender, player);
    }

    public record Payload(
        int entityId,
        Vec3 hitPos
    ) implements CustomPacketPayload {

        @Override
        public void write(FriendlyByteBuf buf) {
            buf.writeVarInt(entityId);
            buf.writeDouble(hitPos.x);
            buf.writeDouble(hitPos.y);
            buf.writeDouble(hitPos.z);
        }

        @Override
        public @NotNull ResourceLocation id() {
            return ID;
        }

    }

}
