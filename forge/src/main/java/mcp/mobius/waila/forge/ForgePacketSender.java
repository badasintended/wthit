package mcp.mobius.waila.forge;

import java.util.Map;
import javax.annotation.Nullable;

import mcp.mobius.waila.config.PluginConfig;
import mcp.mobius.waila.network.PacketExecutor;
import mcp.mobius.waila.network.PacketSender;
import mcp.mobius.waila.util.CommonUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fmllegacy.network.NetworkRegistry;
import net.minecraftforge.fmllegacy.network.PacketDistributor;
import net.minecraftforge.fmllegacy.network.simple.SimpleChannel;

import static mcp.mobius.waila.network.PacketIo.ReceiveData;
import static mcp.mobius.waila.network.PacketIo.RequestBlock;
import static mcp.mobius.waila.network.PacketIo.RequestEntity;
import static mcp.mobius.waila.network.PacketIo.SendConfig;

public class ForgePacketSender extends PacketSender {

    static final String PROTOCOL = "1.0.0";
    static final SimpleChannel NETWORK = NetworkRegistry.newSimpleChannel(
        CommonUtil.id("networking"),
        () -> PROTOCOL, p -> true, p -> true
    );

    @Override
    public void initMain() {
        int i = 0;

        // @formatter:off
        NETWORK.registerMessage(i++, ReceiveData.class,
            (msg, buf) -> ReceiveData.write(buf, msg.tag),
            (     buf) -> ReceiveData.apply(buf, ReceiveData::new),
            (msg, ctx) -> {
                ctx.get().enqueueWork(() -> PacketExecutor.receiveData(msg.tag));
                ctx.get().setPacketHandled(true);
            });

        NETWORK.registerMessage(i++, SendConfig.class,
            (msg, buf) -> SendConfig.write(buf, msg.config),
            (     buf) -> SendConfig.apply(buf, SendConfig::new),
            (msg, ctx) -> {
                ctx.get().enqueueWork(() -> PacketExecutor.sendConfig(msg.forcedKeys));
                ctx.get().setPacketHandled(true);
            });

        NETWORK.registerMessage(i++, RequestEntity.class,
            (msg, buf) -> RequestEntity.write(buf, msg.entity),
            (     buf) -> RequestEntity.apply(buf, RequestEntity::new),
            (msg, ctx) -> {
                ctx.get().enqueueWork(() -> {
                    ServerPlayer player = ctx.get().getSender();
                    PacketExecutor.requestEntity(player, msg.entityId, tag ->
                        NETWORK.send(PacketDistributor.PLAYER.with(() -> player), new ReceiveData(tag)));
                });
                ctx.get().setPacketHandled(true);
            });

        NETWORK.registerMessage(i++, RequestBlock.class,
            (msg, buf) -> RequestBlock.write(buf, msg.blockEntity),
            (     buf) -> RequestBlock.apply(buf, RequestBlock::new),
            (msg, ctx) -> {
                ctx.get().enqueueWork(() -> {
                    ServerPlayer player = ctx.get().getSender();
                    PacketExecutor.requestBlockEntity(player, msg.pos, tag ->
                        NETWORK.send(PacketDistributor.PLAYER.with(() -> player), new ReceiveData(tag)));
                });
                ctx.get().setPacketHandled(true);
            }
        );
        // @formatter:on
    }

    @Override
    public void sendConfig(PluginConfig config, ServerPlayer player) {
        NETWORK.send(PacketDistributor.PLAYER.with(() -> player), new SendConfig(config));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean isServerAvailable() {
        return NETWORK.isRemotePresent(Minecraft.getInstance().getConnection().getConnection());
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void requestEntity(Entity entity) {
        ForgePacketSender.NETWORK.sendToServer(new RequestEntity(entity));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void requestBlock(BlockEntity blockEntity) {
        ForgePacketSender.NETWORK.sendToServer(new RequestBlock(blockEntity));
    }

    public static class SendConfig {

        Map<ResourceLocation, Boolean> forcedKeys;
        PluginConfig config;

        SendConfig(@Nullable Map<ResourceLocation, Boolean> forcedKeys) {
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

    public static class RequestBlock {

        BlockPos pos;
        BlockEntity blockEntity;

        RequestBlock(BlockEntity blockEntity) {
            this.blockEntity = blockEntity;
        }

        RequestBlock(BlockPos pos) {
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
