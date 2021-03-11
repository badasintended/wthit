package mcp.mobius.waila.network;

import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import javax.annotation.Nullable;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.impl.config.ConfigEntry;
import mcp.mobius.waila.api.impl.config.PluginConfig;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraftforge.fml.network.NetworkEvent;

public class MessageServerPing {

    public Map<Identifier, Boolean> forcedKeys = Maps.newHashMap();

    public MessageServerPing(@Nullable Map<Identifier, Boolean> forcedKeys) {
        this.forcedKeys = forcedKeys;
    }

    public MessageServerPing(PluginConfig config) {
        Set<ConfigEntry> entries = config.getSyncableConfigs();
        entries.forEach(e -> forcedKeys.put(e.getId(), e.getValue()));
    }

    public static MessageServerPing read(PacketByteBuf buffer) {
        int size = buffer.readInt();
        Map<Identifier, Boolean> temp = Maps.newHashMap();
        for (int i = 0; i < size; i++) {
            int idLength = buffer.readInt();
            Identifier id = new Identifier(buffer.readString(idLength));
            boolean value = buffer.readBoolean();
            temp.put(id, value);
        }

        return new MessageServerPing(temp);
    }

    public static void write(MessageServerPing message, PacketByteBuf buffer) {
        buffer.writeInt(message.forcedKeys.size());
        message.forcedKeys.forEach((k, v) -> {
            buffer.writeInt(k.toString().length());
            buffer.writeString(k.toString());
            buffer.writeBoolean(v);
        });
    }

    public static class Handler {

        public static void onMessage(MessageServerPing message, Supplier<NetworkEvent.Context> context) {
            context.get().enqueueWork(() -> {
                message.forcedKeys.forEach(PluginConfig.INSTANCE::set);
                Waila.LOGGER.info("Received config from the server: {}", new Gson().toJson(message.forcedKeys));
            });
            context.get().setPacketHandled(true);
        }

    }

}
