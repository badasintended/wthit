package mcp.mobius.waila.network.fabric;

import java.util.Map;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.impl.DataAccessor;
import mcp.mobius.waila.api.impl.config.PluginConfig;
import mcp.mobius.waila.network.ClientNetworkHandler;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;

public class ClientNetworkHandlerImpl extends ClientNetworkHandler {

    public static void init() {
        ClientPlayNetworking.registerGlobalReceiver(RECEIVE_DATA, (client, handler, buf, responseSender) -> {
            CompoundTag tag = buf.readCompoundTag();
            client.execute(() -> DataAccessor.INSTANCE.setServerData(tag));
        });

        ClientPlayNetworking.registerGlobalReceiver(GET_CONFIG, (client, handler, buf, responseSender) -> {
            int size = buf.readInt();
            Map<Identifier, Boolean> temp = Maps.newHashMap();
            for (int i = 0; i < size; i++) {
                int idLength = buf.readInt();
                Identifier id = new Identifier(buf.readString(idLength));
                boolean value = buf.readBoolean();
                temp.put(id, value);
            }

            client.execute(() -> {
                temp.forEach(PluginConfig.INSTANCE::set);
                Waila.LOGGER.info("Received config from the server: {}", new Gson().toJson(temp));
            });
        });
    }

}
