package mcp.mobius.waila.fabric;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.config.BlacklistConfig;
import mcp.mobius.waila.config.PluginConfig;
import mcp.mobius.waila.network.PacketExecutor;
import mcp.mobius.waila.network.PacketSender;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;

import static mcp.mobius.waila.network.PacketIo.GenerateClientDump;
import static mcp.mobius.waila.network.PacketIo.ReceiveData;
import static mcp.mobius.waila.network.PacketIo.RequestBlock;
import static mcp.mobius.waila.network.PacketIo.RequestEntity;
import static mcp.mobius.waila.network.PacketIo.SendBlacklist;
import static mcp.mobius.waila.network.PacketIo.SendConfig;

public class FabricPacketSender extends PacketSender {

    static final ResourceLocation REQUEST_ENTITY = Waila.id("request_entity");
    static final ResourceLocation REQUEST_BLOCK = Waila.id("request_tile");
    static final ResourceLocation RECEIVE_DATA = Waila.id("receive_data");
    static final ResourceLocation SEND_CONFIG = Waila.id("send_config");
    static final ResourceLocation SEND_BLACKLIST = Waila.id("send_blacklist");
    static final ResourceLocation GENERATE_CLIENT_DUMP = Waila.id("generate_client_dump");

    @Override
    public void initMain() {
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
    public void requestEntity(Entity entity) {
        ClientPlayNetworking.send(REQUEST_ENTITY, RequestEntity.create(entity));
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void requestBlock(BlockEntity blockEntity) {
        ClientPlayNetworking.send(REQUEST_BLOCK, RequestBlock.create(blockEntity));
    }

}
