package mcp.mobius.waila.util;

import java.nio.file.Path;

import mcp.mobius.waila.api.WailaConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.MessageFactory;
import org.apache.logging.log4j.message.ParameterizedMessageFactory;
import net.minecraft.resources.ResourceLocation;

public final class CommonUtil {

    public static final Logger LOGGER = LogManager.getLogger(WailaConstants.MOD_NAME, new MessageFactory() {
        private final String prefix = "[" + WailaConstants.MOD_NAME + "] ";

        public Message newMessage(Object message) {
            return ParameterizedMessageFactory.INSTANCE.newMessage(prefix + "{}", message);
        }

        @Override
        public Message newMessage(String message) {
            return ParameterizedMessageFactory.INSTANCE.newMessage(prefix + message);
        }

        @Override
        public Message newMessage(String message, Object... params) {
            return ParameterizedMessageFactory.INSTANCE.newMessage(prefix + message, params);
        }

    });

    public static Path gameDir;
    public static Path configDir;

    public static ResourceLocation id(String path) {
        return new ResourceLocation(WailaConstants.NAMESPACE, path);
    }

}
