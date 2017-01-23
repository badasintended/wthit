package mcp.mobius.waila.network;

import com.google.common.collect.Maps;
import io.netty.buffer.ByteBuf;
import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.impl.ConfigHandler;
import mcp.mobius.waila.utils.Constants;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import javax.annotation.Nullable;
import java.util.Map;

public class MessageServerPing implements IMessage {

    public Map<String, Boolean> forcedKeys = Maps.newHashMap();

    public MessageServerPing() {
    }

    public MessageServerPing(@Nullable Map<String, Boolean> forcedKeys) {
        if (forcedKeys != null) {
            this.forcedKeys = forcedKeys;
            return;
        }

        ConfigCategory serverForcing = ConfigHandler.instance().config.getCategory(Constants.CATEGORY_SERVER);
        for (String key : serverForcing.keySet())
            if (serverForcing.get(key).getBoolean(false))
                this.forcedKeys.put(key, ConfigHandler.instance().getConfig(key));
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        int amount = buf.readInt();
        for (int i = 0; i < amount; i++)
            forcedKeys.put(ByteBufUtils.readUTF8String(buf), buf.readBoolean());
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(forcedKeys.size());
        for (Map.Entry<String, Boolean> entry : forcedKeys.entrySet()) {
            ByteBufUtils.writeUTF8String(buf, entry.getKey());
            buf.writeBoolean(entry.getValue());
        }
    }

    public static class Handler implements IMessageHandler<MessageServerPing, IMessage> {

        @Override
        public IMessage onMessage(MessageServerPing message, MessageContext ctx) {
            Waila.LOGGER.info("Received server authentication msg. Remote sync will be activated");
            Waila.instance.serverPresent = true;

            for (String key : message.forcedKeys.keySet())
                Waila.LOGGER.info("Received forced key config {} : {}", key, message.forcedKeys.get(key));

            ConfigHandler.instance().forcedConfigs = message.forcedKeys;
            return null;
        }
    }
}
