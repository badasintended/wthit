package mcp.mobius.waila.network.s2c;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import lol.bai.badpackets.api.PacketSender;
import mcp.mobius.waila.Waila;
import mcp.mobius.waila.config.ConfigEntry;
import mcp.mobius.waila.config.PluginConfig;
import mcp.mobius.waila.network.Packet;
import mcp.mobius.waila.util.Log;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import static mcp.mobius.waila.mcless.network.NetworkConstants.CONFIG_BOOL;
import static mcp.mobius.waila.mcless.network.NetworkConstants.CONFIG_DOUBLE;
import static mcp.mobius.waila.mcless.network.NetworkConstants.CONFIG_INT;
import static mcp.mobius.waila.mcless.network.NetworkConstants.CONFIG_STRING;

public class ConfigSyncS2CPacket implements Packet.S2C<ConfigSyncS2CPacket.Payload> {

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
        int groupSize = buf.readVarInt();
        for (int i = 0; i < groupSize; i++) {
            String namespace = buf.readUtf();
            int groupLen = buf.readVarInt();
            for (int j = 0; j < groupLen; j++) {
                ResourceLocation id = new ResourceLocation(namespace, buf.readUtf());
                byte type = buf.readByte();
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
    public void receive(Minecraft client, ClientPacketListener handler, Payload payload, PacketSender responseSender) {
        Map<ResourceLocation, Object> map = payload.map;

        for (ConfigEntry<Object> config : PluginConfig.getSyncableConfigs()) {
            ResourceLocation id = config.getId();
            Object clientOnlyValue = config.getClientOnlyValue();
            Object syncedValue = clientOnlyValue instanceof Enum<?> e
                ? Enum.valueOf(e.getDeclaringClass(), (String) map.getOrDefault(id, e.name()))
                : map.get(id);

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
            Map<String, List<ConfigEntry<Object>>> groups = PluginConfig.getSyncableConfigs().stream()
                .collect(Collectors.groupingBy(c -> c.getId().getNamespace()));

            buf.writeVarInt(groups.size());
            groups.forEach((namespace, entries) -> {
                buf.writeUtf(namespace);
                buf.writeVarInt(entries.size());
                entries.forEach(e -> {
                    buf.writeUtf(e.getId().getPath());
                    Object v = e.getLocalValue();
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
