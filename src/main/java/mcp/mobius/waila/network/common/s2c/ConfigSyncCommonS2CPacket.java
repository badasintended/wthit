package mcp.mobius.waila.network.common.s2c;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import lol.bai.badpackets.api.config.ConfigPackets;
import lol.bai.badpackets.api.play.PlayPackets;
import mcp.mobius.waila.Waila;
import mcp.mobius.waila.config.ConfigEntry;
import mcp.mobius.waila.config.PluginConfig;
import mcp.mobius.waila.network.Packet;
import mcp.mobius.waila.util.Log;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import static mcp.mobius.waila.mcless.network.NetworkConstants.CONFIG_BOOL;
import static mcp.mobius.waila.mcless.network.NetworkConstants.CONFIG_DOUBLE;
import static mcp.mobius.waila.mcless.network.NetworkConstants.CONFIG_INT;
import static mcp.mobius.waila.mcless.network.NetworkConstants.CONFIG_STRING;

public class ConfigSyncCommonS2CPacket implements Packet {

    private static final Log LOG = Log.create();
    private static final Gson GSON = new Gson();
    public static final CustomPacketPayload.Type<Payload> TYPE = new CustomPacketPayload.Type<>(Waila.id("config"));
    public static final StreamCodec<FriendlyByteBuf, Payload> CODEC = StreamCodec.ofMember((p, buf) -> {
        var groups = p.map.keySet().stream()
            .collect(Collectors.groupingBy(ResourceLocation::getNamespace));

        buf.writeVarInt(groups.size());
        groups.forEach((namespace, entries) -> {
            buf.writeUtf(namespace);
            buf.writeVarInt(entries.size());
            entries.forEach(e -> {
                buf.writeUtf(e.getPath());
                var v = p.map.get(e);
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
    }, buf -> {
        Map<ResourceLocation, Object> map = new HashMap<>();
        var groupSize = buf.readVarInt();
        for (var i = 0; i < groupSize; i++) {
            var namespace = buf.readUtf();
            var groupLen = buf.readVarInt();
            for (var j = 0; j < groupLen; j++) {
                var id = ResourceLocation.fromNamespaceAndPath(namespace, buf.readUtf());
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
    });

    @Override
    public void common() {
        ConfigPackets.registerClientChannel(TYPE, CODEC);
        PlayPackets.registerClientChannel(TYPE, CODEC);
    }

    @Override
    public void client() {
        ConfigPackets.registerClientReceiver(TYPE, (context, payload) -> receive(payload));
        PlayPackets.registerClientReceiver(TYPE, (context, payload) -> receive(payload));
    }

    private static void receive(Payload payload) {
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

        public Payload() {
            this(PluginConfig.getSyncableConfigs().stream()
                .filter(it -> it.getOrigin().isEnabled())
                .collect(Collectors.toMap(ConfigEntry::getId, ConfigEntry::getLocalValue)));
        }

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }

    }

}
