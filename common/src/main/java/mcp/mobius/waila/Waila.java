package mcp.mobius.waila;

import java.nio.file.Path;

import com.google.gson.GsonBuilder;
import mcp.mobius.waila.api.IJsonConfig;
import mcp.mobius.waila.api.WailaConstants;
import mcp.mobius.waila.config.WailaConfig;
import mcp.mobius.waila.network.PacketSender;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.Tag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class Waila {

    public static final Logger LOGGER = LogManager.getLogger("Waila");

    public static IJsonConfig<WailaConfig> config;

    public static Tag<Block> blockBlacklist;
    public static Tag<EntityType<?>> entityBlacklist;

    public static PacketSender packet;
    public static PluginLoader pluginLoader;
    public static Path configDir;

    public static boolean clientSide = false;

    public static ResourceLocation id(String path) {
        return new ResourceLocation(WailaConstants.WAILA, path);
    }

    protected static void init() {
        config = IJsonConfig.of(WailaConfig.class)
            .file(WailaConstants.WAILA + "/" + WailaConstants.WAILA)
            .version(WailaConstants.CONFIG_VERSION, WailaConfig::getConfigVersion, WailaConfig::setConfigVersion)
            .gson(new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(WailaConfig.Overlay.Color.class, new WailaConfig.Overlay.Color.Adapter())
                .registerTypeAdapter(ResourceLocation.class, new ResourceLocation.Serializer())
                .create())
            .build();
    }

}
