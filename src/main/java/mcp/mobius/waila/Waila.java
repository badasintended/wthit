package mcp.mobius.waila;

import java.nio.file.Path;

import com.google.gson.GsonBuilder;
import mcp.mobius.waila.api.IJsonConfig;
import mcp.mobius.waila.api.WailaConstants;
import mcp.mobius.waila.config.BlacklistConfig;
import mcp.mobius.waila.config.WailaConfig;
import mcp.mobius.waila.service.ICommonService;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.MessageFactory;
import org.apache.logging.log4j.message.ParameterizedMessageFactory;

public abstract class Waila {

    public static final boolean ENABLE_DEBUG_COMMAND = Boolean.getBoolean("waila.debugCommands");

    public static final boolean CLIENT_SIDE = ICommonService.INSTANCE.isClientSide();
    public static final Path GAME_DIR = ICommonService.INSTANCE.getGameDir();
    public static final Path CONFIG_DIR = ICommonService.INSTANCE.getConfigDir();

    public static final TagKey<Block> BLOCK_BLACKLIST_TAG = TagKey.create(Registry.BLOCK_REGISTRY, id("blacklist"));
    public static final TagKey<EntityType<?>> ENTITY_BLACKLIST_TAG = TagKey.create(Registry.ENTITY_TYPE_REGISTRY, id("blacklist"));

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

    public static final IJsonConfig<WailaConfig> CONFIG = IJsonConfig.of(WailaConfig.class)
        .file(WailaConstants.NAMESPACE + "/" + WailaConstants.WAILA)
        .version(WailaConstants.CONFIG_VERSION, WailaConfig::getConfigVersion, WailaConfig::setConfigVersion)
        .gson(new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(WailaConfig.Overlay.Color.class, new WailaConfig.Overlay.Color.Adapter())
            .registerTypeAdapter(ResourceLocation.class, new ResourceLocation.Serializer())
            .create())
        .build();

    public static final IJsonConfig<BlacklistConfig> BLACKLIST_CONFIG = IJsonConfig.of(BlacklistConfig.class)
        .file(WailaConstants.NAMESPACE + "/blacklist")
        .version(BlacklistConfig.VERSION, BlacklistConfig::getConfigVersion, BlacklistConfig::setConfigVersion)
        .gson(new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(BlacklistConfig.class, new BlacklistConfig.Adapter())
            .create())
        .build();

    public static ResourceLocation id(String path) {
        return new ResourceLocation(WailaConstants.NAMESPACE, path);
    }

}
