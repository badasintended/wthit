package mcp.mobius.waila.network.play.c2s;

import lol.bai.badpackets.api.play.PlayPackets;
import mcp.mobius.waila.Waila;
import mcp.mobius.waila.access.DataReader;
import mcp.mobius.waila.access.DataWriter;
import mcp.mobius.waila.access.ServerAccessor;
import mcp.mobius.waila.api.IDataProvider;
import mcp.mobius.waila.api.IServerAccessor;
import mcp.mobius.waila.config.PluginConfig;
import mcp.mobius.waila.network.Packet;
import mcp.mobius.waila.registry.Registrar;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

public class EntityDataRequestPlayC2SPacket implements Packet {

    public static final CustomPacketPayload.Type<Payload> TYPE = new CustomPacketPayload.Type<>(Waila.id("entity"));
    public static final StreamCodec<FriendlyByteBuf, Payload> CODEC = StreamCodec.composite(
        ByteBufCodecs.VAR_INT, Payload::entityId,
        StreamCodec.composite(
            ByteBufCodecs.DOUBLE, Vec3::x,
            ByteBufCodecs.DOUBLE, Vec3::y,
            ByteBufCodecs.DOUBLE, Vec3::z,
            Vec3::new), Payload::hitPos,
        Payload::new);

    @Override
    public void common() {
        PlayPackets.registerServerChannel(TYPE, CODEC);
        PlayPackets.registerServerReceiver(TYPE, (context, payload) -> {
            var player = context.player();
            var entityId = payload.entityId;
            var hitPos = payload.hitPos;

            var registrar = Registrar.get();
            var world = player.level();
            var entity = world.getEntity(entityId);

            if (entity == null) {
                return;
            }

            var raw = DataWriter.SERVER.reset();
            IServerAccessor<Entity> accessor = ServerAccessor.INSTANCE.set(world, player, new EntityHitResult(entity, hitPos), entity);

            for (var provider : registrar.entityData.get(entity)) {
                DataWriter.SERVER.tryAppend(player, provider.instance(), accessor, PluginConfig.SERVER, IDataProvider::appendData);
            }

            raw.putInt("WailaEntityID", entity.getId());
            raw.putLong("WailaTime", System.currentTimeMillis());

            DataWriter.SERVER.send(context, player);
            DataReader.SERVER.reset(null);
        });
    }

    public record Payload(
        int entityId,
        Vec3 hitPos
    ) implements CustomPacketPayload {

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }

    }

}
