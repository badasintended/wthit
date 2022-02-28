package mcp.mobius.waila.fabric;

import java.util.concurrent.CompletableFuture;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.WailaConstants;
import mcp.mobius.waila.config.BlacklistConfig;
import mcp.mobius.waila.config.PluginConfig;
import mcp.mobius.waila.network.PacketExecutor;
import mcp.mobius.waila.network.PacketSender;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientLoginNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerLoginConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerLoginNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;

import static mcp.mobius.waila.network.PacketIo.GenerateClientDump;
import static mcp.mobius.waila.network.PacketIo.ReceiveData;
import static mcp.mobius.waila.network.PacketIo.RequestBlock;
import static mcp.mobius.waila.network.PacketIo.RequestEntity;
import static mcp.mobius.waila.network.PacketIo.SendBlacklist;
import static mcp.mobius.waila.network.PacketIo.SendConfig;

public class FabricPacketSender extends PacketSender {

    static final ResourceLocation VERSION_CHECK = Waila.id("version_check");
    static final ResourceLocation REQUEST_ENTITY = Waila.id("request_entity");
    static final ResourceLocation REQUEST_BLOCK = Waila.id("request_tile");
    static final ResourceLocation RECEIVE_DATA = Waila.id("receive_data");
    static final ResourceLocation SEND_CONFIG = Waila.id("send_config");
    static final ResourceLocation SEND_BLACKLIST = Waila.id("send_blacklist");
    static final ResourceLocation GENERATE_CLIENT_DUMP = Waila.id("generate_client_dump");

    @Override
    public void initMain() {
        ServerLoginConnectionEvents.QUERY_START.register((handler, server, sender, synchronizer) ->
            sender.sendPacket(VERSION_CHECK, PacketByteBufs.empty()));
        
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            if (ServerPlayNetworking.canSend(RECEIVE_DATA) && !ServerPlayNetworking.canSend(VERSION_CHECK)) {
                handler.disconnect(new TextComponent("Your " + WailaConstants.MOD_NAME + " client version is outdated!"));
            }
        });

        ServerLoginNetworking.registerGlobalReceiver(VERSION_CHECK, (server, handler, understood, buf, synchronizer, responseSender) -> {
            if (understood) {
                int clientVersion = buf.readVarInt();
                if (clientVersion != NETWORK_VERSION) {
                    server.execute(() -> handler.disconnect(new TextComponent(WailaConstants.MOD_NAME + " network version mismatch! " +
                        "Server version is " + NETWORK_VERSION + " while client version is " + clientVersion)));
                }
            }
        });

        ServerPlayNetworking.registerGlobalReceiver(VERSION_CHECK, (server, player, handler, buf, responseSender) -> {});

        ServerPlayNetworking.registerGlobalReceiver(REQUEST_ENTITY, (server, player, handler, buf, response) ->
            RequestEntity.consume(buf, entityId ->
                server.execute(() -> PacketExecutor.requestEntity(player, entityId, tag ->
                    response.sendPacket(RECEIVE_DATA, ReceiveData.create(tag))))));

        ServerPlayNetworking.registerGlobalReceiver(REQUEST_BLOCK, (server, player, handler, buf, response) ->
            RequestBlock.consume(buf, pos ->
                server.execute(() -> PacketExecutor.requestBlockEntity(player, pos, tag ->
                    response.sendPacket(RECEIVE_DATA, ReceiveData.create(tag))))));
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void initClient() {
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            if (isServerAvailable() && !ClientPlayNetworking.canSend(VERSION_CHECK)) {
                handler.getConnection().disconnect(new TextComponent("Server " + WailaConstants.MOD_NAME + " version is outdated!"));
            }
        });

        ClientLoginNetworking.registerGlobalReceiver(VERSION_CHECK, (client, handler, buf, listenerAdder) -> {
            FriendlyByteBuf versionBuf = PacketByteBufs.create();
            versionBuf.writeVarInt(NETWORK_VERSION);
            return CompletableFuture.completedFuture(versionBuf);
        });
        
        ClientPlayNetworking.registerGlobalReceiver(VERSION_CHECK, (client, handler, buf, response) -> {});

        ClientPlayNetworking.registerGlobalReceiver(RECEIVE_DATA, (client, handler, buf, response) ->
            ReceiveData.consume(buf, tag ->
                client.execute(() -> PacketExecutor.receiveData(tag))));

        ClientPlayNetworking.registerGlobalReceiver(SEND_CONFIG, (client, handler, buf, response) ->
            SendConfig.consume(buf, map ->
                client.execute(() -> PacketExecutor.sendConfig(map))));

        ClientPlayNetworking.registerGlobalReceiver(SEND_BLACKLIST, (client, handler, buf, response) ->
            SendBlacklist.consume(buf, ids ->
                client.execute(() -> PacketExecutor.sendBlacklist(ids))));

        ClientPlayNetworking.registerGlobalReceiver(GENERATE_CLIENT_DUMP, (client, handler, buf, response) ->
            GenerateClientDump.consume(buf, unused ->
                client.execute(PacketExecutor::generateClientDump)));
    }

    @Override
    public void sendPluginConfig(PluginConfig config, ServerPlayer player) {
        ServerPlayNetworking.send(player, SEND_CONFIG, SendConfig.create(config));
    }

    @Override
    public void sendBlacklistConfig(BlacklistConfig config, ServerPlayer player) {
        ServerPlayNetworking.send(player, SEND_BLACKLIST, SendBlacklist.create(config));
    }

    @Override
    public void generateClientDump(ServerPlayer player) {
        ServerPlayNetworking.send(player, GENERATE_CLIENT_DUMP, GenerateClientDump.create(null));
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean isServerAvailable() {
        return ClientPlayNetworking.canSend(REQUEST_ENTITY);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void requestEntity(EntityHitResult hitResult) {
        ClientPlayNetworking.send(REQUEST_ENTITY, RequestEntity.create(hitResult));
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void requestBlock(BlockHitResult hitResult) {
        ClientPlayNetworking.send(REQUEST_BLOCK, RequestBlock.create(hitResult));
    }

}
