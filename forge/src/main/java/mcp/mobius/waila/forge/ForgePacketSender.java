package mcp.mobius.waila.forge;

import java.util.Map;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.config.PluginConfig;
import mcp.mobius.waila.network.PacketExecutor;
import mcp.mobius.waila.network.PacketReader;
import mcp.mobius.waila.network.PacketSender;
import mcp.mobius.waila.network.PacketWriter;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import org.jetbrains.annotations.Nullable;

public class ForgePacketSender extends PacketSender {

    static final String PROTOCOL = "1.0.0";
    static final SimpleChannel NETWORK = NetworkRegistry.newSimpleChannel(
        Waila.id("networking"),
        () -> PROTOCOL, p -> true, p -> true
    );

    @Override
    public void initMain() {
        int i = 0;

        // @formatter:off
        NETWORK.registerMessage(i++, ReceiveData.class,
            (msg, buf) -> PacketWriter.receiveData(buf, msg.tag),
            (buf     ) -> PacketReader.receiveData(buf, ReceiveData::new),
            (msg, ctx) -> {
                ctx.get().enqueueWork(() -> PacketExecutor.receiveData(msg.tag));
                ctx.get().setPacketHandled(true);
            });

        NETWORK.registerMessage(i++, SendConfig.class,
            (msg, buf) -> PacketWriter.sendConfig(buf, msg.config),
            (buf     ) -> PacketReader.sendConfig(buf, SendConfig::new),
            (msg, ctx) -> {
                ctx.get().enqueueWork(() -> PacketExecutor.sendConfig(msg.forcedKeys));
                ctx.get().setPacketHandled(true);
            });

        NETWORK.registerMessage(i++, RequestEntity.class,
            (msg, buf) -> PacketWriter.requestEntity(buf, msg.entity),
            (buf     ) -> PacketReader.requestEntity(buf, RequestEntity::new),
            (msg, ctx) -> {
                ctx.get().enqueueWork(() -> {
                    ServerPlayerEntity player = ctx.get().getSender();
                    PacketExecutor.requestEntity(player, msg.entityId, tag ->
                        NETWORK.send(PacketDistributor.PLAYER.with(() -> player), new ReceiveData(tag)));
                });
                ctx.get().setPacketHandled(true);
            });

        NETWORK.registerMessage(i++, RequestBlockEntity.class,
            (msg, buf) -> PacketWriter.requestBlockEntity(buf, msg.blockEntity),
            (buf     ) -> PacketReader.requestBlockEntity(buf, RequestBlockEntity::new),
            (msg, ctx) -> {
                ctx.get().enqueueWork(() -> {
                    ServerPlayerEntity player = ctx.get().getSender();
                    PacketExecutor.requestBlockEntity(player, msg.pos, tag ->
                        NETWORK.send(PacketDistributor.PLAYER.with(() -> player), new ReceiveData(tag)));
                });
                ctx.get().setPacketHandled(true);
            }
        );
        // @formatter:on
    }

    @Override
    public void sendConfig(PluginConfig config, ServerPlayerEntity player) {
        NETWORK.send(PacketDistributor.PLAYER.with(() -> player), new SendConfig(config));
    }

    @Override
    public void requestEntity(Entity entity) {
        ForgePacketSender.NETWORK.sendToServer(new RequestEntity(entity));
    }

    @Override
    public void requestBlock(BlockEntity blockEntity) {
        ForgePacketSender.NETWORK.sendToServer(new RequestBlockEntity(blockEntity));
    }

    public static class SendConfig {

        Map<Identifier, Boolean> forcedKeys;
        PluginConfig config;

        SendConfig(@Nullable Map<Identifier, Boolean> forcedKeys) {
            this.forcedKeys = forcedKeys;
        }

        SendConfig(PluginConfig config) {
            this.config = config;
        }

    }

    public static class RequestEntity {

        int entityId;
        Entity entity;

        RequestEntity(Entity entity) {
            this.entity = entity;
        }

        RequestEntity(int entityId) {
            this.entityId = entityId;
        }

    }

    public static class RequestBlockEntity {

        BlockPos pos;
        BlockEntity blockEntity;

        RequestBlockEntity(BlockEntity blockEntity) {
            this.blockEntity = blockEntity;
        }

        RequestBlockEntity(BlockPos pos) {
            this.pos = pos;
        }

    }

    public static class ReceiveData {

        public CompoundTag tag;

        public ReceiveData(CompoundTag tag) {
            this.tag = tag;
        }

    }

}
