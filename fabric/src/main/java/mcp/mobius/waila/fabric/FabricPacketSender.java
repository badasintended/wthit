package mcp.mobius.waila.fabric;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.impl.config.PluginConfig;
import mcp.mobius.waila.network.PacketExecutor;
import mcp.mobius.waila.network.PacketSender;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import static mcp.mobius.waila.network.PacketIo.ReceiveData;
import static mcp.mobius.waila.network.PacketIo.RequestBlock;
import static mcp.mobius.waila.network.PacketIo.RequestEntity;
import static mcp.mobius.waila.network.PacketIo.SendConfig;

public class FabricPacketSender extends PacketSender {

    static final Identifier REQUEST_ENTITY = Waila.id("request_entity");
    static final Identifier REQUEST_BLOCK = Waila.id("request_tile");
    static final Identifier RECEIVE_DATA = Waila.id("receive_data");
    static final Identifier SEND_CONFIG = Waila.id("send_config");

    @Override
    public void initMain() {
        ServerPlayNetworking.registerGlobalReceiver(REQUEST_ENTITY, (server, player, handler, buf, sender) ->
            RequestEntity.consume(buf, entityId ->
                server.execute(() -> PacketExecutor.requestEntity(player, entityId, tag ->
                    sender.sendPacket(RECEIVE_DATA, ReceiveData.create(tag))))));

        ServerPlayNetworking.registerGlobalReceiver(REQUEST_BLOCK, (server, player, handler, buf, sender) ->
            RequestBlock.consume(buf, pos ->
                server.execute(() -> PacketExecutor.requestBlockEntity(player, pos, tag ->
                    sender.sendPacket(RECEIVE_DATA, ReceiveData.create(tag))))));
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void initClient() {
        ClientPlayNetworking.registerGlobalReceiver(RECEIVE_DATA, (client, handler, buf, responseSender) ->
            ReceiveData.consume(buf, tag ->
                client.execute(() -> PacketExecutor.receiveData(tag))));

        ClientPlayNetworking.registerGlobalReceiver(SEND_CONFIG, (client, handler, buf, responseSender) ->
            SendConfig.consume(buf, map ->
                client.execute(() -> PacketExecutor.sendConfig(map))));
    }

    @Override
    public void sendConfig(PluginConfig config, ServerPlayerEntity player) {
        ServerPlayNetworking.send(player, SEND_CONFIG, SendConfig.create(config));
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
