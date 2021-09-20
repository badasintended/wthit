package mcp.mobius.waila;

import com.google.gson.GsonBuilder;
import mcp.mobius.waila.api.IJsonConfig;
import mcp.mobius.waila.api.IWailaConfig;
import mcp.mobius.waila.api.WailaConstants;
import mcp.mobius.waila.config.JsonConfig;
import mcp.mobius.waila.config.WailaConfig;
import mcp.mobius.waila.impl.Impl;
import mcp.mobius.waila.network.PacketSender;
import mcp.mobius.waila.plugin.PluginLoader;
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

    public static boolean clientSide = false;

    static {
        Impl.reg(IJsonConfig.Builder0.class, c -> new JsonConfig.Builder<>((Class<?>) c));
        Impl.reg(IWailaConfig.class, () -> Waila.config.get());
    }

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

}
