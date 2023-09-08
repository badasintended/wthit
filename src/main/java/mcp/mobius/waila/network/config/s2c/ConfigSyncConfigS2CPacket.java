package mcp.mobius.waila.network.config.s2c;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import lol.bai.badpackets.api.PacketSender;
import mcp.mobius.waila.Waila;
import mcp.mobius.waila.config.PluginConfig;
import mcp.mobius.waila.network.Packet;
import mcp.mobius.waila.util.Log;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientConfigurationPacketListenerImpl;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import static mcp.mobius.waila.mcless.network.NetworkConstants.CONFIG_BOOL;
import static mcp.mobius.waila.mcless.network.NetworkConstants.CONFIG_DOUBLE;
import static mcp.mobius.waila.mcless.network.NetworkConstants.CONFIG_INT;
import static mcp.mobius.waila.mcless.network.NetworkConstants.CONFIG_STRING;

public class ConfigSyncConfigS2CPacket implements Packet.ConfigS2C<ConfigSyncConfigS2CPacket.Payload> {

    public static final ResourceLocation ID = Waila.id("config");

    private static final Log LOG = Log.create();
    private static final Gson GSON = new Gson();

    @Override
    public ResourceLocation id() {
        return ID;
    }

    @Override
    public Payload read(FriendlyByteBuf buf) {
        Map<ResourceLocation, Object> map = new HashMap<>();
        var groupSize = buf.readVarInt();
        for (var i = 0; i < groupSize; i++) {
            var namespace = buf.readUtf();
            var groupLen = buf.readVarInt();
            for (var j = 0; j < groupLen; j++) {
                var id = new ResourceLocation(namespace, buf.readUtf());
                var type = buf.readByte();
                switch (type) {
                    case CONFIG_BOOL -> map.put(id, buf.readBoolean());
                    case CONFIG_INT -> map.put(id, buf.readVarInt());
                    case CONFIG_DOUBLE -> map.put(id, buf.readDouble());
                    case CONFIG_STRING -> map.put(id, buf.readUtf());
                }
            }
        }

        return new Payload(map);
    }

    @Override
    public void receive(Minecraft client, ClientConfigurationPacketListenerImpl handler, Payload payload, PacketSender responseSender) {
        var map = payload.map;

        for (var config : PluginConfig.getSyncableConfigs()) {
            var id = config.getId();
            var clientOnlyValue = config.getClientOnlyValue();
            var syncedValue = map.get(id);

            if (clientOnlyValue instanceof Enum<?> e && !(syncedValue instanceof Enum<?>)) {
                syncedValue = Enum.valueOf(e.getDeclaringClass(), (String) map.getOrDefault(id, e.name()));
            }

            if (syncedValue instanceof Double d && clientOnlyValue instanceof Integer) {
                syncedValue = d.intValue();
            }

            config.setServerValue(syncedValue);
        }

        LOG.info("Received config from the server: {}", GSON.toJson(map));
    }

    public record Payload(
         Map<ResourceLocation, Object> map
    ) implements CustomPacketPayload {

        @Override
        public void write(@NotNull FriendlyByteBuf buf) {
            var groups = map.keySet().stream()
                .collect(Collectors.groupingBy(ResourceLocation::getNamespace));

            buf.writeVarInt(groups.size());
            groups.forEach((namespace, entries) -> {
                buf.writeUtf(namespace);
                buf.writeVarInt(entries.size());
                entries.forEach(e -> {
                    buf.writeUtf(e.getPath());
                    var v = map.get(e);
                    if (v instanceof Boolean z) {
                        buf.writeByte(CONFIG_BOOL);
                        buf.writeBoolean(z);
                    } else if (v instanceof Integer i) {
                        buf.writeByte(CONFIG_INT);
                        buf.writeVarInt(i);
                    } else if (v instanceof Double d) {
                        buf.writeByte(CONFIG_DOUBLE);
                        buf.writeDouble(d);
                    } else if (v instanceof String str) {
                        buf.writeByte(CONFIG_STRING);
                        buf.writeUtf(str);
                    } else if (v instanceof Enum<?> en) {
                        buf.writeByte(CONFIG_STRING);
                        buf.writeUtf(en.name());
                    }
                });
            });
        }

        @Override
        public @NotNull ResourceLocation id() {
            return ID;
        }

    }

}
