package mcp.mobius.waila;

import java.nio.file.Path;

import com.google.gson.GsonBuilder;
import mcp.mobius.waila.api.IJsonConfig;
import mcp.mobius.waila.api.IPluginInfo;
import mcp.mobius.waila.api.WailaConstants;
import mcp.mobius.waila.api.__internal__.IHarvestService;
import mcp.mobius.waila.config.BlacklistConfig;
import mcp.mobius.waila.config.WailaConfig;
import mcp.mobius.waila.gui.hud.theme.ThemeDefinition;
import mcp.mobius.waila.registry.RegistryFilter;
import mcp.mobius.waila.service.ICommonService;
import mcp.mobius.waila.util.Log;
import mcp.mobius.waila.util.UnsupportedPlatformException;
import net.minecraft.resources.ResourceLocation;

public abstract class Waila {

    private static final Log LOG = Log.create();

    public static final boolean DEV = ICommonService.INSTANCE.isDev();
    public static final boolean CLIENT_SIDE = ICommonService.INSTANCE.getSide().matches(IPluginInfo.Side.CLIENT);
    public static final boolean ENABLE_DEBUG_COMMAND = DEV || Boolean.getBoolean("waila.debugCommands");

    public static final String ISSUE_URL = ICommonService.INSTANCE.getIssueUrl();

    private static final String ALLOW_UNSUPPORTED_PLATFORMS_KEY = "waila.allowUnsupportedPlatforms";
    public static final boolean ALLOW_UNSUPPORTED_PLATFORMS = Boolean.getBoolean(ALLOW_UNSUPPORTED_PLATFORMS_KEY);

    public static final Path GAME_DIR = ICommonService.INSTANCE.getGameDir();
    public static final Path CONFIG_DIR = ICommonService.INSTANCE.getConfigDir();

    public static final IJsonConfig<WailaConfig> CONFIG = IJsonConfig.of(WailaConfig.class)
        .file(WailaConstants.NAMESPACE + "/" + WailaConstants.WAILA)
        .version(WailaConstants.CONFIG_VERSION, WailaConfig::getConfigVersion, WailaConfig::setConfigVersion)
        .json5()
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
        .json5()
        .commenter(() -> BlacklistConfig.COMMENTER)
        .gson(new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(BlacklistConfig.class, new BlacklistConfig.Adapter())
            .create())
        .build();

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(WailaConstants.NAMESPACE, path);
    }

    protected static void onServerStopped() {
        RegistryFilter.attach(null);
    }

    protected static void onTagReload() {
        IHarvestService.INSTANCE.resetCache();
    }

    protected static void unsupportedPlatform(String platformName, String loaderName, String clazz) {
        try {
            Class.forName(clazz);
            var runningPlatformName = ICommonService.INSTANCE.getPlatformName();

            if (ALLOW_UNSUPPORTED_PLATFORMS)
                LOG.warn("Running on unsupported platform {}, you are on your own.", platformName);
            else {
                throw new UnsupportedPlatformException("""
                    %1$s detected.
                    \t\tYou appear to be using the %3$s version of %4$s with %2$s, which is unsupported.
                    \t\tPlease use a version of %4$s that specifically made for %2$s instead.
                    \t\tRun with -D%5$s=true JVM arg if you know what you are doing."""
                    .formatted(loaderName, platformName, runningPlatformName, WailaConstants.MOD_NAME, ALLOW_UNSUPPORTED_PLATFORMS_KEY));
            }
        } catch (ClassNotFoundException e) {
            // no-op
        }
    }

}
