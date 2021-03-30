package mcp.mobius.waila.fabric;

import io.netty.buffer.Unpooled;
import mcp.mobius.waila.Waila;
import mcp.mobius.waila.config.PluginConfig;
import mcp.mobius.waila.network.PacketExecutor;
import mcp.mobius.waila.network.PacketReader;
import mcp.mobius.waila.network.PacketSender;
import mcp.mobius.waila.network.PacketWriter;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class FabricPacketSender extends PacketSender {

    static final Identifier REQUEST_ENTITY = Waila.id("request_entity");
    static final Identifier REQUEST_BLOCK = Waila.id("request_tile");
    static final Identifier RECEIVE_DATA = Waila.id("receive_data");
    static final Identifier SEND_CONFIG = Waila.id("send_config");

    @Override
    public void initMain() {
        ServerPlayNetworking.registerGlobalReceiver(REQUEST_ENTITY, (server, player, handler, buf, sender) -> {
            PacketReader.requestEntity(buf, entityId -> {
                server.execute(() -> PacketExecutor.requestEntity(player, entityId, tag ->
                    sender.sendPacket(RECEIVE_DATA, PacketWriter.receiveData(buf(), tag))));
                return null;
            });
        });
        ServerPlayNetworking.registerGlobalReceiver(REQUEST_BLOCK, (server, player, handler, buf, sender) -> {
            PacketReader.requestBlockEntity(buf, pos -> {
                server.execute(() -> PacketExecutor.requestBlockEntity(player, pos, tag ->
                    sender.sendPacket(RECEIVE_DATA, PacketWriter.receiveData(buf(), tag))));
                return null;
            });
        });
    }

    @Override
    public void sendConfig(PluginConfig config, ServerPlayerEntity player) {
        ServerPlayNetworking.send(player, SEND_CONFIG, PacketWriter.sendConfig(buf(), config));
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void initClient() {
        ClientPlayNetworking.registerGlobalReceiver(RECEIVE_DATA, (client, handler, buf, responseSender) -> {
            PacketReader.receiveData(buf, tag -> {
                client.execute(() -> PacketExecutor.receiveData(tag));
                return null;
            });
        });

        ClientPlayNetworking.registerGlobalReceiver(SEND_CONFIG, (client, handler, buf, responseSender) -> {
            PacketReader.sendConfig(buf, map -> {
                client.execute(() -> PacketExecutor.sendConfig(map));
                return null;
            });
        });
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void requestEntity(Entity entity) {
        ClientPlayNetworking.send(REQUEST_ENTITY, PacketWriter.requestEntity(buf(), entity));
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void requestBlock(BlockEntity blockEntity) {
        ClientPlayNetworking.send(REQUEST_BLOCK, PacketWriter.requestBlockEntity(buf(), blockEntity));
    }

    private PacketByteBuf buf() {
        return new PacketByteBuf(Unpooled.buffer());
    }

}
