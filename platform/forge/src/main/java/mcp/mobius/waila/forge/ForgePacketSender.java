package mcp.mobius.waila.forge;

import java.util.Map;
import javax.annotation.Nullable;

import it.unimi.dsi.fastutil.ints.IntObjectPair;
import mcp.mobius.waila.Waila;
import mcp.mobius.waila.config.BlacklistConfig;
import mcp.mobius.waila.config.PluginConfig;
import mcp.mobius.waila.network.PacketExecutor;
import mcp.mobius.waila.network.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import static mcp.mobius.waila.network.PacketIo.GenerateClientDump;
import static mcp.mobius.waila.network.PacketIo.ReceiveData;
import static mcp.mobius.waila.network.PacketIo.RequestBlock;
import static mcp.mobius.waila.network.PacketIo.RequestEntity;
import static mcp.mobius.waila.network.PacketIo.SendBlacklist;
import static mcp.mobius.waila.network.PacketIo.SendConfig;

public class ForgePacketSender extends PacketSender {

    static final String PROTOCOL = "" + NETWORK_VERSION;
    static final SimpleChannel NETWORK = NetworkRegistry.newSimpleChannel(
        Waila.id("networking"),
        () -> PROTOCOL, NetworkRegistry.acceptMissingOr(PROTOCOL), NetworkRegistry.acceptMissingOr(PROTOCOL)
    );

    @SuppressWarnings({"ConstantConditions", "UnusedAssignment"})
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

        NETWORK.registerMessage(i++, GenerateClientDump.class,
            (msg, buf) -> GenerateClientDump.write(buf, null),
            (     buf) -> GenerateClientDump.apply(buf, unused -> new GenerateClientDump()),
            (msg, ctx) -> {
                ctx.get().enqueueWork(PacketExecutor::generateClientDump);
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
            (msg, buf) -> RequestEntity.write(buf, msg.hitResult),
            (     buf) -> RequestEntity.apply(buf, RequestEntity::new),
            (msg, ctx) -> {
                ctx.get().enqueueWork(() -> {
                    ServerPlayer player = ctx.get().getSender();
                    PacketExecutor.requestEntity(player, msg.id2vec, tag ->
                        NETWORK.send(PacketDistributor.PLAYER.with(() -> player), new ReceiveData(tag)));
                });
                ctx.get().setPacketHandled(true);
            });

        NETWORK.registerMessage(i++, RequestBlock.class,
            (msg, buf) -> RequestBlock.write(buf, msg.hitResult),
            (     buf) -> RequestBlock.apply(buf, RequestBlock::new),
            (msg, ctx) -> {
                ctx.get().enqueueWork(() -> {
                    ServerPlayer player = ctx.get().getSender();
                    PacketExecutor.requestBlockEntity(player, msg.hitResult, tag ->
                        NETWORK.send(PacketDistributor.PLAYER.with(() -> player), new ReceiveData(tag)));
                });
                ctx.get().setPacketHandled(true);
            });

        NETWORK.registerMessage(i++, SendBlacklist.class,
            (msg, buf) -> SendBlacklist.write(buf, msg.config),
            (     buf) -> SendBlacklist.apply(buf, SendBlacklist::new),
            (msg, ctx) -> {
                ctx.get().enqueueWork(() -> PacketExecutor.sendBlacklist(msg.ids));
                ctx.get().setPacketHandled(true);
            });
        // @formatter:on
    }

    @Override
    public void sendPluginConfig(PluginConfig config, ServerPlayer player) {
        NETWORK.send(PacketDistributor.PLAYER.with(() -> player), new SendConfig(config));
    }

    @Override
    public void sendBlacklistConfig(BlacklistConfig config, ServerPlayer player) {
        NETWORK.send(PacketDistributor.PLAYER.with(() -> player), new SendBlacklist(config));
    }

    @Override
    public void generateClientDump(ServerPlayer player) {
        NETWORK.send(PacketDistributor.PLAYER.with(() -> player), new GenerateClientDump());
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    @SuppressWarnings("ConstantConditions")
    public boolean isServerAvailable() {
        return NETWORK.isRemotePresent(Minecraft.getInstance().getConnection().getConnection());
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void requestEntity(EntityHitResult hitResult) {
        NETWORK.sendToServer(new RequestEntity(hitResult));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void requestBlock(BlockHitResult hitResult) {
        NETWORK.sendToServer(new RequestBlock(hitResult));
    }

    public static class SendConfig {

        Map<ResourceLocation, Object> forcedKeys;
        PluginConfig config;

        SendConfig(@Nullable Map<ResourceLocation, Object> forcedKeys) {
            this.forcedKeys = forcedKeys;
        }

        SendConfig(PluginConfig config) {
            this.config = config;
        }

    }

    public static class SendBlacklist {

        BlacklistConfig config;
        int[][] ids;

        SendBlacklist(BlacklistConfig config) {
            this.config = config;
        }

        SendBlacklist(int[][] ids) {
            this.ids = ids;
        }

    }

    public static class GenerateClientDump {

    }

    public static class RequestEntity {

        IntObjectPair<Vec3> id2vec;
        EntityHitResult hitResult;

        RequestEntity(EntityHitResult hitResult) {
            this.hitResult = hitResult;
        }

        RequestEntity(IntObjectPair<Vec3> id2vec) {
            this.id2vec = id2vec;
        }

    }

    public static class RequestBlock {

        BlockHitResult hitResult;

        RequestBlock(BlockHitResult hitResult) {
            this.hitResult = hitResult;
        }

    }

    public static class ReceiveData {

        public CompoundTag tag;

        public ReceiveData(CompoundTag tag) {
            this.tag = tag;
        }

    }

}
