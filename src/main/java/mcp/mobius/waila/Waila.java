package mcp.mobius.waila;

import java.nio.file.Path;

import com.google.gson.GsonBuilder;
import mcp.mobius.waila.api.IJsonConfig;
import mcp.mobius.waila.api.IPluginInfo;
import mcp.mobius.waila.api.WailaConstants;
import mcp.mobius.waila.config.BlacklistConfig;
import mcp.mobius.waila.config.WailaConfig;
import mcp.mobius.waila.gui.hud.theme.ThemeDefinition;
import mcp.mobius.waila.service.ICommonService;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;

public abstract class Waila {

    public static final boolean DEV = ICommonService.INSTANCE.isDev();
    public static final boolean CLIENT_SIDE = ICommonService.INSTANCE.getSide().matches(IPluginInfo.Side.CLIENT);
    public static final boolean ENABLE_DEBUG_COMMAND = DEV || Boolean.getBoolean("waila.debugCommands");

    public static final Path GAME_DIR = ICommonService.INSTANCE.getGameDir();
    public static final Path CONFIG_DIR = ICommonService.INSTANCE.getConfigDir();

    public static final TagKey<Block> BLOCK_BLACKLIST_TAG = TagKey.create(Registry.BLOCK_REGISTRY, id("blacklist"));
    public static final TagKey<EntityType<?>> ENTITY_BLACKLIST_TAG = TagKey.create(Registry.ENTITY_TYPE_REGISTRY, id("blacklist"));

    public static final IJsonConfig<WailaConfig> CONFIG = IJsonConfig.of(WailaConfig.class)
        .file(WailaConstants.NAMESPACE + "/" + WailaConstants.WAILA)
        .version(WailaConstants.CONFIG_VERSION, WailaConfig::getConfigVersion, WailaConfig::setConfigVersion)
        .gson(new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(WailaConfig.Overlay.Color.class, new WailaConfig.Overlay.Color.Adapter())
            .registerTypeAdapter(ThemeDefinition.class, new ThemeDefinition.Adapter())
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
