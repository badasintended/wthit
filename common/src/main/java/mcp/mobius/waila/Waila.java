package mcp.mobius.waila;

import java.nio.file.Path;

import com.google.gson.GsonBuilder;
import mcp.mobius.waila.api.IJsonConfig;
import mcp.mobius.waila.api.WailaConstants;
import mcp.mobius.waila.config.WailaConfig;
import mcp.mobius.waila.network.PacketSender;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class Waila {

    public static final String MODID = WailaConstants.WAILA;
    public static final String NAME = WailaConstants.MOD_NAME;
    public static final Logger LOGGER = LogManager.getLogger("Waila");

    public static IJsonConfig<WailaConfig> config;

    public static Tag<Block> blockBlacklist;
    public static Tag<EntityType<?>> entityBlacklist;

    public static PacketSender packet;
    public static WailaPlugins plugins;
    public static Path configDir;

    public static Identifier id(String path) {
        return new Identifier(WailaConstants.WAILA, path);
    }

    protected static void init() {
        config = IJsonConfig.of(WailaConfig.class)
            .file(WailaConstants.WAILA + "/" + WailaConstants.WAILA)
            .version(WailaConstants.CONFIG_VERSION, WailaConfig::getConfigVersion, WailaConfig::setConfigVersion)
            .gson(new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(WailaConfig.ConfigOverlay.ConfigOverlayColor.class, new WailaConfig.ConfigOverlay.ConfigOverlayColor.Adapter())
                .registerTypeAdapter(Identifier.class, new Identifier.Serializer())
                .create())
            .build();
    }

}