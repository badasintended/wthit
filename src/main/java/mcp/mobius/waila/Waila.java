package mcp.mobius.waila;

import com.google.gson.GsonBuilder;
import mcp.mobius.waila.api.IBlacklistConfig;
import mcp.mobius.waila.api.IJsonConfig;
import mcp.mobius.waila.api.IPluginInfo;
import mcp.mobius.waila.api.IWailaConfig;
import mcp.mobius.waila.api.WailaConstants;
import mcp.mobius.waila.config.BlacklistConfig;
import mcp.mobius.waila.config.JsonConfig;
import mcp.mobius.waila.config.WailaConfig;
import mcp.mobius.waila.impl.Impl;
import mcp.mobius.waila.network.PacketSender;
import mcp.mobius.waila.plugin.PluginInfo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.Tag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;

public abstract class Waila {


    public static IJsonConfig<WailaConfig> config;
    public static IJsonConfig<BlacklistConfig> blacklistConfig;

    public static Tag<Block> blockBlacklist;
    public static Tag<EntityType<?>> entityBlacklist;

    public static PacketSender packet;

    public static boolean clientSide = false;

    static {
        initImpl();
    }

    @SuppressWarnings("Convert2MethodRef")
    private static void initImpl() {
        Impl.reg(IJsonConfig.Builder0.class, (Class<?> c) -> new JsonConfig.Builder<>(c));
        Impl.reg(IWailaConfig.class, () -> Waila.config.get());
        Impl.reg(IBlacklistConfig.class, () -> Waila.blacklistConfig.get());
        Impl.reg(IPluginInfo.class, (ResourceLocation i) -> PluginInfo.get(i));
        Impl.reg(IPluginInfo.class, (String s) -> PluginInfo.getAllFromMod(s));
        Impl.reg(IPluginInfo.class, () -> PluginInfo.getAll());
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

        blacklistConfig = IJsonConfig.of(BlacklistConfig.class)
            .file(WailaConstants.NAMESPACE + "/blacklist")
            .version(BlacklistConfig.VERSION, BlacklistConfig::getConfigVersion, BlacklistConfig::setConfigVersion)
            .gson(new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(BlacklistConfig.class, new BlacklistConfig.Adapter())
                .create())
            .build();
    }

}
