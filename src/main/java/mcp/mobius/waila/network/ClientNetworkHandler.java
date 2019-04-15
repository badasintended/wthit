package mcp.mobius.waila.network;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.impl.DataAccessor;
import mcp.mobius.waila.api.impl.config.PluginConfig;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;

import java.util.Map;

public class ClientNetworkHandler {

    public static final Identifier RECEIVE_DATA = new Identifier(Waila.MODID, "receive_data");
    public static final Identifier GET_CONFIG = new Identifier(Waila.MODID, "send_config");

    public static void init() {
        ClientSidePacketRegistry.INSTANCE.register(ClientNetworkHandler.RECEIVE_DATA, (packetContext, packetByteBuf) -> {
            CompoundTag tag = packetByteBuf.readCompoundTag();
            packetContext.getTaskQueue().execute(() -> {
                DataAccessor.INSTANCE.setServerData(tag);
            });
        });

        ClientSidePacketRegistry.INSTANCE.register(ClientNetworkHandler.GET_CONFIG, (packetContext, packetByteBuf) -> {
            int size = packetByteBuf.readInt();
            Map<Identifier, Boolean> temp = Maps.newHashMap();
            for (int i = 0; i < size; i++) {
                int idLength = packetByteBuf.readInt();
                Identifier id = new Identifier(packetByteBuf.readString(idLength));
                boolean value = packetByteBuf.readBoolean();
                temp.put(id, value);
            }

            packetContext.getTaskQueue().execute(() -> {
                temp.forEach(PluginConfig.INSTANCE::set);
                Waila.LOGGER.info("Received config from the server: {}", new Gson().toJson(temp));
            });
        });
    }
}
