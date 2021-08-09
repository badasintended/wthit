package mcp.mobius.waila;

import java.nio.file.Path;

import com.google.gson.GsonBuilder;
import mcp.mobius.waila.api.IJsonConfig;
import mcp.mobius.waila.api.IModInfo;
import mcp.mobius.waila.api.IWailaConfig;
import mcp.mobius.waila.api.WailaConstants;
import mcp.mobius.waila.config.JsonConfig;
import mcp.mobius.waila.config.WailaConfig;
import mcp.mobius.waila.impl.ImplFactory;
import mcp.mobius.waila.network.PacketSender;
import mcp.mobius.waila.util.ModInfo;
import mcp.mobius.waila.util.PluginLoader;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.Tag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;

public abstract class Waila {

    public static IJsonConfig<WailaConfig> config;

    public static Tag<Block> blockBlacklist;
    public static Tag<EntityType<?>> entityBlacklist;

    public static PacketSender packet;
    public static PluginLoader pluginLoader;
    public static Path configDir;

    public static boolean clientSide = false;

    protected static void init() {
        config = IJsonConfig.of(WailaConfig.class)
            .file(WailaConstants.NAMESPACE + "/" + WailaConstants.WAILA)
            .version(WailaConstants.CONFIG_VERSION, WailaConfig::getConfigVersion, WailaConfig::setConfigVersion)
            .gson(new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(WailaConfig.Overlay.Color.class, new WailaConfig.Overlay.Color.Adapter())
                .registerTypeAdapter(ResourceLocation.class, new ResourceLocation.Serializer())
                .create())
            .build();
    }

    static {
        ImplFactory.reg(IJsonConfig.Builder0.class, c -> new JsonConfig.Builder<>((Class<?>) c));
        ImplFactory.reg(IModInfo.class, s -> ModInfo.get((String) s));
        ImplFactory.reg(IWailaConfig.class, () -> Waila.config.get());
    }

}
